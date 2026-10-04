package edu.co.icesi.finanzas.dto;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.Estado;
import java.time.LocalDate;
import java.util.Set;
public record PermisoRequest(@NotBlank @Size(max=100) String nombre, @Size(max=250) String descripcion, @NotNull Estado estado) {}
