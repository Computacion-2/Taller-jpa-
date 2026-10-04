package edu.co.icesi.finanzas.unit;
import edu.co.icesi.finanzas.entity.*;
import edu.co.icesi.finanzas.entity.keys.*;
import edu.co.icesi.finanzas.entity.enums.Estado;
import java.time.LocalDate;
public final class Fixtures {
    static Moneda moneda() { Moneda m = new Moneda(); m.setId(1L); return m; }
    static Permiso permiso(long id) { Permiso p = new Permiso(); p.setId(id); p.setNombre("PERMISO"+id); p.setEstado(Estado.ACTIVO); return p; }
    static Rol rol(long id) {
        Rol r = new Rol(); r.setId(id); r.setNombre("ROL"+id); r.setEstado(Estado.ACTIVO);
        RolPermiso rp = new RolPermiso(); rp.setId(new RolPermisoId(id,1L)); rp.setRol(r); rp.setPermiso(permiso(1L)); rp.setFechaAsignacion(LocalDate.now());
        r.getPermisos().add(rp); return r;
    }
    static Usuario usuario() {
        Usuario u = new Usuario(); u.setId(1L); u.setCorreo("ana@u.icesi.edu.co"); u.setNombreCompleto("Ana");
        u.setMoneda(moneda()); u.setEstado(Estado.ACTIVO); u.setFechaRegistro(LocalDate.now()); u.setPasswordHash("hash");
        UsuarioRol ur = new UsuarioRol(); ur.setId(new UsuarioRolId(1L,1L)); ur.setUsuario(u); ur.setRol(rol(1L)); ur.setFechaAsignacion(LocalDate.now());
        u.getRoles().add(ur); return u;
    }
}
