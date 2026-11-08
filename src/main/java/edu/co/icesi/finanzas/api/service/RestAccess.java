package edu.co.icesi.finanzas.api.service;

import edu.co.icesi.finanzas.entity.*;
import edu.co.icesi.finanzas.entity.enums.EstadoParticipacion;
import edu.co.icesi.finanzas.entity.keys.ParticipacionPlanId;
import edu.co.icesi.finanzas.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import static edu.co.icesi.finanzas.exception.BusinessException.missing;

@Component
@RequiredArgsConstructor
public class RestAccess {
    private final UsuarioRepository usuarios;
    private final ParticipacionPlanRepository participaciones;

    public boolean admin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
    }

    public Long userId() {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarios.findByCorreoIgnoreCase(correo).orElseThrow(() -> missing("Usuario")).getId();
    }

    public boolean own(Usuario user) { return user != null && user.getId().equals(userId()); }

    public boolean member(PlanAhorro plan, Usuario user) {
        return plan.getPropietario().getId().equals(user.getId()) || participaciones
            .findById(new ParticipacionPlanId(plan.getId(), user.getId()))
            .filter(p -> p.getEstado() == EstadoParticipacion.ACTIVO).isPresent();
    }

    public boolean canRead(Object entity) {
        if (admin()) return true;
        if (entity instanceof Moneda || entity instanceof TipoCuenta) return true;
        if (entity instanceof Categoria e) return e.getUsuario() == null || own(e.getUsuario());
        if (entity instanceof Cuenta e) return own(e.getUsuario());
        if (entity instanceof Movimiento e) return own(e.getCuenta().getUsuario());
        if (entity instanceof MovimientoRecurrente e) return own(e.getUsuario());
        if (entity instanceof Presupuesto e) return own(e.getUsuario());
        if (entity instanceof Deuda e) return own(e.getUsuario());
        if (entity instanceof AbonoDeuda e) return own(e.getDeuda().getUsuario());
        if (entity instanceof PlanAhorro e) return own(e.getPropietario()) || participaciones
            .findById(new ParticipacionPlanId(e.getId(), userId()))
            .filter(p -> p.getEstado() == EstadoParticipacion.ACTIVO).isPresent();
        if (entity instanceof AportePlan e) return own(e.getUsuario()) || own(e.getPlan().getPropietario());
        if (entity instanceof InvitacionPlan e) return own(e.getUsuarioInvitado()) || own(e.getPlan().getPropietario());
        if (entity instanceof ParticipacionPlan e) return own(e.getUsuario()) || own(e.getPlan().getPropietario());
        return false;
    }

    public void requireRead(Object entity) {
        if (!canRead(entity)) throw missing("Recurso");
    }

    public void requireWrite(Object entity) {
        if (admin()) return;
        boolean allowed;
        if (entity instanceof Moneda || entity instanceof TipoCuenta || entity instanceof RolPermiso || entity instanceof UsuarioRol) {
            allowed = false;
        } else if (entity instanceof Categoria e) {
            allowed = own(e.getUsuario());
        } else if (entity instanceof PlanAhorro e) {
            allowed = own(e.getPropietario());
        } else if (entity instanceof ParticipacionPlan e) {
            allowed = own(e.getPlan().getPropietario());
        } else if (entity instanceof AportePlan e) {
            allowed = own(e.getUsuario());
        } else {
            allowed = canRead(entity);
        }
        if (!allowed) throw new AccessDeniedException("Recurso no autorizado");
    }
}
