package edu.co.icesi.finanzas.dto;
import jakarta.validation.constraints.*;
import edu.co.icesi.finanzas.entity.enums.Estado;
import java.time.LocalDate;
import java.util.Set;
public record PermisoView(Long id, String nombre, String descripcion, Estado estado) {}
