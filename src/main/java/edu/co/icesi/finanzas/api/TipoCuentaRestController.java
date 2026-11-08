package edu.co.icesi.finanzas.api;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.service.TipoCuentaRestService;
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
@RequestMapping("/api/tipos-cuenta")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TipoCuentaRestController {
    private final TipoCuentaRestService service;

    @Operation(summary = "Listar tipos-cuenta")
    @GetMapping
    public List<TipoCuentaDto> listar() { return service.listar(); }

    @Operation(summary = "Consultar TipoCuenta por clave")
    @GetMapping("/{id}")
    public TipoCuentaDto obtener(@PathVariable Long id) { return service.obtener(id); }

    @Operation(summary = "Crear TipoCuenta")
    @PostMapping
    public ResponseEntity<TipoCuentaDto> crear(@Valid @RequestBody TipoCuentaRequest request) {
        var dto = service.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(dto.id()).toUri()).body(dto);
    }

    @Operation(summary = "Actualizar TipoCuenta")
    @PutMapping("/{id}")
    public TipoCuentaDto actualizar(@PathVariable Long id, @Valid @RequestBody TipoCuentaRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Eliminar TipoCuenta")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
