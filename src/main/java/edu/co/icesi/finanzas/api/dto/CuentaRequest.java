package edu.co.icesi.finanzas.api.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.*;
public record CuentaRequest(
    @NotNull @Positive Long usuarioId,
    @NotNull @Positive Long tipoCuentaId,
    @NotBlank @Size(max = 100) String nombre,
    @NotNull @PositiveOrZero @Digits(integer = 13, fraction = 2) BigDecimal saldoInicial,
    @NotNull EstadoCuenta estado
) {}
