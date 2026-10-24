package edu.co.icesi.finanzas.dto.form;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter @Setter
public class UsuarioForm {
    @NotBlank(message = "Escriba el nombre completo")
    @Size(max = 150, message = "El nombre admite hasta 150 caracteres")
    private String nombreCompleto;
    @NotBlank(message = "Escriba el correo")
    @Email(message = "Escriba un correo válido")
    @Size(max = 150, message = "El correo admite hasta 150 caracteres")
    private String correo;
    @NotNull(message = "Seleccione una moneda")
    @Positive(message = "Seleccione una moneda válida")
    private Long monedaId;
    @NotEmpty(message = "Seleccione al menos un rol")
    private Set<@NotNull @Positive Long> rolIds = new LinkedHashSet<>();
}
