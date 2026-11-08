package edu.co.icesi.finanzas.api.dto;
import java.time.Instant;
import java.util.List;
public record AuthResponse(String accessToken, String tokenType, Instant issuedAt,
        Instant expiresAt, String correo, List<String> roles) {}
