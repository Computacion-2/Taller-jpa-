package edu.co.icesi.finanzas.api.service;
import edu.co.icesi.finanzas.entity.*;
import edu.co.icesi.finanzas.entity.keys.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import static edu.co.icesi.finanzas.exception.BusinessException.missing;

@Component
@RequiredArgsConstructor
public class RestReferences {
    private final EntityManager em;

    public void lock(Object entity) {
        em.refresh(entity, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
    }

    private <T> T reference(Class<T> type, Long id) {
        T entity = id == null ? null : em.find(type, id);
        if (entity == null) throw missing(type.getSimpleName());
        return entity;
    }

    public void resolve(Object entity) {
        if (entity instanceof Categoria e) {
            e.setUsuario(e.getUsuario() == null ? null : reference(Usuario.class, e.getUsuario().getId()));
        }
        if (entity instanceof Cuenta e) {
            e.setUsuario(e.getUsuario() == null ? null : reference(Usuario.class, e.getUsuario().getId()));
            e.setTipoCuenta(e.getTipoCuenta() == null ? null : reference(TipoCuenta.class, e.getTipoCuenta().getId()));
        }
        if (entity instanceof Movimiento e) {
            e.setCuenta(e.getCuenta() == null ? null : reference(Cuenta.class, e.getCuenta().getId()));
            e.setCategoria(e.getCategoria() == null ? null : reference(Categoria.class, e.getCategoria().getId()));
        }
        if (entity instanceof MovimientoRecurrente e) {
            e.setUsuario(e.getUsuario() == null ? null : reference(Usuario.class, e.getUsuario().getId()));
            e.setCuenta(e.getCuenta() == null ? null : reference(Cuenta.class, e.getCuenta().getId()));
            e.setCategoria(e.getCategoria() == null ? null : reference(Categoria.class, e.getCategoria().getId()));
        }
        if (entity instanceof Presupuesto e) {
            e.setUsuario(e.getUsuario() == null ? null : reference(Usuario.class, e.getUsuario().getId()));
            e.setCategoria(e.getCategoria() == null ? null : reference(Categoria.class, e.getCategoria().getId()));
        }
        if (entity instanceof Deuda e) {
            e.setUsuario(e.getUsuario() == null ? null : reference(Usuario.class, e.getUsuario().getId()));
            e.setContraparteUsuario(e.getContraparteUsuario() == null ? null : reference(Usuario.class, e.getContraparteUsuario().getId()));
        }
        if (entity instanceof AbonoDeuda e) {
            e.setDeuda(e.getDeuda() == null ? null : reference(Deuda.class, e.getDeuda().getId()));
        }
        if (entity instanceof PlanAhorro e) {
            e.setPropietario(e.getPropietario() == null ? null : reference(Usuario.class, e.getPropietario().getId()));
        }
        if (entity instanceof InvitacionPlan e) {
            e.setPlan(e.getPlan() == null ? null : reference(PlanAhorro.class, e.getPlan().getId()));
            e.setUsuarioInvitado(e.getUsuarioInvitado() == null ? null : reference(Usuario.class, e.getUsuarioInvitado().getId()));
        }
        if (entity instanceof AportePlan e) {
            e.setPlan(e.getPlan() == null ? null : reference(PlanAhorro.class, e.getPlan().getId()));
            e.setUsuario(e.getUsuario() == null ? null : reference(Usuario.class, e.getUsuario().getId()));
            e.setCuenta(e.getCuenta() == null ? null : reference(Cuenta.class, e.getCuenta().getId()));
        }
        if (entity instanceof ParticipacionPlan e) {
            e.setPlan(e.getPlan() == null ? null : reference(PlanAhorro.class, e.getPlan().getId()));
            e.setUsuario(e.getUsuario() == null ? null : reference(Usuario.class, e.getUsuario().getId()));
            e.setId(new ParticipacionPlanId(e.getPlan().getId(), e.getUsuario().getId()));
        }
        if (entity instanceof UsuarioRol e) {
            e.setUsuario(e.getUsuario() == null ? null : reference(Usuario.class, e.getUsuario().getId()));
            e.setRol(e.getRol() == null ? null : reference(Rol.class, e.getRol().getId()));
            e.setId(new UsuarioRolId(e.getUsuario().getId(), e.getRol().getId()));
        }
        if (entity instanceof RolPermiso e) {
            e.setRol(e.getRol() == null ? null : reference(Rol.class, e.getRol().getId()));
            e.setPermiso(e.getPermiso() == null ? null : reference(Permiso.class, e.getPermiso().getId()));
            e.setId(new RolPermisoId(e.getRol().getId(), e.getPermiso().getId()));
        }
    }
}
