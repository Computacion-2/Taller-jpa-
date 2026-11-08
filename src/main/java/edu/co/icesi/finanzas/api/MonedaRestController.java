package edu.co.icesi.finanzas.api;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.service.MonedaRestService;
import edu.co.icesi.finanzas.entity.keys.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/monedas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class MonedaRestController {
    private final MonedaRestService service;

    @Operation(summary = "Listar monedas")
    @GetMapping
    public List<MonedaDto> listar() { return service.listar(); }

    @Operation(summary = "Consultar Moneda por clave")
    @GetMapping("/{id}")
    public MonedaDto obtener(@PathVariable Long id) { return service.obtener(id); }

    @Operation(summary = "Crear Moneda")
    @PostMapping
    public ResponseEntity<MonedaDto> crear(@Valid @RequestBody MonedaRequest request) {
        var dto = service.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(dto.id()).toUri()).body(dto);
    }

    @Operation(summary = "Actualizar Moneda")
    @PutMapping("/{id}")
    public MonedaDto actualizar(@PathVariable Long id, @Valid @RequestBody MonedaRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Eliminar Moneda")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
