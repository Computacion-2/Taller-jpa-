package edu.co.icesi.finanzas.controller;

import edu.co.icesi.finanzas.dto.CambioPassword;
import edu.co.icesi.finanzas.dto.RegistroRequest;
import edu.co.icesi.finanzas.dto.UsuarioView;
import edu.co.icesi.finanzas.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MiCuentaController {
    private final UsuarioService service;

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioView registrar(@Valid @RequestBody RegistroRequest request) {
        return service.registrar(request);
    }

    @PutMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cambiarPassword(Authentication authentication, @Valid @RequestBody CambioPassword request) {
        String correo = authentication.getName();
        var usuario = service.listar(correo, null, null, org.springframework.data.domain.PageRequest.of(0, 1));
        if (usuario.content().isEmpty()) {
            throw new IllegalStateException("Usuario autenticado no encontrado");
        }
        service.cambiarPassword(usuario.content().get(0).id(), request);
    }
}
