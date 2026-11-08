package edu.co.icesi.finanzas.api.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.*;
public record PlanAhorroRequest(
    @NotNull @Positive Long propietarioId,
    @NotBlank @Size(max = 150) String nombre,
    @Size(max = 500) String descripcion,
    @NotNull @Positive @Digits(integer = 13, fraction = 2) BigDecimal montoObjetivo,
    @NotNull LocalDate fechaObjetivo,
    @NotNull TipoPlan tipo,
    @NotNull EstadoPlan estado
) {}
