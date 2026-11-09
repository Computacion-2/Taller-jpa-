package edu.co.icesi.finanzas.api;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.service.MonedaRestService;
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
@RequestMapping(value = "/api/monedas", produces = "application/json")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class MonedaRestController {
    private final MonedaRestService service;

    @Operation(summary = "Listar monedas")
    @ApiResponse(responseCode = "200", description = "Operacion completada correctamente", useReturnTypeSchema = true)
    @GetMapping
    public List<MonedaDto> listar() { return service.listar(); }

    @Operation(summary = "Consultar Moneda por clave")
    @ApiResponse(responseCode = "200", description = "Operacion completada correctamente", useReturnTypeSchema = true)
    @GetMapping("/{id}")
    public MonedaDto obtener(@PathVariable Long id) { return service.obtener(id); }

    @Operation(summary = "Crear Moneda")
    @ApiResponse(responseCode = "201", description = "Recurso creado", useReturnTypeSchema = true)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MonedaDto> crear(@Valid @RequestBody MonedaRequest request) {
        var dto = service.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(dto.id()).toUri()).body(dto);
    }

    @Operation(summary = "Actualizar Moneda")
    @ApiResponse(responseCode = "200", description = "Operacion completada correctamente", useReturnTypeSchema = true)
    @PutMapping("/{id}")
    public MonedaDto actualizar(@PathVariable Long id, @Valid @RequestBody MonedaRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Eliminar Moneda")
    @ApiResponse(responseCode = "204", description = "Operacion completada sin cuerpo de respuesta", content = @Content)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
