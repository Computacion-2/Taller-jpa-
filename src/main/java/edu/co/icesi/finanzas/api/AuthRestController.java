package edu.co.icesi.finanzas.api;

import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.dto.*;
import edu.co.icesi.finanzas.security.JwtService;
import edu.co.icesi.finanzas.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
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
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@SecurityRequirements
public class AuthRestController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwt;
    private final UsuarioService usuarios;

    @Operation(summary = "Iniciar sesion y obtener un JWT")
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        var auth = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(request.correo(), request.password()));
        return jwt.generateToken((UserDetails) auth.getPrincipal());
    }

    @Operation(summary = "Registrar una cuenta institucional con rol USUARIO")
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UsuarioView> registro(@Valid @RequestBody RegistroRequest request) {
        var usuario = usuarios.registrar(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/me").build().toUri()).body(usuario);
    }
}
