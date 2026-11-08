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
