package edu.co.icesi.finanzas.api.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.*;
public record MonedaRequest(
    @NotBlank @Size(max = 3) @Pattern(regexp = "[A-Z]{3}") String codigo,
    @NotBlank @Size(max = 50) String nombre,
    @Size(max = 5) String simbolo
) {}
