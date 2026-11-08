package edu.co.icesi.finanzas.api;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.service.DeudaRestService;
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
@RequestMapping("/api/deudas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DeudaRestController {
    private final DeudaRestService service;

    @Operation(summary = "Listar deudas")
    @GetMapping
    public List<DeudaDto> listar() { return service.listar(); }

    @Operation(summary = "Consultar Deuda por clave")
    @GetMapping("/{id}")
    public DeudaDto obtener(@PathVariable Long id) { return service.obtener(id); }

    @Operation(summary = "Crear Deuda")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DeudaDto> crear(@Valid @RequestBody DeudaRequest request) {
        var dto = service.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(dto.id()).toUri()).body(dto);
    }

    @Operation(summary = "Actualizar Deuda")
    @PutMapping("/{id}")
    public DeudaDto actualizar(@PathVariable Long id, @Valid @RequestBody DeudaRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Eliminar Deuda")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
