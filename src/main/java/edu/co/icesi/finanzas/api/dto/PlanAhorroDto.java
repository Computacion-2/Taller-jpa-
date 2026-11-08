package edu.co.icesi.finanzas.api.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.*;
public record PlanAhorroDto(
    Long id,
    Long propietarioId,
    String nombre,
    String descripcion,
    BigDecimal montoObjetivo,
    LocalDate fechaObjetivo,
    TipoPlan tipo,
    EstadoPlan estado,
    LocalDate fechaCreacion
) {}
