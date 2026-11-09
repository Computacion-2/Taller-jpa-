package edu.co.icesi.finanzas.api;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.service.ParticipacionPlanRestService;
import edu.co.icesi.finanzas.entity.keys.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ProblemDetail;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@io.swagger.v3.oas.annotations.responses.ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida",
        content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Autenticacion requerida",
        content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado",
        content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Recurso no encontrado",
        content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflicto con los datos existentes",
        content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
})
@RestController
@RequestMapping(value = "/api/participaciones-plan", produces = "application/json")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ParticipacionPlanRestController {
    private final ParticipacionPlanRestService service;

    @Operation(summary = "Listar participaciones-plan")
    @ApiResponse(responseCode = "200", description = "Operacion completada correctamente", useReturnTypeSchema = true)
    @GetMapping
    public List<ParticipacionPlanDto> listar() { return service.listar(); }

    @Operation(summary = "Consultar ParticipacionPlan por clave")
    @ApiResponse(responseCode = "200", description = "Operacion completada correctamente", useReturnTypeSchema = true)
    @GetMapping("/{planId}/{usuarioId}")
    public ParticipacionPlanDto obtener(@PathVariable Long planId, @PathVariable Long usuarioId) { return service.obtener(new ParticipacionPlanId(planId, usuarioId)); }

    @Operation(summary = "Crear ParticipacionPlan")
    @ApiResponse(responseCode = "201", description = "Recurso creado", useReturnTypeSchema = true)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ParticipacionPlanDto> crear(@Valid @RequestBody ParticipacionPlanRequest request) {
        var dto = service.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{planId}/{usuarioId}").buildAndExpand(dto.planId(), dto.usuarioId()).toUri()).body(dto);
    }

    @Operation(summary = "Actualizar ParticipacionPlan")
    @ApiResponse(responseCode = "200", description = "Operacion completada correctamente", useReturnTypeSchema = true)
    @PutMapping("/{planId}/{usuarioId}")
    public ParticipacionPlanDto actualizar(@PathVariable Long planId, @PathVariable Long usuarioId, @Valid @RequestBody ParticipacionPlanRequest request) {
        return service.actualizar(new ParticipacionPlanId(planId, usuarioId), request);
    }

    @Operation(summary = "Eliminar ParticipacionPlan")
    @ApiResponse(responseCode = "204", description = "Operacion completada sin cuerpo de respuesta", content = @Content)
    @DeleteMapping("/{planId}/{usuarioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long planId, @PathVariable Long usuarioId) { service.eliminar(new ParticipacionPlanId(planId, usuarioId)); }
}
