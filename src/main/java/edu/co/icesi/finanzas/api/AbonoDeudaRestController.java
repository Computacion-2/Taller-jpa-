package edu.co.icesi.finanzas.api;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.service.AbonoDeudaRestService;
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
@RequestMapping("/api/abonos-deuda")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AbonoDeudaRestController {
    private final AbonoDeudaRestService service;

    @Operation(summary = "Listar abonos-deuda")
    @GetMapping
    public List<AbonoDeudaDto> listar() { return service.listar(); }

    @Operation(summary = "Consultar AbonoDeuda por clave")
    @GetMapping("/{id}")
    public AbonoDeudaDto obtener(@PathVariable Long id) { return service.obtener(id); }

    @Operation(summary = "Crear AbonoDeuda")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AbonoDeudaDto> crear(@Valid @RequestBody AbonoDeudaRequest request) {
        var dto = service.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(dto.id()).toUri()).body(dto);
    }

    @Operation(summary = "Actualizar AbonoDeuda")
    @PutMapping("/{id}")
    public AbonoDeudaDto actualizar(@PathVariable Long id, @Valid @RequestBody AbonoDeudaRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Eliminar AbonoDeuda")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
