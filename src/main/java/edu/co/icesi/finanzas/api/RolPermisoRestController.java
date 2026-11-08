package edu.co.icesi.finanzas.api;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.service.RolPermisoRestService;
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
@RequestMapping("/api/admin/roles-permisos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class RolPermisoRestController {
    private final RolPermisoRestService service;

    @Operation(summary = "Listar admin/roles-permisos")
    @GetMapping
    public List<RolPermisoDto> listar() { return service.listar(); }

    @Operation(summary = "Consultar RolPermiso por clave")
    @GetMapping("/{rolId}/{permisoId}")
    public RolPermisoDto obtener(@PathVariable Long rolId, @PathVariable Long permisoId) { return service.obtener(new RolPermisoId(rolId, permisoId)); }

    @Operation(summary = "Crear RolPermiso")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RolPermisoDto> crear(@Valid @RequestBody RolPermisoRequest request) {
        var dto = service.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{rolId}/{permisoId}").buildAndExpand(dto.rolId(), dto.permisoId()).toUri()).body(dto);
    }

    @Operation(summary = "Actualizar RolPermiso")
    @PutMapping("/{rolId}/{permisoId}")
    public RolPermisoDto actualizar(@PathVariable Long rolId, @PathVariable Long permisoId, @Valid @RequestBody RolPermisoRequest request) {
        return service.actualizar(new RolPermisoId(rolId, permisoId), request);
    }

    @Operation(summary = "Eliminar RolPermiso")
    @DeleteMapping("/{rolId}/{permisoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long rolId, @PathVariable Long permisoId) { service.eliminar(new RolPermisoId(rolId, permisoId)); }
}
