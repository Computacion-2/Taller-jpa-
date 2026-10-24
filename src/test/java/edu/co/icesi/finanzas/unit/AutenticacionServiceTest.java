package edu.co.icesi.finanzas.unit;

import edu.co.icesi.finanzas.entity.Permiso;
import edu.co.icesi.finanzas.entity.Rol;
import edu.co.icesi.finanzas.entity.RolPermiso;
import edu.co.icesi.finanzas.entity.UsuarioRol;
import edu.co.icesi.finanzas.entity.enums.Estado;
import edu.co.icesi.finanzas.repo.UsuarioRepository;
import edu.co.icesi.finanzas.security.AutenticacionService;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import static edu.co.icesi.finanzas.unit.Fixtures.usuario;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutenticacionServiceTest {
    @Mock UsuarioRepository usuarios;
    @InjectMocks AutenticacionService service;

    @Test
    void cargaRolesYPermisosActivosYOmiteLosInactivos() {
        var usuario = usuario();
        var activo = usuario.getRoles().get(0).getRol();
        activo.setNombre(" rol1 ");
        var permisoInactivo = new Permiso();
        permisoInactivo.setNombre("INACTIVO");
        permisoInactivo.setEstado(Estado.INACTIVO);
        var asociacion = new RolPermiso();
        asociacion.setPermiso(permisoInactivo);
        activo.getPermisos().add(asociacion);
        var inactivo = new Rol();
        inactivo.setNombre("NO_DEBE_APARECER");
        inactivo.setEstado(Estado.INACTIVO);
        var asignacion = new UsuarioRol();
        asignacion.setRol(inactivo);
        usuario.getRoles().add(asignacion);
        when(usuarios.findForLogin(usuario.getCorreo())).thenReturn(Optional.of(usuario));

        var detalles = service.loadUserByUsername(usuario.getCorreo());

        assertEquals(usuario.getCorreo(), detalles.getUsername());
        assertEquals("hash", detalles.getPassword());
        assertTrue(detalles.isEnabled());
        assertEquals(Set.of("ROLE_ROL1", "PERMISO1"), detalles.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));
    }

    @Test
    void cuentaInactivaQuedaDeshabilitadaYSePermitenColeccionesVacias() {
        var usuario = usuario();
        usuario.setEstado(Estado.INACTIVO);
        usuario.getRoles().get(0).getRol().getPermisos().clear();
        when(usuarios.findForLogin(usuario.getCorreo())).thenReturn(Optional.of(usuario));
        assertFalse(service.loadUserByUsername(usuario.getCorreo()).isEnabled());
        usuario.getRoles().clear();
        assertTrue(service.loadUserByUsername(usuario.getCorreo()).getAuthorities().isEmpty());
    }

    @Test
    void correoDesconocidoRechazaLaAutenticacion() {
        assertThrows(UsernameNotFoundException.class,
            () -> service.loadUserByUsername("no@u.icesi.edu.co"));
    }
}
