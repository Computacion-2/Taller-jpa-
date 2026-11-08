package edu.co.icesi.finanzas.api;

import edu.co.icesi.finanzas.dto.*;
import edu.co.icesi.finanzas.entity.enums.Estado;
import edu.co.icesi.finanzas.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@io.swagger.v3.oas.annotations.responses.ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Autenticacion requerida"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflicto con los datos existentes")
})
@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
public class UsuarioRestController {
    private final UsuarioService service;

    @Operation(summary = "Consultar usuario")
    @GetMapping
    public PageView<UsuarioView> listar(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) Long rolId,
            @RequestParam(required = false) Estado estado,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return service.listar(texto, rolId, estado, pageable);
    }

    @Operation(summary = "Consultar usuario")
    @GetMapping("/{id}")
    public UsuarioView obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @Operation(summary = "Crear o asignar usuario")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UsuarioView> crear(@Valid @RequestBody UsuarioCrear request) {
        var dto = service.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(dto.id()).toUri()).body(dto);
    }

    @Operation(summary = "Actualizar usuario")
    @PutMapping("/{id}")
    public UsuarioView editar(@PathVariable Long id, @Valid @RequestBody UsuarioEditar request) {
        return service.editar(id, request);
    }

    @Operation(summary = "Modificar usuario")
    @PatchMapping("/{id}/estado")
    public UsuarioView cambiarEstado(@PathVariable Long id, @RequestParam Estado estado) {
        return service.cambiarEstado(id, estado);
    }

    @Operation(summary = "Crear o asignar usuario")
    @PostMapping("/{id}/roles/{rolId}")
    public UsuarioView asignarRol(@PathVariable Long id, @PathVariable Long rolId) {
        return service.asignarRol(id, rolId);
    }

    @Operation(summary = "Eliminar o retirar usuario")
    @DeleteMapping("/{id}/roles/{rolId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retirarRol(@PathVariable Long id, @PathVariable Long rolId) {
        service.retirarRol(id, rolId);
    }

    @Operation(summary = "Eliminar o retirar usuario")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    @Operation(summary = "Actualizar usuario")
    @PutMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restablecerPassword(@PathVariable Long id, @Valid @RequestBody ResetPassword request) {
        service.restablecerPassword(id, request);
    }
}
