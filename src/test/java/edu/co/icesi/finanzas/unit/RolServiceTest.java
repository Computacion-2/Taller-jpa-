package edu.co.icesi.finanzas.unit;

import edu.co.icesi.finanzas.dto.RolRequest;
import edu.co.icesi.finanzas.dto.RolView;
import edu.co.icesi.finanzas.entity.Permiso;
import edu.co.icesi.finanzas.entity.Rol;
import edu.co.icesi.finanzas.entity.RolPermiso;
import edu.co.icesi.finanzas.entity.enums.Estado;
import edu.co.icesi.finanzas.exception.BusinessException;
import edu.co.icesi.finanzas.repo.PermisoRepository;
import edu.co.icesi.finanzas.repo.RolPermisoRepository;
import edu.co.icesi.finanzas.repo.RolRepository;
import edu.co.icesi.finanzas.repo.UsuarioRolRepository;
import edu.co.icesi.finanzas.service.impl.RolServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {
    @Mock RolRepository roles;
    @Mock PermisoRepository permisos;
    @Mock RolPermisoRepository asociaciones;
    @Mock UsuarioRolRepository usuarioRoles;
    @InjectMocks RolServiceImpl service;

    @Test
    void listarYObtener() {
        Rol rol = new Rol();
        rol.setId(10L);
        rol.setNombre("ADMINISTRADOR");
        rol.setEstado(Estado.ACTIVO);
        when(roles.findAll()).thenReturn(List.of(rol));
        when(roles.findById(10L)).thenReturn(Optional.of(rol));

        assertEquals(1, service.listar().size());
        assertEquals(10L, service.obtener(10L).id());
    }

    @Test
    void creaConPermisosYNormalizaNombre() {
        RolRequest request = new RolRequest("  Auditor  ", "Audita", Estado.ACTIVO, Set.of(3L, 4L));
        Permiso permiso1 = new Permiso(); permiso1.setId(3L); permiso1.setNombre("P1"); permiso1.setEstado(Estado.ACTIVO);
        Permiso permiso2 = new Permiso(); permiso2.setId(4L); permiso2.setNombre("P2"); permiso2.setEstado(Estado.ACTIVO);
        when(roles.existsByNombreIgnoreCaseAndIdNot("Auditor", -1L)).thenReturn(false);
        when(permisos.findAllById(Set.of(3L, 4L))).thenReturn(List.of(permiso1, permiso2));
        when(permisos.findById(3L)).thenReturn(Optional.of(permiso1));
        when(permisos.findById(4L)).thenReturn(Optional.of(permiso2));
        when(roles.save(any(Rol.class))).thenAnswer(inv -> {
            Rol rol = inv.getArgument(0);
            rol.setId(7L);
            return rol;
        });

        RolView view = service.crear(request);

        assertEquals("Auditor", view.nombre());
        assertEquals(2, view.permisoIds().size());
        verify(roles, times(2)).save(any(Rol.class));
    }

    @Test
    void rechazaDuplicado() {
        RolRequest request = new RolRequest("ADMINISTRADOR", "desc", Estado.ACTIVO, Set.of(1L));
        when(roles.existsByNombreIgnoreCaseAndIdNot("ADMINISTRADOR", -1L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.crear(request));
        verify(roles, never()).save(any());
    }

    @Test
    void rechazaSinPermisos() {
        RolRequest request = new RolRequest("NUEVO", "desc", Estado.ACTIVO, Set.of());
        assertThrows(BusinessException.class, () -> service.crear(request));
        verify(roles, never()).save(any());
    }

    @Test
    void actualizaPermisosYNombre() {
        Rol rol = new Rol();
        rol.setId(5L);
        rol.setNombre("VIEJO");
        rol.setEstado(Estado.INACTIVO);
        rol.setDescripcion("viejo");

        Permiso nuevo = new Permiso();
        nuevo.setId(9L);
        nuevo.setNombre("NUEVO");
        nuevo.setEstado(Estado.ACTIVO);

        RolPermiso existente = new RolPermiso();
        existente.setId(new edu.co.icesi.finanzas.entity.keys.RolPermisoId(5L, 1L));
        existente.setRol(rol);
        Permiso base = new Permiso();
        base.setId(1L);
        base.setNombre("BASE");
        base.setEstado(Estado.ACTIVO);
        existente.setPermiso(base);
        rol.getPermisos().add(existente);

        when(roles.findLockedById(5L)).thenReturn(Optional.of(rol));
        when(roles.existsByNombreIgnoreCaseAndIdNot("NUEVO", 5L)).thenReturn(false);
        when(permisos.findAllById(Set.of(9L))).thenReturn(List.of(nuevo));
        when(permisos.findById(9L)).thenReturn(Optional.of(nuevo));
        when(roles.save(any(Rol.class))).thenAnswer(inv -> inv.getArgument(0));

        RolView view = service.actualizar(5L, new RolRequest(" NUEVO ", "nuevo", Estado.ACTIVO, Set.of(9L)));

        assertEquals("NUEVO", view.nombre());
        assertEquals(Set.of(9L), view.permisoIds());
    }

    @Test
    void eliminaCuandoNoHayUsuariosAsignados() {
        Rol rol = new Rol();
        rol.setId(8L);
        when(roles.findLockedById(8L)).thenReturn(Optional.of(rol));
        when(usuarioRoles.existsByRol_Id(8L)).thenReturn(false);

        service.eliminar(8L);

        verify(roles).delete(rol);
    }

    @Test
    void noEliminaRolEnUso() {
        Rol rol = new Rol();
        rol.setId(9L);
        when(roles.findLockedById(9L)).thenReturn(Optional.of(rol));
        when(usuarioRoles.existsByRol_Id(9L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.eliminar(9L));
        verify(roles, never()).delete(any());
    }

    @Test
    void faltanPermisosYRoles() {
        when(roles.findById(11L)).thenReturn(Optional.empty());
        when(roles.findLockedById(1L)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> service.obtener(11L));
        assertThrows(BusinessException.class, () -> service.actualizar(1L, new RolRequest("X", "Y", Estado.ACTIVO, Set.of(99L))));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void rechazaNombreNuloOVacio(String nombre) {
        assertThrows(BusinessException.class,
                () -> service.crear(new RolRequest(nombre, null, Estado.ACTIVO, Set.of(1L))));
        verify(roles, never()).save(any());
    }

    @Test
    void rechazaActualizacionDuplicadaYPermisosVaciosOInexistentes() {
        Rol rol = new Rol();
        rol.setId(5L);
        when(roles.findLockedById(5L)).thenReturn(Optional.of(rol));
        when(roles.existsByNombreIgnoreCaseAndIdNot("DUPLICADO", 5L)).thenReturn(true);
        assertThrows(BusinessException.class, () -> service.actualizar(5L,
                new RolRequest("DUPLICADO", null, Estado.ACTIVO, Set.of(1L))));

        when(roles.existsByNombreIgnoreCaseAndIdNot("VACIO", 5L)).thenReturn(false);
        assertThrows(BusinessException.class, () -> service.actualizar(5L,
                new RolRequest("VACIO", null, Estado.ACTIVO, Set.of())));

        when(roles.existsByNombreIgnoreCaseAndIdNot("FALTA", 5L)).thenReturn(false);
        when(permisos.findAllById(Set.of(99L))).thenReturn(List.of());
        assertThrows(BusinessException.class, () -> service.actualizar(5L,
                new RolRequest("FALTA", null, Estado.ACTIVO, Set.of(99L))));
        verify(roles, never()).save(any());
    }

    @Test
    void filtraIdsInvalidosYFallaSiPermisoDesapareceAlAsignar() {
        Set<Long> idsInvalidos = new HashSet<>();
        idsInvalidos.add(null);
        idsInvalidos.add(0L);
        idsInvalidos.add(-1L);
        assertThrows(BusinessException.class, () -> service.crear(
                new RolRequest("INVALIDO", null, Estado.ACTIVO, idsInvalidos)));
        assertThrows(BusinessException.class, () -> service.crear(
                new RolRequest("NULO", null, Estado.ACTIVO, null)));

        when(roles.existsByNombreIgnoreCaseAndIdNot("NUEVO", -1L)).thenReturn(false);
        Permiso permiso = new Permiso();
        permiso.setId(12L);
        when(permisos.findAllById(Set.of(12L))).thenReturn(List.of(permiso));
        when(roles.save(any(Rol.class))).thenAnswer(inv -> {
            Rol rol = inv.getArgument(0);
            rol.setId(12L);
            return rol;
        });
        when(permisos.findById(12L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> service.crear(
                new RolRequest("NUEVO", null, Estado.ACTIVO, Set.of(12L))));
    }

    @Test
    void conservaPermisosExistentesYRetiraLosQueYaNoAplican() {
        Rol rol = new Rol();
        rol.setId(20L);
        rol.setNombre("ANTES");
        Permiso conservar = new Permiso();
        conservar.setId(1L);
        RolPermiso asociacionConservar = new RolPermiso();
        asociacionConservar.setRol(rol);
        asociacionConservar.setPermiso(conservar);
        Permiso retirar = new Permiso();
        retirar.setId(2L);
        RolPermiso asociacionRetirar = new RolPermiso();
        asociacionRetirar.setRol(rol);
        asociacionRetirar.setPermiso(retirar);
        rol.getPermisos().addAll(List.of(asociacionConservar, asociacionRetirar));

        when(roles.findLockedById(20L)).thenReturn(Optional.of(rol));
        when(roles.existsByNombreIgnoreCaseAndIdNot("ACTUALIZADO", 20L)).thenReturn(false);
        when(permisos.findAllById(Set.of(1L))).thenReturn(List.of(conservar));
        when(roles.save(any(Rol.class))).thenAnswer(inv -> inv.getArgument(0));

        RolView view = service.actualizar(20L,
                new RolRequest(" ACTUALIZADO ", null, Estado.ACTIVO, Set.of(1L)));

        assertEquals("ACTUALIZADO", view.nombre());
        assertEquals(Set.of(1L), view.permisoIds());
        verify(permisos, never()).findById(any());
    }

    @Test
    void reportaRolYPermisoInexistentesEnOperacionesMutables() {
        when(roles.findLockedById(30L)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> service.eliminar(30L));

        Rol rol = new Rol();
        rol.setId(31L);
        when(roles.findLockedById(31L)).thenReturn(Optional.of(rol));
        when(roles.existsByNombreIgnoreCaseAndIdNot("ACTUALIZADO", 31L)).thenReturn(false);
        Permiso permiso = new Permiso();
        permiso.setId(7L);
        when(permisos.findAllById(Set.of(7L))).thenReturn(List.of(permiso));
        when(permisos.findById(7L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> service.actualizar(31L,
                new RolRequest("ACTUALIZADO", null, Estado.ACTIVO, Set.of(7L))));
    }
}
