package edu.co.icesi.finanzas.api;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.service.AportePlanRestService;
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
@RequestMapping("/api/aportes-plan")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AportePlanRestController {
    private final AportePlanRestService service;

    @Operation(summary = "Listar aportes-plan")
    @GetMapping
    public List<AportePlanDto> listar() { return service.listar(); }

    @Operation(summary = "Consultar AportePlan por clave")
    @GetMapping("/{id}")
    public AportePlanDto obtener(@PathVariable Long id) { return service.obtener(id); }

    @Operation(summary = "Crear AportePlan")
    @PostMapping
    public ResponseEntity<AportePlanDto> crear(@Valid @RequestBody AportePlanRequest request) {
        var dto = service.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(dto.id()).toUri()).body(dto);
    }

    @Operation(summary = "Actualizar AportePlan")
    @PutMapping("/{id}")
    public AportePlanDto actualizar(@PathVariable Long id, @Valid @RequestBody AportePlanRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Eliminar AportePlan")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
