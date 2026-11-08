package edu.co.icesi.finanzas.api.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.*;
public record MovimientoRecurrenteRequest(
    @NotNull @Positive Long usuarioId,
    @NotNull @Positive Long cuentaId,
    @NotNull @Positive Long categoriaId,
    @NotNull TipoMovimiento tipo,
    @NotNull @Positive @Digits(integer = 13, fraction = 2) BigDecimal monto,
    @NotNull Periodicidad periodicidad,
    @NotNull LocalDate fechaInicio,
     LocalDate fechaCorte,
    @NotNull LocalDate proximaOcurrencia,
    @NotNull EstadoRecurrente estado,
    @Size(max = 500) String descripcion
) {}
