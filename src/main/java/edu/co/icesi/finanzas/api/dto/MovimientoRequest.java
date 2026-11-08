package edu.co.icesi.finanzas.api.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.*;
public record MovimientoRequest(
    @NotNull @Positive Long cuentaId,
    @NotNull @Positive Long categoriaId,
    @NotNull TipoMovimiento tipo,
    @NotNull @Positive @Digits(integer = 13, fraction = 2) BigDecimal monto,
    @NotNull LocalDate fechaMovimiento,
    @Size(max = 500) String descripcion,
    @Size(max = 500) String soporteUrl
) {}
