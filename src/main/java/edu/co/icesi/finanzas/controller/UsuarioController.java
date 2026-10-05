package edu.co.icesi.finanzas.controller;

import edu.co.icesi.finanzas.dto.*;
import edu.co.icesi.finanzas.entity.enums.Estado;
import edu.co.icesi.finanzas.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService service;

    @GetMapping
    public PageView<UsuarioView> listar(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) Long rolId,
            @RequestParam(required = false) Estado estado,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return service.listar(texto, rolId, estado, pageable);
    }

    @GetMapping("/{id}")
    public UsuarioView obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioView crear(@Valid @RequestBody UsuarioCrear request) {
        return service.crear(request);
    }

    @PutMapping("/{id}")
    public UsuarioView editar(@PathVariable Long id, @Valid @RequestBody UsuarioEditar request) {
        return service.editar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public UsuarioView cambiarEstado(@PathVariable Long id, @RequestParam Estado estado) {
        return service.cambiarEstado(id, estado);
    }

    @PostMapping("/{id}/roles/{rolId}")
    public UsuarioView asignarRol(@PathVariable Long id, @PathVariable Long rolId) {
        return service.asignarRol(id, rolId);
    }

    @DeleteMapping("/{id}/roles/{rolId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retirarRol(@PathVariable Long id, @PathVariable Long rolId) {
        service.retirarRol(id, rolId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    @PutMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restablecerPassword(@PathVariable Long id, @Valid @RequestBody ResetPassword request) {
        service.restablecerPassword(id, request);
    }
}
