package edu.co.icesi.finanzas.api.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.*;
public record TipoCuentaRequest(
    @NotBlank @Size(max = 50) String nombre,
    @Size(max = 200) String descripcion,
    @NotNull Estado estado
) {}
