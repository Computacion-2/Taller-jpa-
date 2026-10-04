package edu.co.icesi.finanzas.controller;
import edu.co.icesi.finanzas.dto.*;
import edu.co.icesi.finanzas.service.PermisoService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/admin/permisos") @RequiredArgsConstructor
public class PermisoController {
    private final PermisoService service;
    @GetMapping public List<PermisoView> listar() { return service.listar(); }
    @GetMapping("/{id}") public PermisoView obtener(@PathVariable Long id) { return service.obtener(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public PermisoView crear(@Valid @RequestBody PermisoRequest request) { return service.crear(request); }
    @PutMapping("/{id}")
    public PermisoView actualizar(@PathVariable Long id, @Valid @RequestBody PermisoRequest request) { return service.actualizar(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
