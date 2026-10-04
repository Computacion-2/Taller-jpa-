package edu.co.icesi.finanzas.unit;
import edu.co.icesi.finanzas.dto.*;
import edu.co.icesi.finanzas.entity.*;
import edu.co.icesi.finanzas.entity.enums.Estado;
import edu.co.icesi.finanzas.repo.*;
import edu.co.icesi.finanzas.service.impl.*;
import edu.co.icesi.finanzas.exception.BusinessException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static edu.co.icesi.finanzas.unit.Fixtures.*;
import java.util.*;
@ExtendWith(MockitoExtension.class)
class PermisoServiceTest {
    @Mock PermisoRepository permisos;
    @Mock RolPermisoRepository asociaciones;
    @InjectMocks PermisoServiceImpl service;
    final PermisoRequest request = new PermisoRequest(" Consultar ", "Consulta", Estado.ACTIVO);
    @Test void consultaListaYDetalle() {
        Permiso p = permiso(1); when(permisos.findAll()).thenReturn(List.of(p)); when(permisos.findById(1L)).thenReturn(Optional.of(p));
        assertEquals(1, service.listar().size()); assertEquals(1L, service.obtener(1L).id());
    }
    @Test void creaConNombreNormalizado() {
        when(permisos.save(any())).thenAnswer(i -> { Permiso p=i.getArgument(0); p.setId(3L); return p; });
        assertEquals("Consultar", service.crear(request).nombre());
        verify(permisos).existsByNombreIgnoreCaseAndIdNot("Consultar",-1L);
    }
    @Test void actualizaDatosSinCambiarIdentidad() {
        when(permisos.findLockedById(1L)).thenReturn(Optional.of(permiso(1)));
        when(permisos.save(any())).thenAnswer(i -> i.getArgument(0));
        assertEquals(1L, service.actualizar(1L,request).id());
        verify(permisos).existsByNombreIgnoreCaseAndIdNot("Consultar",1L);
    }
    @Test void rechazaDuplicado() {
        when(permisos.existsByNombreIgnoreCaseAndIdNot("Consultar",-1L)).thenReturn(true);
        assertThrows(BusinessException.class, () -> service.crear(request)); verify(permisos, never()).save(any());
    }
    @Test void eliminaLibre() {
        Permiso p=permiso(1); when(permisos.findLockedById(1L)).thenReturn(Optional.of(p));
        service.eliminar(1L); verify(permisos).delete(p);
    }
    @Test void conservaAsignados() {
        when(permisos.findLockedById(1L)).thenReturn(Optional.of(permiso(1))); when(asociaciones.existsByPermiso_Id(1L)).thenReturn(true);
        assertThrows(BusinessException.class, () -> service.eliminar(1L)); verify(permisos,never()).delete(any());
    }
    @Test void inexistentes() {
        assertThrows(BusinessException.class, () -> service.obtener(9L));
        assertThrows(BusinessException.class, () -> service.actualizar(9L,request));
        assertThrows(BusinessException.class, () -> service.eliminar(9L));
    }
}
