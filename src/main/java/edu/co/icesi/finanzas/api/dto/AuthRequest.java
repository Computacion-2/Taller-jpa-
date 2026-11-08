package edu.co.icesi.finanzas.api.dto;
import jakarta.validation.constraints.*;
public record AuthRequest(@NotBlank @Email String correo, @NotBlank String password) {}
