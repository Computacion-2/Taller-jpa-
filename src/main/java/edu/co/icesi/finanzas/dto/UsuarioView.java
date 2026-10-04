package edu.co.icesi.finanzas.dto;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.Estado;
import java.time.LocalDate;
import java.util.Set;
public record UsuarioView(Long id, String nombreCompleto, String correo, String fotoUrl, String programaAcademico, Long monedaId, Estado estado, LocalDate fechaRegistro, LocalDate fechaActualizacion, Set<Long> rolIds) {}
