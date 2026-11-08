package edu.co.icesi.finanzas.api;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.service.UsuarioRolRestService;
import edu.co.icesi.finanzas.entity.keys.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@io.swagger.v3.oas.annotations.responses.ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Autenticacion requerida"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflicto con los datos existentes")
})
@RestController
@RequestMapping("/api/admin/usuarios-roles")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UsuarioRolRestController {
    private final UsuarioRolRestService service;

    @Operation(summary = "Listar admin/usuarios-roles")
    @GetMapping
    public List<UsuarioRolDto> listar() { return service.listar(); }

    @Operation(summary = "Consultar UsuarioRol por clave")
    @GetMapping("/{usuarioId}/{rolId}")
    public UsuarioRolDto obtener(@PathVariable Long usuarioId, @PathVariable Long rolId) { return service.obtener(new UsuarioRolId(usuarioId, rolId)); }

    @Operation(summary = "Crear UsuarioRol")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UsuarioRolDto> crear(@Valid @RequestBody UsuarioRolRequest request) {
        var dto = service.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{usuarioId}/{rolId}").buildAndExpand(dto.usuarioId(), dto.rolId()).toUri()).body(dto);
    }

    @Operation(summary = "Actualizar UsuarioRol")
    @PutMapping("/{usuarioId}/{rolId}")
    public UsuarioRolDto actualizar(@PathVariable Long usuarioId, @PathVariable Long rolId, @Valid @RequestBody UsuarioRolRequest request) {
        return service.actualizar(new UsuarioRolId(usuarioId, rolId), request);
    }

    @Operation(summary = "Eliminar UsuarioRol")
    @DeleteMapping("/{usuarioId}/{rolId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long usuarioId, @PathVariable Long rolId) { service.eliminar(new UsuarioRolId(usuarioId, rolId)); }
}
