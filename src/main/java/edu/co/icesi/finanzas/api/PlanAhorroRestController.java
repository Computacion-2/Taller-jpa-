package edu.co.icesi.finanzas.api;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.service.PlanAhorroRestService;
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
@RequestMapping("/api/planes-ahorro")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PlanAhorroRestController {
    private final PlanAhorroRestService service;

    @Operation(summary = "Listar planes-ahorro")
    @GetMapping
    public List<PlanAhorroDto> listar() { return service.listar(); }

    @Operation(summary = "Consultar PlanAhorro por clave")
    @GetMapping("/{id}")
    public PlanAhorroDto obtener(@PathVariable Long id) { return service.obtener(id); }

    @Operation(summary = "Crear PlanAhorro")
    @PostMapping
    public ResponseEntity<PlanAhorroDto> crear(@Valid @RequestBody PlanAhorroRequest request) {
        var dto = service.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(dto.id()).toUri()).body(dto);
    }

    @Operation(summary = "Actualizar PlanAhorro")
    @PutMapping("/{id}")
    public PlanAhorroDto actualizar(@PathVariable Long id, @Valid @RequestBody PlanAhorroRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Eliminar PlanAhorro")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
