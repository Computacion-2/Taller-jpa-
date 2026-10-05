package edu.co.icesi.finanzas.service;

import edu.co.icesi.finanzas.dto.RolRequest;
import edu.co.icesi.finanzas.dto.RolView;
import jakarta.validation.Valid;

import java.util.List;

public interface RolService {
    List<RolView> listar();
    RolView obtener(Long id);
    RolView crear(@Valid RolRequest request);
    RolView actualizar(Long id, @Valid RolRequest request);
    void eliminar(Long id);
}
