package edu.co.icesi.finanzas.api.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.*;
public record PresupuestoDto(
    Long id,
    Long usuarioId,
    Long categoriaId,
    Integer anio,
    Integer mes,
    BigDecimal montoLimite,
    LocalDate fechaCreacion
) {}
