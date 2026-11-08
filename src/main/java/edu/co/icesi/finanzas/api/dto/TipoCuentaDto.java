package edu.co.icesi.finanzas.api.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.*;
public record TipoCuentaDto(
    Long id,
    String nombre,
    String descripcion,
    Estado estado
) {}
