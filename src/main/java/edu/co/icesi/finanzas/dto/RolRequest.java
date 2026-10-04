package edu.co.icesi.finanzas.dto;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.Estado;
import java.time.LocalDate;
import java.util.Set;
public record RolRequest(@NotBlank @Size(max=50) String nombre, @Size(max=200) String descripcion, @NotNull Estado estado, @NotEmpty Set<@NotNull @Positive Long> permisoIds) {}
