package edu.co.icesi.finanzas.service;
import edu.co.icesi.finanzas.dto.*;
import edu.co.icesi.finanzas.entity.enums.Estado;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import java.util.List;
public interface PermisoService {
    List<PermisoView> listar();
    PermisoView obtener(Long id);
    PermisoView crear(@Valid PermisoRequest request);
    PermisoView actualizar(Long id, @Valid PermisoRequest request);
    void eliminar(Long id);
}
