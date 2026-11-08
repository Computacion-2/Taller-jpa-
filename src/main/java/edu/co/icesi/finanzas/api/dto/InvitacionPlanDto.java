package edu.co.icesi.finanzas.api.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.*;
public record InvitacionPlanDto(
    Long id,
    Long planId,
    Long usuarioInvitadoId,
    Long usuarioInvitadorId,
    EstadoInvitacion estado,
    LocalDate fechaInvitacion,
    LocalDate fechaRespuesta
) {}
