package edu.co.icesi.finanzas.controller;

import edu.co.icesi.finanzas.dto.RolRequest;
import edu.co.icesi.finanzas.dto.RolView;
import edu.co.icesi.finanzas.service.RolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class RolController {
    private final RolService service;

    @GetMapping
    public List<RolView> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public RolView obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RolView crear(@Valid @RequestBody RolRequest request) {
        return service.crear(request);
    }

    @PutMapping("/{id}")
    public RolView actualizar(@PathVariable Long id, @Valid @RequestBody RolRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
