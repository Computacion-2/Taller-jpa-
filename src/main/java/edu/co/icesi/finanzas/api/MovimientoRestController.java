package edu.co.icesi.finanzas.api;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.service.MovimientoRestService;
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
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class MovimientoRestController {
    private final MovimientoRestService service;

    @Operation(summary = "Listar movimientos")
    @GetMapping
    public List<MovimientoDto> listar() { return service.listar(); }

    @Operation(summary = "Consultar Movimiento por clave")
    @GetMapping("/{id}")
    public MovimientoDto obtener(@PathVariable Long id) { return service.obtener(id); }

    @Operation(summary = "Crear Movimiento")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MovimientoDto> crear(@Valid @RequestBody MovimientoRequest request) {
        var dto = service.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(dto.id()).toUri()).body(dto);
    }

    @Operation(summary = "Actualizar Movimiento")
    @PutMapping("/{id}")
    public MovimientoDto actualizar(@PathVariable Long id, @Valid @RequestBody MovimientoRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Eliminar Movimiento")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
