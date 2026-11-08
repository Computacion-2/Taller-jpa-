package edu.co.icesi.finanzas.api.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.*;
public record AportePlanDto(
    Long id,
    Long planId,
    Long usuarioId,
    Long cuentaId,
    BigDecimal monto,
    LocalDate fechaAporte,
    EstadoAporte estado,
    LocalDate fechaCreacion
) {}
