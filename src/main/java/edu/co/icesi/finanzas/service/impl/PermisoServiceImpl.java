package edu.co.icesi.finanzas.service.impl;
import edu.co.icesi.finanzas.dto.*;
import edu.co.icesi.finanzas.entity.*;
import edu.co.icesi.finanzas.entity.enums.*;
import edu.co.icesi.finanzas.entity.keys.*;
import edu.co.icesi.finanzas.repo.*;
import edu.co.icesi.finanzas.service.*;
import static edu.co.icesi.finanzas.exception.BusinessException.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.time.LocalDate;
import java.util.*;
@Service @Validated @RequiredArgsConstructor @Transactional(readOnly = true)
public class PermisoServiceImpl implements PermisoService {
    private final PermisoRepository permisos;
    private final RolPermisoRepository asociaciones;
    public List<PermisoView> listar() { return permisos.findAll().stream().map(Views::permiso).toList(); }
    public PermisoView obtener(Long id) { return Views.permiso(permisos.findById(id).orElseThrow(() -> missing("Permiso"))); }
    @Transactional
    public PermisoView crear(PermisoRequest request) { return guardar(new Permiso(), request); }
    @Transactional
    public PermisoView actualizar(Long id, PermisoRequest request) {
        return guardar(permisos.findLockedById(id).orElseThrow(() -> missing("Permiso")), request);
    }
    private PermisoView guardar(Permiso p, PermisoRequest r) {
        Long excluded = p.getId() == null ? -1L : p.getId();
        if (permisos.existsByNombreIgnoreCaseAndIdNot(r.nombre().trim(), excluded)) throw conflict("Nombre de permiso duplicado");
        p.setNombre(r.nombre().trim()); p.setDescripcion(r.descripcion()); p.setEstado(r.estado());
        return Views.permiso(permisos.save(p));
    }
    @Transactional
    public void eliminar(Long id) {
        Permiso p = permisos.findLockedById(id).orElseThrow(() -> missing("Permiso"));
        if (asociaciones.existsByPermiso_Id(id)) throw conflict("Retire el permiso de sus roles antes de eliminarlo");
        permisos.delete(p);
    }
}
