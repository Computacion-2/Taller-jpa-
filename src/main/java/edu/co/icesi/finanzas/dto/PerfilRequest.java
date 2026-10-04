package edu.co.icesi.finanzas.dto;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.Estado;
import java.time.LocalDate;
import java.util.Set;
public record PerfilRequest(@NotBlank @Size(max=150) String nombreCompleto, @Size(max=500) String fotoUrl, @Size(max=150) String programaAcademico, @NotNull @Positive Long monedaId) {}
