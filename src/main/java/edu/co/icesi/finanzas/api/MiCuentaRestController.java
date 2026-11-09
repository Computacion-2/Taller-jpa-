package edu.co.icesi.finanzas.api;

import edu.co.icesi.finanzas.dto.*;
import edu.co.icesi.finanzas.repo.UsuarioRepository;
import edu.co.icesi.finanzas.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ProblemDetail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import static edu.co.icesi.finanzas.exception.BusinessException.missing;

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
@RequestMapping(value = "/api", produces = "application/json")
@RequiredArgsConstructor
public class MiCuentaRestController {
    private final UsuarioService service;
    private final UsuarioRepository usuarios;

    @Operation(summary = "Registrar una cuenta (ruta compatible con JPA)")
    @ApiResponse(responseCode = "201", description = "Recurso creado", useReturnTypeSchema = true)
    @io.swagger.v3.oas.annotations.security.SecurityRequirements
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioView registrar(@Valid @RequestBody RegistroRequest request) {
        return service.registrar(request);
    }

    @Operation(summary = "Consultar el perfil autenticado")
    @ApiResponse(responseCode = "200", description = "Operacion completada correctamente", useReturnTypeSchema = true)
    @GetMapping("/me")
    public UsuarioView perfil(Authentication auth) { return service.obtener(id(auth)); }

    @Operation(summary = "Cambiar la contrasena propia")
    @ApiResponse(responseCode = "204", description = "Operacion completada sin cuerpo de respuesta", content = @Content)
    @PutMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cambiarPassword(Authentication auth, @Valid @RequestBody CambioPassword request) {
        service.cambiarPassword(id(auth), request);
    }

    private Long id(Authentication auth) {
        return usuarios.findByCorreoIgnoreCase(auth.getName()).orElseThrow(() -> missing("Usuario")).getId();
    }
}
