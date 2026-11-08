package edu.co.icesi.finanzas.api.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.*;
public record PresupuestoRequest(
    @NotNull @Positive Long usuarioId,
    @NotNull @Positive Long categoriaId,
    @NotNull @Min(1900) @Max(9999) Integer anio,
    @NotNull @Min(1) @Max(12) Integer mes,
    @NotNull @Positive @Digits(integer = 13, fraction = 2) BigDecimal montoLimite
) {}
