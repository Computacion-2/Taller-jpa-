package edu.co.icesi.finanzas.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

public class SecurityFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    private final AutenticacionService users;
    private final ObjectMapper mapper;

    public SecurityFilter(JwtService jwt, AutenticacionService users, ObjectMapper mapper) {
        this.jwt = jwt;
        this.users = users;
        this.mapper = mapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null) {
            try {
                if (!header.regionMatches(true, 0, "Bearer ", 0, 7)) {
                    throw new IllegalArgumentException("Esquema de autenticacion invalido");
                }
                var claims = jwt.validate(header.substring(7).trim());
                var user = users.loadUserByUsername(claims.getSubject());
                if (!user.isEnabled()) throw new UsernameNotFoundException("Usuario inactivo");
                var auth = UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
                auth.eraseCredentials();
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException | IllegalArgumentException | UsernameNotFoundException error) {
                SecurityContextHolder.clearContext();
                response.setStatus(401);
                response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
                response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                mapper.writeValue(response.getWriter(), ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNAUTHORIZED, "Token invalido o expirado"));
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
