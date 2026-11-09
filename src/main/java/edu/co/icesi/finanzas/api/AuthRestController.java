package edu.co.icesi.finanzas.api;

import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.dto.*;
import edu.co.icesi.finanzas.security.JwtService;
import edu.co.icesi.finanzas.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ProblemDetail;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@io.swagger.v3.oas.annotations.responses.ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida",
        content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Autenticacion requerida",
        content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado",
        content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Recurso no encontrado",
        content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflicto con los datos existentes",
        content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
})
@RestController
@RequestMapping(value = "/api/auth", produces = "application/json")
@RequiredArgsConstructor
@SecurityRequirements
public class AuthRestController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwt;
    private final UsuarioService usuarios;

    @Operation(summary = "Iniciar sesion y obtener un JWT")
    @ApiResponse(responseCode = "200", description = "Operacion completada correctamente", useReturnTypeSchema = true)
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        var auth = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(request.correo(), request.password()));
        return jwt.generateToken((UserDetails) auth.getPrincipal());
    }

    @Operation(summary = "Registrar una cuenta institucional con rol USUARIO")
    @ApiResponse(responseCode = "201", description = "Recurso creado", useReturnTypeSchema = true)
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UsuarioView> registro(@Valid @RequestBody RegistroRequest request) {
        var usuario = usuarios.registrar(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/me").build().toUri()).body(usuario);
    }
}
