package edu.co.icesi.finanzas.api.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.*;
public record DeudaDto(
    Long id,
    Long usuarioId,
    Long contraparteUsuarioId,
    String contraparteNombre,
    TipoDeuda tipo,
    BigDecimal montoOriginal,
    LocalDate fechaOrigen,
    LocalDate fechaLimite,
    String descripcion,
    EstadoDeuda estado,
    BigDecimal saldoPendiente
) {}
