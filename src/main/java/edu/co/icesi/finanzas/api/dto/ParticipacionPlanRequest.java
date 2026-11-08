package edu.co.icesi.finanzas.api.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.*;
public record ParticipacionPlanRequest(
    @NotNull @Positive Long planId,
    @NotNull @Positive Long usuarioId,
    @Positive @Digits(integer = 13, fraction = 2) BigDecimal metaIndividual,
     LocalDate fechaAceptacion,
    @NotNull EstadoParticipacion estado
) {}
