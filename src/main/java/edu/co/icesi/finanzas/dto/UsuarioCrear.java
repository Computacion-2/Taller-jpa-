package edu.co.icesi.finanzas.dto;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.Estado;
import java.time.LocalDate;
import java.util.Set;
public record UsuarioCrear(@NotBlank @Size(max=150) String nombreCompleto, @NotBlank @Email @Size(max=150) String correo, @NotBlank @Size(min=8,max=72) String password, @NotNull @Positive Long monedaId, @NotEmpty Set<@NotNull @Positive Long> rolIds) {}
