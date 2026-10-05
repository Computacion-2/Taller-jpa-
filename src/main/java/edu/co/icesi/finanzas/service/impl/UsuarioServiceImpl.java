package edu.co.icesi.finanzas.service.impl;

import edu.co.icesi.finanzas.dto.*;
import edu.co.icesi.finanzas.entity.*;
import edu.co.icesi.finanzas.entity.enums.Estado;
import edu.co.icesi.finanzas.entity.keys.UsuarioRolId;
import edu.co.icesi.finanzas.repo.*;
import edu.co.icesi.finanzas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static edu.co.icesi.finanzas.exception.BusinessException.conflict;
import static edu.co.icesi.finanzas.exception.BusinessException.invalid;
import static edu.co.icesi.finanzas.exception.BusinessException.missing;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final MonedaRepository monedas;
    private final UsuarioRolRepository usuarioRoles;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageView<UsuarioView> listar(String texto, Long rolId, Estado estado, Pageable pageable) {
        String valor = texto == null ? null : texto.trim();
        if (valor != null && valor.isEmpty()) {
            valor = null;
        }
        Page<Usuario> page = usuarios.buscar(valor, rolId, estado, pageable);
        return PageView.from(page.map(Views::usuario));
    }

    @Override
    public UsuarioView obtener(Long id) {
        return Views.usuario(usuarios.findById(id).orElseThrow(() -> missing("Usuario")));
    }

    @Override
    @Transactional
    public UsuarioView crear(UsuarioCrear request) {
        String correo = normalizarCorreo(request.correo());
        if (usuarios.existsByCorreoIgnoreCaseAndIdNot(correo, -1L)) {
            throw conflict("Correo ya registrado");
        }

        Set<Long> rolIds = normalizarIds(request.rolIds());
        if (rolIds.isEmpty()) {
            throw invalid("El usuario debe tener al menos un rol");
        }
        validarRolesExistentes(rolIds);

        Moneda moneda = monedas.findById(request.monedaId()).orElseThrow(() -> missing("Moneda"));
        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(request.nombreCompleto().trim());
        usuario.setCorreo(correo);
        usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        usuario.setMoneda(moneda);
        usuario.setEstado(Estado.ACTIVO);
        usuario.setFechaRegistro(LocalDate.now());

        usuario = usuarios.save(usuario);
        for (Long rolId : rolIds) {
            Rol rol = roles.findById(rolId).orElseThrow(() -> missing("Rol"));
            UsuarioRol asignacion = new UsuarioRol();
            asignacion.setId(new UsuarioRolId(usuario.getId(), rolId));
            asignacion.setUsuario(usuario);
            asignacion.setRol(rol);
            asignacion.setFechaAsignacion(LocalDate.now());
            usuario.getRoles().add(asignacion);
        }
        return Views.usuario(usuarios.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioView editar(Long id, UsuarioEditar request) {
        Usuario usuario = usuarios.findLockedById(id).orElseThrow(() -> missing("Usuario"));
        String correo = normalizarCorreo(request.correo());
        if (usuarios.existsByCorreoIgnoreCaseAndIdNot(correo, id)) {
            throw conflict("Correo ya registrado");
        }

        Set<Long> rolIds = normalizarIds(request.rolIds());
        if (rolIds.isEmpty()) {
            throw invalid("El usuario debe tener al menos un rol");
        }
        validarRolesExistentes(rolIds);
        Moneda moneda = monedas.findById(request.monedaId()).orElseThrow(() -> missing("Moneda"));

        usuario.setNombreCompleto(request.nombreCompleto().trim());
        usuario.setCorreo(correo);
        usuario.setMoneda(moneda);
        usuario.setEstado(request.estado());

        Set<Long> idsActuales = usuario.getRoles().stream()
                .map(ur -> ur.getRol().getId())
                .collect(Collectors.toSet());

        usuario.getRoles().removeIf(ur -> !rolIds.contains(ur.getRol().getId()));
        for (Long rolId : rolIds) {
            if (idsActuales.contains(rolId)) {
                continue;
            }
            Rol rol = roles.findById(rolId).orElseThrow(() -> missing("Rol"));
            UsuarioRol asignacion = new UsuarioRol();
            asignacion.setId(new UsuarioRolId(usuario.getId(), rolId));
            asignacion.setUsuario(usuario);
            asignacion.setRol(rol);
            asignacion.setFechaAsignacion(LocalDate.now());
            usuario.getRoles().add(asignacion);
        }

        usuario.setFechaActualizacion(LocalDate.now());
        return Views.usuario(usuarios.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioView cambiarEstado(Long id, Estado estado) {
        Usuario usuario = usuarios.findLockedById(id).orElseThrow(() -> missing("Usuario"));
        if (usuario.getEstado() == Estado.ACTIVO && estado == Estado.INACTIVO && esUltimoAdministradorActivo(id)) {
            throw conflict("No se puede desactivar al último administrador activo");
        }
        usuario.setEstado(estado);
        usuario.setFechaActualizacion(LocalDate.now());
        return Views.usuario(usuarios.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioView asignarRol(Long usuarioId, Long rolId) {
        Usuario usuario = usuarios.findLockedById(usuarioId).orElseThrow(() -> missing("Usuario"));
        Rol rol = roles.findById(rolId).orElseThrow(() -> missing("Rol"));

        boolean yaTieneRol = usuario.getRoles().stream()
                .anyMatch(ur -> Objects.equals(ur.getRol().getId(), rolId));
        if (yaTieneRol) {
            return Views.usuario(usuario);
        }

        UsuarioRol asignacion = new UsuarioRol();
        asignacion.setId(new UsuarioRolId(usuarioId, rolId));
        asignacion.setUsuario(usuario);
        asignacion.setRol(rol);
        asignacion.setFechaAsignacion(LocalDate.now());
        usuario.getRoles().add(asignacion);
        return Views.usuario(usuarios.save(usuario));
    }

    @Override
    @Transactional
    public void retirarRol(Long usuarioId, Long rolId) {
        Usuario usuario = usuarios.findLockedById(usuarioId).orElseThrow(() -> missing("Usuario"));
        List<UsuarioRol> rolesUsuario = usuario.getRoles();
        if (rolesUsuario.stream().noneMatch(ur -> Objects.equals(ur.getRol().getId(), rolId))) {
            throw invalid("El usuario no tiene ese rol");
        }
        if (rolesUsuario.stream().filter(ur -> ur.getRol() != null).count() <= 1) {
            throw conflict("El usuario no puede quedar sin roles");
        }
        rolesUsuario.removeIf(ur -> Objects.equals(ur.getRol().getId(), rolId));
        usuarios.save(usuario);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = usuarios.findLockedById(id).orElseThrow(() -> missing("Usuario"));
        if (esUltimoAdministradorActivo(id)) {
            throw conflict("No se puede eliminar al último administrador activo");
        }
        usuarios.delete(usuario);
    }

    @Override
    @Transactional
    public void restablecerPassword(Long id, ResetPassword request) {
        Usuario usuario = usuarios.findLockedById(id).orElseThrow(() -> missing("Usuario"));
        usuario.setPasswordHash(passwordEncoder.encode(request.nueva()));
        usuario.setFechaActualizacion(LocalDate.now());
        usuarios.save(usuario);
    }

    @Override
    @Transactional
    public void cambiarPassword(Long usuarioId, CambioPassword request) {
        Usuario usuario = usuarios.findLockedById(usuarioId).orElseThrow(() -> missing("Usuario"));
        if (!passwordEncoder.matches(request.actual(), usuario.getPasswordHash())) {
            throw invalid("La contraseña actual no coincide");
        }
        usuario.setPasswordHash(passwordEncoder.encode(request.nueva()));
        usuario.setFechaActualizacion(LocalDate.now());
        usuarios.save(usuario);
    }

    @Override
    @Transactional
    public UsuarioView registrar(RegistroRequest request) {
        String correo = normalizarCorreo(request.correo());
        if (!correo.toLowerCase().endsWith("@u.icesi.edu.co")) {
            throw invalid("El correo debe pertenecer al dominio u.icesi.edu.co");
        }
        if (usuarios.existsByCorreoIgnoreCaseAndIdNot(correo, -1L)) {
            throw conflict("Correo ya registrado");
        }
        Rol rolUsuario = roles.findByNombre("USUARIO").orElseThrow(() -> missing("Rol"));
        Moneda moneda = monedas.findById(request.monedaId()).orElseThrow(() -> missing("Moneda"));

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(request.nombreCompleto().trim());
        usuario.setCorreo(correo);
        usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        usuario.setMoneda(moneda);
        usuario.setEstado(Estado.ACTIVO);
        usuario.setFechaRegistro(LocalDate.now());
        usuario = usuarios.save(usuario);

        UsuarioRol asignacion = new UsuarioRol();
        asignacion.setId(new UsuarioRolId(usuario.getId(), rolUsuario.getId()));
        asignacion.setUsuario(usuario);
        asignacion.setRol(rolUsuario);
        asignacion.setFechaAsignacion(LocalDate.now());
        usuario.getRoles().add(asignacion);
        return Views.usuario(usuarios.save(usuario));
    }

    private void validarRolesExistentes(Set<Long> rolIds) {
        if (rolIds.size() != roles.findAllById(rolIds).size()) {
            throw missing("Rol");
        }
    }

    private String normalizarCorreo(String correo) {
        if (correo == null || correo.isBlank()) {
            throw invalid("Correo obligatorio");
        }
        return correo.trim();
    }

    private Set<Long> normalizarIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new LinkedHashSet<>();
        }
        return ids.stream()
                .filter(Objects::nonNull)
                .map(Long::valueOf)
                .filter(id -> id > 0)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private boolean esUltimoAdministradorActivo(Long usuarioId) {
        List<Usuario> administradores = usuarios.findAll().stream()
                .filter(u -> u.getEstado() == Estado.ACTIVO)
                .filter(u -> u.getRoles().stream().map(ur -> ur.getRol().getNombre()).anyMatch(nombre -> "ADMINISTRADOR".equalsIgnoreCase(nombre)))
                .toList();
        return administradores.size() == 1 && administradores.get(0).getId().equals(usuarioId);
    }
}
