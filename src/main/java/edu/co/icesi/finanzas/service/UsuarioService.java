package edu.co.icesi.finanzas.service;

import edu.co.icesi.finanzas.dto.*;
import edu.co.icesi.finanzas.entity.enums.Estado;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

public interface UsuarioService {
    PageView<UsuarioView> listar(String texto, Long rolId, Estado estado, Pageable pageable);
    UsuarioView obtener(Long id);
    UsuarioView crear(@Valid UsuarioCrear request);
    UsuarioView editar(Long id, @Valid UsuarioEditar request);
    UsuarioView cambiarEstado(Long id, Estado estado);
    UsuarioView asignarRol(Long usuarioId, Long rolId);
    void retirarRol(Long usuarioId, Long rolId);
    void eliminar(Long id);
    void restablecerPassword(Long id, @Valid ResetPassword request);
    void cambiarPassword(Long usuarioId, @Valid CambioPassword request);
    UsuarioView registrar(@Valid RegistroRequest request);
}
