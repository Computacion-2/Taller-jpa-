package edu.co.icesi.finanzas.api.service;

import edu.co.icesi.finanzas.entity.*;
import edu.co.icesi.finanzas.entity.enums.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import static edu.co.icesi.finanzas.exception.BusinessException.*;

@Component
@RequiredArgsConstructor
public class ReglasFinancieras {
    private final RestAccess access;
    private final EntityManager em;

    public void prepare(Object entity, Object old) {
        if (entity instanceof Moneda e) e.setCodigo(e.getCodigo().trim().toUpperCase(Locale.ROOT));
        if (entity instanceof Categoria e) {
            if (old instanceof Categoria before && before.getTipo() != e.getTipo()) {
                Long uses = em.createQuery("select count(m) from Movimiento m where m.categoria.id = :id", Long.class)
                    .setParameter("id", before.getId()).getSingleResult();
                Long recurring = em.createQuery("select count(m) from MovimientoRecurrente m where m.categoria.id = :id", Long.class)
                    .setParameter("id", before.getId()).getSingleResult();
                Long budgets = em.createQuery("select count(p) from Presupuesto p where p.categoria.id = :id", Long.class)
                    .setParameter("id", before.getId()).getSingleResult();
                if (uses + recurring + budgets > 0) throw conflict("La categoria tiene registros asociados");
            }
            if (old instanceof Categoria before) sameUser(before.getUsuario(), e.getUsuario());
            e.setFechaCreacion(old == null ? LocalDate.now() : ((Categoria) old).getFechaCreacion());
        }
        if (entity instanceof Cuenta e) {
            if (e.getTipoCuenta().getEstado() != Estado.ACTIVO) throw invalid("Tipo de cuenta inactivo");
            if (old instanceof Cuenta before) {
                em.refresh(before, LockModeType.PESSIMISTIC_WRITE);
                sameUser(before.getUsuario(), e.getUsuario());
                e.setSaldoActual(before.getSaldoActual().add(e.getSaldoInicial()).subtract(before.getSaldoInicial()));
            } else e.setSaldoActual(e.getSaldoInicial());
            e.setFechaCreacion(old == null ? LocalDate.now() : ((Cuenta) old).getFechaCreacion());
            e.setFechaArchivado(e.getEstado() == EstadoCuenta.ARCHIVADA ? LocalDate.now() : null);
        }
        if (entity instanceof Movimiento e) {
            category(e.getCategoria(), e.getCuenta().getUsuario(), e.getTipo());
            activeAccount(e.getCuenta());
            e.setFechaCreacion(old == null ? LocalDate.now() : ((Movimiento) old).getFechaCreacion());
            movement((Movimiento) old, e);
        }
        if (entity instanceof MovimientoRecurrente e) {
            sameUser(e.getUsuario(), e.getCuenta().getUsuario());
            activeAccount(e.getCuenta());
            category(e.getCategoria(), e.getUsuario(), e.getTipo());
            if (e.getProximaOcurrencia().isBefore(e.getFechaInicio()) ||
                    (e.getFechaCorte() != null && (e.getFechaCorte().isBefore(e.getFechaInicio()) || e.getProximaOcurrencia().isAfter(e.getFechaCorte())))) {
                throw invalid("Rango de recurrencia invalido");
            }
            if (old instanceof MovimientoRecurrente before) sameUser(before.getUsuario(), e.getUsuario());
        }
        if (entity instanceof Presupuesto e) {
            category(e.getCategoria(), e.getUsuario(), TipoMovimiento.GASTO);
            e.setFechaCreacion(old == null ? LocalDate.now() : ((Presupuesto) old).getFechaCreacion());
            if (old instanceof Presupuesto before) sameUser(before.getUsuario(), e.getUsuario());
        }
    }

    public void beforeDelete(Object entity) {
        if (entity instanceof Movimiento e) movement(e, null);
    }

    private void sameUser(Usuario a, Usuario b) {
        if (!Objects.equals(a == null ? null : a.getId(), b == null ? null : b.getId())) {
            throw invalid("El propietario no coincide");
        }
    }

    private void activeAccount(Cuenta cuenta) {
        access.requireWrite(cuenta);
        if (cuenta.getEstado() != EstadoCuenta.ACTIVA) throw conflict("La cuenta esta archivada");
    }

    private void category(Categoria categoria, Usuario usuario, TipoMovimiento tipo) {
        if (categoria.getUsuario() != null) sameUser(categoria.getUsuario(), usuario);
        if (categoria.getTipo() != tipo || categoria.getEstado() != Estado.ACTIVO) throw invalid("Categoria incompatible o inactiva");
    }

    private BigDecimal signed(Movimiento e) { return e.getTipo() == TipoMovimiento.INGRESO ? e.getMonto() : e.getMonto().negate(); }

    private void movement(Movimiento old, Movimiento next) {
        var accounts = new TreeMap<Long, Cuenta>();
        if (old != null) accounts.put(old.getCuenta().getId(), old.getCuenta());
        if (next != null) accounts.put(next.getCuenta().getId(), next.getCuenta());
        accounts.values().forEach(c -> em.refresh(c, LockModeType.PESSIMISTIC_WRITE));
        if (old != null) old.getCuenta().setSaldoActual(old.getCuenta().getSaldoActual().subtract(signed(old)));
        if (next != null) next.getCuenta().setSaldoActual(next.getCuenta().getSaldoActual().add(signed(next)));
    }
}
