package edu.co.icesi.finanzas.api.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.*;
public record CategoriaDto(
    Long id,
    Long usuarioId,
    String nombre,
    TipoMovimiento tipo,
    String descripcion,
    Estado estado,
    LocalDate fechaCreacion
) {}
