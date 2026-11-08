package edu.co.icesi.finanzas.api.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.*;
public record MovimientoDto(
    Long id,
    Long cuentaId,
    Long categoriaId,
    TipoMovimiento tipo,
    BigDecimal monto,
    LocalDate fechaMovimiento,
    String descripcion,
    String soporteUrl,
    LocalDate fechaCreacion
) {}
