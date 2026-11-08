package edu.co.icesi.finanzas.api;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.service.ParticipacionPlanRestService;
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
@RequestMapping("/api/participaciones-plan")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ParticipacionPlanRestController {
    private final ParticipacionPlanRestService service;

    @Operation(summary = "Listar participaciones-plan")
    @GetMapping
    public List<ParticipacionPlanDto> listar() { return service.listar(); }

    @Operation(summary = "Consultar ParticipacionPlan por clave")
    @GetMapping("/{planId}/{usuarioId}")
    public ParticipacionPlanDto obtener(@PathVariable Long planId, @PathVariable Long usuarioId) { return service.obtener(new ParticipacionPlanId(planId, usuarioId)); }

    @Operation(summary = "Crear ParticipacionPlan")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ParticipacionPlanDto> crear(@Valid @RequestBody ParticipacionPlanRequest request) {
        var dto = service.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{planId}/{usuarioId}").buildAndExpand(dto.planId(), dto.usuarioId()).toUri()).body(dto);
    }

    @Operation(summary = "Actualizar ParticipacionPlan")
    @PutMapping("/{planId}/{usuarioId}")
    public ParticipacionPlanDto actualizar(@PathVariable Long planId, @PathVariable Long usuarioId, @Valid @RequestBody ParticipacionPlanRequest request) {
        return service.actualizar(new ParticipacionPlanId(planId, usuarioId), request);
    }

    @Operation(summary = "Eliminar ParticipacionPlan")
    @DeleteMapping("/{planId}/{usuarioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long planId, @PathVariable Long usuarioId) { service.eliminar(new ParticipacionPlanId(planId, usuarioId)); }
}
