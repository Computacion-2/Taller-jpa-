package edu.co.icesi.finanzas.api.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.*;
public record DeudaRequest(
    @NotNull @Positive Long usuarioId,
    @Positive Long contraparteUsuarioId,
    @Size(max = 150) String contraparteNombre,
    @NotNull TipoDeuda tipo,
    @NotNull @Positive @Digits(integer = 13, fraction = 2) BigDecimal montoOriginal,
    @NotNull LocalDate fechaOrigen,
    @NotNull LocalDate fechaLimite,
    @Size(max = 500) String descripcion
) {}
