package edu.co.icesi.finanzas.api.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.*;
public record CategoriaRequest(
    @Positive Long usuarioId,
    @NotBlank @Size(max = 100) String nombre,
    @NotNull TipoMovimiento tipo,
    @Size(max = 200) String descripcion,
    @NotNull Estado estado
) {}
