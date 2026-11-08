package edu.co.icesi.finanzas.api.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.*;
public record CuentaDto(
    Long id,
    Long usuarioId,
    Long tipoCuentaId,
    String nombre,
    BigDecimal saldoInicial,
    BigDecimal saldoActual,
    EstadoCuenta estado,
    LocalDate fechaCreacion,
    LocalDate fechaArchivado
) {}
