package edu.co.icesi.finanzas.api;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.service.PresupuestoRestService;
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
@RequestMapping("/api/presupuestos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PresupuestoRestController {
    private final PresupuestoRestService service;

    @Operation(summary = "Listar presupuestos")
    @GetMapping
    public List<PresupuestoDto> listar() { return service.listar(); }

    @Operation(summary = "Consultar Presupuesto por clave")
    @GetMapping("/{id}")
    public PresupuestoDto obtener(@PathVariable Long id) { return service.obtener(id); }

    @Operation(summary = "Crear Presupuesto")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PresupuestoDto> crear(@Valid @RequestBody PresupuestoRequest request) {
        var dto = service.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(dto.id()).toUri()).body(dto);
    }

    @Operation(summary = "Actualizar Presupuesto")
    @PutMapping("/{id}")
    public PresupuestoDto actualizar(@PathVariable Long id, @Valid @RequestBody PresupuestoRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Eliminar Presupuesto")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
