package edu.co.icesi.finanzas.unit;

import edu.co.icesi.finanzas.dto.*;
import edu.co.icesi.finanzas.entity.*;
import edu.co.icesi.finanzas.entity.enums.Estado;
import edu.co.icesi.finanzas.entity.keys.UsuarioRolId;
import edu.co.icesi.finanzas.exception.BusinessException;
import edu.co.icesi.finanzas.repo.*;
import edu.co.icesi.finanzas.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock UsuarioRepository usuarios;
    @Mock RolRepository roles;
    @Mock MonedaRepository monedas;
    @Mock UsuarioRolRepository usuarioRoles;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks UsuarioServiceImpl service;

    @Test
    void listarYObtener() {
        Usuario usuario = Fixtures.usuario();
        when(usuarios.buscar(null, null, null, PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(usuario), PageRequest.of(0, 10), 1));
        when(usuarios.findById(1L)).thenReturn(Optional.of(usuario));

        PageView<UsuarioView> page = service.listar(null, null, null, PageRequest.of(0, 10));
        assertEquals(1, page.totalElements());
        assertEquals(1L, service.obtener(1L).id());
    }

    @Test
    void creaUsuarioConRolesYHash() {
        UsuarioCrear request = new UsuarioCrear("Ana", "ana@u.icesi.edu.co", "ClaveSegura1", 1L, Set.of(1L, 2L));
        Moneda moneda = new Moneda();
        moneda.setId(1L);
        Rol rol1 = new Rol(); rol1.setId(1L); rol1.setNombre("ADMINISTRADOR"); rol1.setEstado(Estado.ACTIVO);
        Rol rol2 = new Rol(); rol2.setId(2L); rol2.setNombre("USUARIO"); rol2.setEstado(Estado.ACTIVO);
        when(usuarios.existsByCorreoIgnoreCaseAndIdNot("ana@u.icesi.edu.co", -1L)).thenReturn(false);
        when(monedas.findById(1L)).thenReturn(Optional.of(moneda));
        when(roles.findAllById(Set.of(1L, 2L))).thenReturn(List.of(rol1, rol2));
        when(roles.findById(1L)).thenReturn(Optional.of(rol1));
        when(roles.findById(2L)).thenReturn(Optional.of(rol2));
        when(passwordEncoder.encode("ClaveSegura1")).thenReturn("hash");
        when(usuarios.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(11L);
            return u;
        });

        UsuarioView view = service.crear(request);

        assertEquals("Ana", view.nombreCompleto());
        assertEquals(Set.of(1L, 2L), view.rolIds());
        verify(usuarios, times(2)).save(any(Usuario.class));
    }

    @Test
    void rechazaCorreoDuplicadoYSinRoles() {
        when(usuarios.existsByCorreoIgnoreCaseAndIdNot("ana@u.icesi.edu.co", -1L)).thenReturn(true);
        assertThrows(BusinessException.class, () -> service.crear(new UsuarioCrear("Ana", "ana@u.icesi.edu.co", "ClaveSegura1", 1L, Set.of(1L))));

        assertThrows(BusinessException.class, () -> service.crear(new UsuarioCrear("Ana", "nueva@u.icesi.edu.co", "ClaveSegura1", 1L, Set.of())));
    }

    @Test
    void actualizaDatosSinCambiarPassword() {
        Usuario usuario = Fixtures.usuario();
        usuario.setNombreCompleto("Ana vieja");
        Moneda moneda = new Moneda(); moneda.setId(4L);
        Rol rol1 = new Rol(); rol1.setId(1L); rol1.setNombre("ADMINISTRADOR"); rol1.setEstado(Estado.ACTIVO);
        Rol rol2 = new Rol(); rol2.setId(2L); rol2.setNombre("USUARIO"); rol2.setEstado(Estado.ACTIVO);
        usuario.getRoles().clear();
        UsuarioRol asignacion = new UsuarioRol();
        asignacion.setId(new UsuarioRolId(1L, 1L));
        asignacion.setUsuario(usuario); asignacion.setRol(rol1); asignacion.setFechaAsignacion(LocalDate.now());
        usuario.getRoles().add(asignacion);

        when(usuarios.findLockedById(1L)).thenReturn(Optional.of(usuario));
        when(usuarios.existsByCorreoIgnoreCaseAndIdNot("ana@u.icesi.edu.co", 1L)).thenReturn(false);
        when(monedas.findById(4L)).thenReturn(Optional.of(moneda));
        when(roles.findAllById(Set.of(1L, 2L))).thenReturn(List.of(rol1, rol2));
        when(roles.findById(2L)).thenReturn(Optional.of(rol2));
        when(usuarios.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioView view = service.editar(1L, new UsuarioEditar("Ana nueva", "ana@u.icesi.edu.co", 4L, Estado.ACTIVO, Set.of(1L, 2L)));

        assertEquals("Ana nueva", view.nombreCompleto());
        assertEquals(Set.of(1L, 2L), view.rolIds());
    }

    @Test
    void cambiaEstadoYAdminNoPuedeDesactivarseSiEsElUltimo() {
        Usuario admin = Fixtures.usuario();
        admin.setId(5L);
        admin.setEstado(Estado.ACTIVO);
        Rol rolAdmin = new Rol(); rolAdmin.setId(5L); rolAdmin.setNombre("ADMINISTRADOR"); rolAdmin.setEstado(Estado.ACTIVO);
        UsuarioRol ur = new UsuarioRol(); ur.setId(new UsuarioRolId(5L, 5L)); ur.setUsuario(admin); ur.setRol(rolAdmin); ur.setFechaAsignacion(LocalDate.now());
        admin.getRoles().clear(); admin.getRoles().add(ur);

        when(usuarios.findLockedById(5L)).thenReturn(Optional.of(admin));
        when(usuarios.findAll()).thenReturn(List.of(admin));

        assertThrows(BusinessException.class, () -> service.cambiarEstado(5L, Estado.INACTIVO));
        verify(usuarios, never()).save(any(Usuario.class));
    }

    @Test
    void asignaQuitaRolesYProtegeUltimoRol() {
        Usuario usuario = Fixtures.usuario();
        UsuarioRol actual = usuario.getRoles().get(0);
        Rol rolExtra = new Rol(); rolExtra.setId(99L); rolExtra.setNombre("USUARIO"); rolExtra.setEstado(Estado.ACTIVO);
        when(usuarios.findLockedById(1L)).thenReturn(Optional.of(usuario));
        when(roles.findById(99L)).thenReturn(Optional.of(rolExtra));
        when(usuarios.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        service.asignarRol(1L, 99L);
        verify(usuarios).save(any(Usuario.class));

        Usuario usuario2 = Fixtures.usuario();
        usuario2.getRoles().clear();
        when(usuarios.findLockedById(2L)).thenReturn(Optional.of(usuario2));

        assertThrows(BusinessException.class, () -> service.retirarRol(2L, 1L));
    }

    @Test
    void validaPasswordYEliminaUsuario() {
        Usuario usuario = Fixtures.usuario();
        usuario.setPasswordHash("encoded");
        when(usuarios.findLockedById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("actual", "encoded")).thenReturn(false);
        assertThrows(BusinessException.class, () -> service.cambiarPassword(1L, new CambioPassword("actual", "NuevaSegura1")));

        when(passwordEncoder.matches("correcta", "encoded")).thenReturn(true);
        when(usuarios.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        service.cambiarPassword(1L, new CambioPassword("correcta", "NuevaSegura1"));

        Usuario admin = Fixtures.usuario();
        admin.setId(7L);
        admin.setEstado(Estado.ACTIVO);
        Rol adminRol = new Rol(); adminRol.setId(7L); adminRol.setNombre("ADMINISTRADOR"); adminRol.setEstado(Estado.ACTIVO);
        admin.getRoles().clear();
        UsuarioRol ur = new UsuarioRol(); ur.setId(new UsuarioRolId(7L, 7L)); ur.setUsuario(admin); ur.setRol(adminRol); ur.setFechaAsignacion(LocalDate.now());
        admin.getRoles().add(ur);
        when(usuarios.findLockedById(7L)).thenReturn(Optional.of(admin));
        when(usuarios.findAll()).thenReturn(List.of(admin));
        assertThrows(BusinessException.class, () -> service.eliminar(7L));
    }

    @Test
    void registraUsuarioConDominioIcesi() {
        RegistroRequest request = new RegistroRequest("Pedro", "pedro@u.icesi.edu.co", "ClaveSegura1", 1L);
        Moneda moneda = new Moneda(); moneda.setId(1L);
        Rol rol = new Rol(); rol.setId(12L); rol.setNombre("USUARIO"); rol.setEstado(Estado.ACTIVO);
        when(usuarios.existsByCorreoIgnoreCaseAndIdNot("pedro@u.icesi.edu.co", -1L)).thenReturn(false);
        when(roles.findByNombre("USUARIO")).thenReturn(Optional.of(rol));
        when(monedas.findById(1L)).thenReturn(Optional.of(moneda));
        when(passwordEncoder.encode("ClaveSegura1")).thenReturn("hash");
        when(usuarios.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(20L);
            return u;
        });

        UsuarioView view = service.registrar(request);
        assertEquals("Pedro", view.nombreCompleto());
    }

    @Test
    void normalizaTextoEnBusquedaYFallaSiUsuarioNoExiste() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(usuarios.buscar(null, null, null, pageable)).thenReturn(Page.empty(pageable));
        when(usuarios.buscar("Ana", null, null, pageable)).thenReturn(Page.empty(pageable));

        service.listar("   ", null, null, pageable);
        service.listar(" Ana ", null, null, pageable);
        verify(usuarios).buscar(null, null, null, pageable);
        verify(usuarios).buscar("Ana", null, null, pageable);
        assertThrows(BusinessException.class, () -> service.obtener(99L));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void rechazaCorreoNuloOVacio(String correo) {
        assertThrows(BusinessException.class, () -> service.crear(
                new UsuarioCrear("Ana", correo, "ClaveSegura1", 1L, Set.of(1L))));
        verify(usuarios, never()).save(any());
    }

    @Test
    void rechazaIdsDeRolNulosONoPositivos() {
        Set<Long> idsInvalidos = new HashSet<>();
        idsInvalidos.add(null);
        idsInvalidos.add(0L);
        idsInvalidos.add(-1L);
        assertThrows(BusinessException.class, () -> service.crear(new UsuarioCrear(
                "Ana", "ana@u.icesi.edu.co", "ClaveSegura1", 1L, idsInvalidos)));
        assertThrows(BusinessException.class, () -> service.crear(new UsuarioCrear(
                "Ana", "otra@u.icesi.edu.co", "ClaveSegura1", 1L, null)));
        verify(roles, never()).findAllById(any());
    }

    @Test
    void rechazaRolesInexistentesYMonedaInexistenteAlCrear() {
        when(roles.findAllById(Set.of(1L))).thenReturn(List.of());
        assertThrows(BusinessException.class, () -> service.crear(new UsuarioCrear(
                "Ana", "ana@u.icesi.edu.co", "ClaveSegura1", 1L, Set.of(1L))));
        verify(monedas, never()).findById(any());

        Rol rol = Fixtures.rol(1L);
        when(roles.findAllById(Set.of(2L))).thenReturn(List.of(Fixtures.rol(2L)));
        when(monedas.findById(1L)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> service.crear(new UsuarioCrear(
                "Ana", "otra@u.icesi.edu.co", "ClaveSegura1", 1L, Set.of(2L))));
        verify(usuarios, never()).save(any());
    }

    @Test
    void detectaRolEliminadoEntreValidacionYAsignacion() {
        when(roles.findAllById(Set.of(4L))).thenReturn(List.of(Fixtures.rol(4L)));
        when(monedas.findById(1L)).thenReturn(Optional.of(Fixtures.moneda()));
        when(roles.findById(4L)).thenReturn(Optional.empty());
        when(usuarios.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario usuario = inv.getArgument(0);
            usuario.setId(4L);
            return usuario;
        });

        assertThrows(BusinessException.class, () -> service.crear(new UsuarioCrear(
                "Ana", "ana@u.icesi.edu.co", "ClaveSegura1", 1L, Set.of(4L))));
    }

    @Test
    void editarRechazaUsuarioCorreoRolesYMonedaInexistentes() {
        UsuarioEditar request = new UsuarioEditar("Ana", "ana@u.icesi.edu.co", 1L, Estado.ACTIVO, Set.of(1L));
        assertThrows(BusinessException.class, () -> service.editar(99L, request));

        Usuario usuario = Fixtures.usuario();
        when(usuarios.findLockedById(1L)).thenReturn(Optional.of(usuario));
        when(usuarios.existsByCorreoIgnoreCaseAndIdNot("ana@u.icesi.edu.co", 1L)).thenReturn(true);
        assertThrows(BusinessException.class, () -> service.editar(1L, request));

        when(usuarios.existsByCorreoIgnoreCaseAndIdNot("otra@u.icesi.edu.co", 1L)).thenReturn(false);
        UsuarioEditar sinRoles = new UsuarioEditar("Ana", "otra@u.icesi.edu.co", 1L, Estado.ACTIVO, Set.of());
        assertThrows(BusinessException.class, () -> service.editar(1L, sinRoles));

        when(roles.findAllById(Set.of(2L))).thenReturn(List.of());
        UsuarioEditar rolInexistente = new UsuarioEditar("Ana", "otra@u.icesi.edu.co", 1L, Estado.ACTIVO, Set.of(2L));
        assertThrows(BusinessException.class, () -> service.editar(1L, rolInexistente));
        verify(monedas, never()).findById(any());
    }

    @Test
    void editarRetiraUnRolYConservaElOtro() {
        Usuario usuario = Fixtures.usuario();
        Rol segundoRol = Fixtures.rol(2L);
        UsuarioRol asignacionSegundo = new UsuarioRol();
        asignacionSegundo.setId(new UsuarioRolId(1L, 2L));
        asignacionSegundo.setUsuario(usuario);
        asignacionSegundo.setRol(segundoRol);
        usuario.getRoles().add(asignacionSegundo);
        Moneda moneda = Fixtures.moneda();

        when(usuarios.findLockedById(1L)).thenReturn(Optional.of(usuario));
        when(usuarios.existsByCorreoIgnoreCaseAndIdNot("ana@u.icesi.edu.co", 1L)).thenReturn(false);
        when(roles.findAllById(Set.of(2L))).thenReturn(List.of(segundoRol));
        when(monedas.findById(1L)).thenReturn(Optional.of(moneda));
        when(usuarios.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioView view = service.editar(1L, new UsuarioEditar(
                "Ana", "ana@u.icesi.edu.co", 1L, Estado.ACTIVO, Set.of(2L)));

        assertEquals(Set.of(2L), view.rolIds());
        verify(roles, never()).findById(any());
    }

    @Test
    void cambiarEstadoPermiteRutasQueNoAmenazanAlUltimoAdministrador() {
        Usuario noAdmin = Fixtures.usuario();
        when(usuarios.findLockedById(2L)).thenReturn(Optional.of(noAdmin));
        when(usuarios.findAll()).thenReturn(List.of(usuarioConRol(21L, Estado.ACTIVO, "ADMINISTRADOR")));
        when(usuarios.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals(Estado.INACTIVO, service.cambiarEstado(2L, Estado.INACTIVO).estado());

        Usuario admin = usuarioConRol(3L, Estado.ACTIVO, "ADMINISTRADOR");
        when(usuarios.findLockedById(3L)).thenReturn(Optional.of(admin));
        when(usuarios.findAll()).thenReturn(List.of(admin, usuarioConRol(4L, Estado.ACTIVO, "ADMINISTRADOR")));
        assertEquals(Estado.ACTIVO, service.cambiarEstado(3L, Estado.ACTIVO).estado());

        admin.setEstado(Estado.INACTIVO);
        assertEquals(Estado.ACTIVO, service.cambiarEstado(3L, Estado.ACTIVO).estado());
        admin.setEstado(Estado.ACTIVO);
        assertEquals(Estado.INACTIVO, service.cambiarEstado(3L, Estado.INACTIVO).estado());

        when(usuarios.findLockedById(88L)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> service.cambiarEstado(88L, Estado.INACTIVO));
    }

    @Test
    void asignarRolCubreRolesExistentesYErroresDeBusqueda() {
        Usuario usuario = Fixtures.usuario();
        Rol mismoRol = usuario.getRoles().get(0).getRol();
        when(usuarios.findLockedById(1L)).thenReturn(Optional.of(usuario));
        when(roles.findById(1L)).thenReturn(Optional.of(mismoRol));

        assertEquals(Set.of(1L), service.asignarRol(1L, 1L).rolIds());
        verify(usuarios, never()).save(any());

        when(usuarios.findLockedById(2L)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> service.asignarRol(2L, 1L));
        when(usuarios.findLockedById(3L)).thenReturn(Optional.of(Fixtures.usuario()));
        when(roles.findById(99L)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> service.asignarRol(3L, 99L));
    }

    @Test
    void retirarRolProtegeUltimoYPermiteRetirarUnoDeVarios() {
        Usuario usuario = Fixtures.usuario();
        when(usuarios.findLockedById(1L)).thenReturn(Optional.of(usuario));
        assertThrows(BusinessException.class, () -> service.retirarRol(1L, 1L));

        Usuario varios = Fixtures.usuario();
        UsuarioRol adicional = new UsuarioRol();
        adicional.setId(new UsuarioRolId(1L, 2L));
        adicional.setUsuario(varios);
        adicional.setRol(Fixtures.rol(2L));
        varios.getRoles().add(adicional);
        when(usuarios.findLockedById(2L)).thenReturn(Optional.of(varios));
        service.retirarRol(2L, 1L);
        assertEquals(1, varios.getRoles().size());
        verify(usuarios).save(varios);

        Usuario conReferenciaNula = Fixtures.usuario();
        UsuarioRol referenciaNula = new UsuarioRol();
        referenciaNula.setId(new UsuarioRolId(1L, 3L));
        referenciaNula.setUsuario(conReferenciaNula);
        referenciaNula.setRol(null);
        conReferenciaNula.getRoles().add(referenciaNula);
        when(usuarios.findLockedById(3L)).thenReturn(Optional.of(conReferenciaNula));
        assertThrows(BusinessException.class, () -> service.retirarRol(3L, 1L));

        when(usuarios.findLockedById(4L)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> service.retirarRol(4L, 1L));
    }

    @Test
    void eliminarUsuarioNoAdminYRestablecerPassword() {
        Usuario inactivoAdmin = usuarioConRol(7L, Estado.INACTIVO, "ADMINISTRADOR");
        Usuario activoComun = usuarioConRol(8L, Estado.ACTIVO, "USUARIO");
        when(usuarios.findLockedById(8L)).thenReturn(Optional.of(activoComun));
        when(usuarios.findAll()).thenReturn(List.of(inactivoAdmin, activoComun));
        service.eliminar(8L);
        verify(usuarios).delete(activoComun);

        when(usuarios.findLockedById(9L)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> service.eliminar(9L));

        when(usuarios.findLockedById(10L)).thenReturn(Optional.of(Fixtures.usuario()));
        service.restablecerPassword(10L, new ResetPassword("NuevaSegura1"));
        verify(usuarios).save(any(Usuario.class));
        when(usuarios.findLockedById(11L)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class,
                () -> service.restablecerPassword(11L, new ResetPassword("NuevaSegura1")));
    }

    @Test
    void cambiarPasswordFallaSiNoExisteElUsuario() {
        when(usuarios.findLockedById(12L)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class,
                () -> service.cambiarPassword(12L, new CambioPassword("actual", "nueva")));
    }

    @Test
    void registroRechazaDominioCorreoRolYMonedaInexistentes() {
        assertThrows(BusinessException.class, () -> service.registrar(
                new RegistroRequest("Ana", "ana@example.com", "ClaveSegura1", 1L)));

        when(usuarios.existsByCorreoIgnoreCaseAndIdNot("ana@u.icesi.edu.co", -1L)).thenReturn(true);
        assertThrows(BusinessException.class, () -> service.registrar(
                new RegistroRequest("Ana", "ana@u.icesi.edu.co", "ClaveSegura1", 1L)));

        when(usuarios.existsByCorreoIgnoreCaseAndIdNot("rol@u.icesi.edu.co", -1L)).thenReturn(false);
        when(roles.findByNombre("USUARIO")).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> service.registrar(
                new RegistroRequest("Ana", "rol@u.icesi.edu.co", "ClaveSegura1", 1L)));

        when(usuarios.existsByCorreoIgnoreCaseAndIdNot("moneda@u.icesi.edu.co", -1L)).thenReturn(false);
        when(roles.findByNombre("USUARIO")).thenReturn(Optional.of(Fixtures.rol(1L)));
        when(monedas.findById(1L)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> service.registrar(
                new RegistroRequest("Ana", "moneda@u.icesi.edu.co", "ClaveSegura1", 1L)));
        verify(usuarios, never()).save(any());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void registraCorreoNuloOVacio(String correo) {
        assertThrows(BusinessException.class, () -> service.registrar(
                new RegistroRequest("Ana", correo, "ClaveSegura1", 1L)));
    }

    private Usuario usuarioConRol(Long id, Estado estado, String nombreRol) {
        Usuario usuario = Fixtures.usuario();
        usuario.setId(id);
        usuario.setEstado(estado);
        usuario.getRoles().clear();
        Rol rol = new Rol();
        rol.setId(id);
        rol.setNombre(nombreRol);
        rol.setEstado(Estado.ACTIVO);
        UsuarioRol asignacion = new UsuarioRol();
        asignacion.setId(new UsuarioRolId(id, id));
        asignacion.setUsuario(usuario);
        asignacion.setRol(rol);
        asignacion.setFechaAsignacion(LocalDate.now());
        usuario.getRoles().add(asignacion);
        return usuario;
    }
}
