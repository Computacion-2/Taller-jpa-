package edu.co.icesi.finanzas.dto;
import edu.co.icesi.finanzas.entity.*;
import java.util.stream.Collectors;
public final class Views {
    private Views() {}
    public static PermisoView permiso(Permiso p) {
        return new PermisoView(p.getId(), p.getNombre(), p.getDescripcion(), p.getEstado());
    }
    public static RolView rol(Rol r) {
        return new RolView(r.getId(), r.getNombre(), r.getDescripcion(), r.getEstado(),
            r.getPermisos().stream().map(x -> x.getPermiso().getId()).collect(Collectors.toSet()));
    }
    public static UsuarioView usuario(Usuario u) {
        return new UsuarioView(u.getId(), u.getNombreCompleto(), u.getCorreo(), u.getFotoUrl(),
            u.getProgramaAcademico(), u.getMoneda().getId(), u.getEstado(), u.getFechaRegistro(),
            u.getFechaActualizacion(), u.getRoles().stream().map(x -> x.getRol().getId()).collect(Collectors.toSet()));
    }
}
