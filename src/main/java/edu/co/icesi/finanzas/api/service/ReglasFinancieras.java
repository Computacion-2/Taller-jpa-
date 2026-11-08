package edu.co.icesi.finanzas.api.service;

import edu.co.icesi.finanzas.entity.*;
import edu.co.icesi.finanzas.entity.enums.*;
import edu.co.icesi.finanzas.entity.keys.*;
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
        if (entity instanceof Deuda e) {
            if (e.getFechaLimite().isBefore(e.getFechaOrigen())) throw invalid("La fecha limite precede al origen");
            if (e.getContraparteUsuario() == null && (e.getContraparteNombre() == null || e.getContraparteNombre().isBlank())) {
                throw invalid("Debe indicar una contraparte");
            }
            BigDecimal paid = BigDecimal.ZERO;
            if (old instanceof Deuda before) {
                sameUser(before.getUsuario(), e.getUsuario());
                paid = before.getMontoOriginal().subtract(before.getSaldoPendiente());
            }
            if (e.getMontoOriginal().compareTo(paid) < 0) throw conflict("El monto es menor que los abonos existentes");
            e.setSaldoPendiente(e.getMontoOriginal().subtract(paid));
            debtState(e);
        }
        if (entity instanceof AbonoDeuda e) payment((AbonoDeuda) old, e);
        if (entity instanceof PlanAhorro e) {
            if (old instanceof PlanAhorro before) {
                sameUser(before.getPropietario(), e.getPropietario());
                if (before.getTipo() != e.getTipo() && e.getTipo() == TipoPlan.INDIVIDUAL &&
                        em.createQuery("select count(p) from ParticipacionPlan p where p.plan.id = :id and p.usuario.id <> :owner", Long.class)
                            .setParameter("id", e.getId()).setParameter("owner", e.getPropietario().getId()).getSingleResult() > 0) {
                    throw conflict("El plan tiene otros participantes");
                }
            }
            e.setFechaCreacion(old == null ? LocalDate.now() : ((PlanAhorro) old).getFechaCreacion());
        }
        if (entity instanceof AportePlan e) {
            sameUser(e.getUsuario(), e.getCuenta().getUsuario());
            if (!access.member(e.getPlan(), e.getUsuario())) throw invalid("El usuario no participa en el plan");
            if (e.getEstado() == EstadoAporte.ACTIVO) {
                activeAccount(e.getCuenta());
                activePlan(e.getPlan());
            }
            if (old instanceof AportePlan before) sameUser(before.getUsuario(), e.getUsuario());
            e.setFechaCreacion(old == null ? LocalDate.now() : ((AportePlan) old).getFechaCreacion());
        }
        if (entity instanceof ParticipacionPlan e) {
            activePlan(e.getPlan());
            if (e.getPlan().getTipo() == TipoPlan.INDIVIDUAL) sameUser(e.getPlan().getPropietario(), e.getUsuario());
            if (e.getEstado() == EstadoParticipacion.RETIRADO) noActiveContributions(e);
        }
        if (entity instanceof InvitacionPlan e) invitation(e, (InvitacionPlan) old);
    }

    public void beforeDelete(Object entity) {
        if (entity instanceof Movimiento e) movement(e, null);
        if (entity instanceof AbonoDeuda e) payment(e, null);
        if (entity instanceof ParticipacionPlan e) noActiveContributions(e);
        if (entity instanceof InvitacionPlan e && !access.admin() && !access.own(e.getPlan().getPropietario())) {
            throw new org.springframework.security.access.AccessDeniedException("Solo el propietario elimina invitaciones");
        }
        if (entity instanceof RolPermiso e && em.createQuery("select count(r) from RolPermiso r where r.rol.id = :id", Long.class)
                .setParameter("id", e.getRol().getId()).getSingleResult() <= 1) {
            throw conflict("El rol debe conservar al menos un permiso");
        }
        if (entity instanceof UsuarioRol e) {
            if (em.createQuery("select count(r) from UsuarioRol r where r.usuario.id = :id", Long.class)
                    .setParameter("id", e.getUsuario().getId()).getSingleResult() <= 1) {
                throw conflict("El usuario debe conservar al menos un rol");
            }
            if ("ADMINISTRADOR".equalsIgnoreCase(e.getRol().getNombre()) && e.getUsuario().getEstado() == Estado.ACTIVO &&
                    em.createQuery("select count(distinct r.usuario.id) from UsuarioRol r where upper(r.rol.nombre) = 'ADMINISTRADOR' and r.usuario.estado = :estado", Long.class)
                        .setParameter("estado", Estado.ACTIVO).getSingleResult() <= 1) {
                throw conflict("No se puede retirar el ultimo administrador activo");
            }
        }
    }

    private void activePlan(PlanAhorro plan) {
        if (plan.getEstado() != EstadoPlan.ACTIVO) throw conflict("El plan no esta activo");
    }

    private void noActiveContributions(ParticipacionPlan e) {
        if (em.createQuery("select count(a) from AportePlan a where a.plan.id = :plan and a.usuario.id = :user and a.estado = :estado", Long.class)
                .setParameter("plan", e.getPlan().getId()).setParameter("user", e.getUsuario().getId())
                .setParameter("estado", EstadoAporte.ACTIVO).getSingleResult() > 0) {
            throw conflict("Retire los aportes activos antes de retirar la participacion");
        }
    }

    private void debtState(Deuda e) {
        e.setEstado(e.getSaldoPendiente().signum() == 0 ? EstadoDeuda.SALDADA : EstadoDeuda.PENDIENTE);
    }

    private void payment(AbonoDeuda old, AbonoDeuda next) {
        var debts = new TreeMap<Long, Deuda>();
        if (old != null) debts.put(old.getDeuda().getId(), old.getDeuda());
        if (next != null) debts.put(next.getDeuda().getId(), next.getDeuda());
        debts.values().forEach(d -> em.refresh(d, LockModeType.PESSIMISTIC_WRITE));
        if (old != null) old.getDeuda().setSaldoPendiente(old.getDeuda().getSaldoPendiente().add(old.getMonto()));
        if (next != null) {
            access.requireWrite(next.getDeuda());
            if (next.getFechaAbono().isBefore(next.getDeuda().getFechaOrigen())) throw invalid("El abono precede a la deuda");
            if (next.getMonto().compareTo(next.getDeuda().getSaldoPendiente()) > 0) throw invalid("El abono supera el saldo pendiente");
            next.getDeuda().setSaldoPendiente(next.getDeuda().getSaldoPendiente().subtract(next.getMonto()));
        }
        debts.values().forEach(this::debtState);
    }

    private void invitation(InvitacionPlan e, InvitacionPlan old) {
        activePlan(e.getPlan());
        if (e.getPlan().getTipo() != TipoPlan.COMPARTIDO) throw invalid("Solo se invita a planes compartidos");
        if (e.getUsuarioInvitado().getId().equals(e.getPlan().getPropietario().getId())) throw invalid("El propietario ya pertenece al plan");
        if (old == null) {
            if (!access.admin() && !access.own(e.getPlan().getPropietario())) throw new org.springframework.security.access.AccessDeniedException("Solo el propietario invita");
            if (e.getEstado() != EstadoInvitacion.PENDIENTE) throw invalid("La invitacion debe comenzar pendiente");
            e.setUsuarioInvitador(e.getPlan().getPropietario());
            e.setFechaInvitacion(LocalDate.now());
        } else {
            if (!old.getPlan().getId().equals(e.getPlan().getId()) || !old.getUsuarioInvitado().getId().equals(e.getUsuarioInvitado().getId())) throw invalid("No puede cambiar el plan ni el destinatario");
            if (old.getEstado() != e.getEstado()) {
                if (old.getEstado() != EstadoInvitacion.PENDIENTE) throw conflict("La invitacion ya fue respondida");
                if (!access.admin() && !access.own(e.getUsuarioInvitado())) throw new org.springframework.security.access.AccessDeniedException("Solo el destinatario responde");
            }
            e.setUsuarioInvitador(old.getUsuarioInvitador());
            e.setFechaInvitacion(old.getFechaInvitacion());
            e.setFechaRespuesta(e.getEstado() == EstadoInvitacion.PENDIENTE ? null :
                (old.getFechaRespuesta() == null ? LocalDate.now() : old.getFechaRespuesta()));
            if (e.getEstado() == EstadoInvitacion.ACEPTADA) {
                var id = new ParticipacionPlanId(e.getPlan().getId(), e.getUsuarioInvitado().getId());
                if (em.find(ParticipacionPlan.class, id) == null) {
                    var member = new ParticipacionPlan();
                    member.setId(id);
                    member.setPlan(e.getPlan());
                    member.setUsuario(e.getUsuarioInvitado());
                    member.setEstado(EstadoParticipacion.ACTIVO);
                    member.setFechaAceptacion(LocalDate.now());
                    em.persist(member);
                }
            }
        }
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
