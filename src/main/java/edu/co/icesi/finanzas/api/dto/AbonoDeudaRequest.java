package edu.co.icesi.finanzas.api.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.*;
public record AbonoDeudaRequest(
    @NotNull @Positive Long deudaId,
    @NotNull @Positive @Digits(integer = 13, fraction = 2) BigDecimal monto,
    @NotNull LocalDate fechaAbono,
    @Size(max = 300) String descripcion
) {}
