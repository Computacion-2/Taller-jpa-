package edu.co.icesi.finanzas.security;

import edu.co.icesi.finanzas.api.dto.AuthResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecretKey key;
    private final long duration;
    private static final String ISSUER = "finanzas-personales";

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.duration-seconds:3600}") long duration) {
        if (duration < 1) throw new IllegalArgumentException("Duracion JWT invalida");
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.duration = duration;
    }

    public AuthResponse generateToken(UserDetails user) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(duration);
        var authorities = user.getAuthorities().stream().map(a -> a.getAuthority()).sorted().toList();
        var roles = authorities.stream().filter(a -> a.startsWith("ROLE_")).toList();
        String token = Jwts.builder().issuer(ISSUER).subject(user.getUsername())
            .claim("roles", roles).claim("authorities", authorities)
            .issuedAt(Date.from(now)).notBefore(Date.from(now)).expiration(Date.from(expiration))
            .signWith(key, Jwts.SIG.HS256).compact();
        return new AuthResponse(token, "Bearer", now, expiration, user.getUsername(), roles);
    }

    public Claims validate(String token) {
        Claims claims = Jwts.parser().verifyWith(key).requireIssuer(ISSUER).build()
            .parseSignedClaims(token).getPayload();
        if (claims.getSubject() == null || claims.getIssuedAt() == null || claims.getExpiration() == null
                || claims.getNotBefore() == null || claims.getIssuedAt().after(new Date())
                || !claims.getExpiration().after(claims.getIssuedAt())) {
            throw new IllegalArgumentException("Claims JWT invalidos");
        }
        return claims;
    }
}
