package edu.co.icesi.finanzas.service.impl;

import edu.co.icesi.finanzas.dto.RolRequest;
import edu.co.icesi.finanzas.dto.RolView;
import edu.co.icesi.finanzas.dto.Views;
import edu.co.icesi.finanzas.entity.Permiso;
import edu.co.icesi.finanzas.entity.Rol;
import edu.co.icesi.finanzas.entity.RolPermiso;
import edu.co.icesi.finanzas.entity.keys.RolPermisoId;
import edu.co.icesi.finanzas.repo.PermisoRepository;
import edu.co.icesi.finanzas.repo.RolPermisoRepository;
import edu.co.icesi.finanzas.repo.RolRepository;
import edu.co.icesi.finanzas.repo.UsuarioRolRepository;
import edu.co.icesi.finanzas.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static edu.co.icesi.finanzas.exception.BusinessException.conflict;
import static edu.co.icesi.finanzas.exception.BusinessException.invalid;
import static edu.co.icesi.finanzas.exception.BusinessException.missing;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RolServiceImpl implements RolService {
    private final RolRepository roles;
    private final PermisoRepository permisos;
    private final RolPermisoRepository asociaciones;
    private final UsuarioRolRepository usuarioRoles;

    @Override
    public List<RolView> listar() {
        return roles.findAll().stream().map(Views::rol).toList();
    }

    @Override
    public RolView obtener(Long id) {
        return Views.rol(roles.findById(id).orElseThrow(() -> missing("Rol")));
    }

    @Override
    @Transactional
    public RolView crear(RolRequest request) {
        String nombre = normalizarNombre(request.nombre());
        if (roles.existsByNombreIgnoreCaseAndIdNot(nombre, -1L)) {
            throw conflict("Nombre de rol duplicado");
        }

        Set<Long> permisoIds = normalizarIds(request.permisoIds());
        if (permisoIds.isEmpty()) {
            throw invalid("El rol debe tener al menos un permiso");
        }
        validarPermisosExistentes(permisoIds);

        Rol rol = new Rol();
        rol.setNombre(nombre);
        rol.setDescripcion(descripcion(request.descripcion()));
        rol.setEstado(request.estado());
        rol = roles.save(rol);

        agregarPermisos(rol, permisoIds);
        return Views.rol(roles.save(rol));
    }

    @Override
    @Transactional
    public RolView actualizar(Long id, RolRequest request) {
        Rol rol = roles.findLockedById(id).orElseThrow(() -> missing("Rol"));
        String nombre = normalizarNombre(request.nombre());
        if (roles.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw conflict("Nombre de rol duplicado");
        }

        Set<Long> permisoIds = normalizarIds(request.permisoIds());
        if (permisoIds.isEmpty()) {
            throw invalid("El rol debe tener al menos un permiso");
        }
        validarPermisosExistentes(permisoIds);

        rol.setNombre(nombre);
        rol.setDescripcion(descripcion(request.descripcion()));
        rol.setEstado(request.estado());

        Set<Long> idsActuales = rol.getPermisos().stream()
                .map(rp -> rp.getPermiso().getId())
                .collect(Collectors.toSet());

        rol.getPermisos().removeIf(rp -> !permisoIds.contains(rp.getPermiso().getId()));
        for (Long permisoId : permisoIds) {
            if (idsActuales.contains(permisoId)) {
                continue;
            }
            Permiso permiso = permisos.findById(permisoId).orElseThrow(() -> missing("Permiso"));
            RolPermiso rp = new RolPermiso();
            rp.setId(new RolPermisoId(rol.getId(), permisoId));
            rp.setRol(rol);
            rp.setPermiso(permiso);
            rp.setFechaAsignacion(LocalDate.now());
            rol.getPermisos().add(rp);
        }

        return Views.rol(roles.save(rol));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Rol rol = roles.findLockedById(id).orElseThrow(() -> missing("Rol"));
        if (usuarioRoles.existsByRol_Id(id)) {
            throw conflict("Retire el rol de sus usuarios antes de eliminarlo");
        }
        roles.delete(rol);
    }

    private void agregarPermisos(Rol rol, Set<Long> permisoIds) {
        for (Long permisoId : permisoIds) {
            Permiso permiso = permisos.findById(permisoId).orElseThrow(() -> missing("Permiso"));
            RolPermiso rp = new RolPermiso();
            rp.setId(new RolPermisoId(rol.getId(), permisoId));
            rp.setRol(rol);
            rp.setPermiso(permiso);
            rp.setFechaAsignacion(LocalDate.now());
            rol.getPermisos().add(rp);
        }
    }

    private void validarPermisosExistentes(Set<Long> permisoIds) {
        if (permisoIds.size() != permisos.findAllById(permisoIds).size()) {
            throw missing("Permiso");
        }
    }

    private String normalizarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw invalid("Nombre obligatorio");
        }
        return nombre.trim();
    }

    private String descripcion(String descripcion) {
        return descripcion == null ? null : descripcion.trim();
    }

    private Set<Long> normalizarIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new LinkedHashSet<>();
        }
        return ids.stream()
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
