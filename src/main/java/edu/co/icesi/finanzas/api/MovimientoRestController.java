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
