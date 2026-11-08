package edu.co.icesi.finanzas.api;
import edu.co.icesi.finanzas.dto.*;
import edu.co.icesi.finanzas.service.PermisoService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@SecurityRequirement(name = "bearerAuth")
@io.swagger.v3.oas.annotations.responses.ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Autenticacion requerida"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflicto con los datos existentes")
})
@RestController @RequestMapping("/api/admin/permisos") @RequiredArgsConstructor
public class PermisoRestController {
    private final PermisoService service;
    @Operation(summary = "Consultar permiso")
    @GetMapping public List<PermisoView> listar() { return service.listar(); }
    @Operation(summary = "Consultar permiso")
    @GetMapping("/{id}") public PermisoView obtener(@PathVariable Long id) { return service.obtener(id); }
    @Operation(summary = "Crear o asignar permiso")
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PermisoView> crear(@Valid @RequestBody PermisoRequest request) {
        var dto = service.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(dto.id()).toUri()).body(dto);
    }
    @Operation(summary = "Actualizar permiso")
    @PutMapping("/{id}")
    public PermisoView actualizar(@PathVariable Long id, @Valid @RequestBody PermisoRequest request) { return service.actualizar(id, request); }
    @Operation(summary = "Eliminar o retirar permiso")
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
