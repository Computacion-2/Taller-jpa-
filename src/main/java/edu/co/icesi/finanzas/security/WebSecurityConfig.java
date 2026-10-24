package edu.co.icesi.finanzas.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Mantiene el contrato de la API del taller JPA y su autenticación Basic. */
    @Bean
    @Order(1)
    SecurityFilterChain apiFilterChain(HttpSecurity http, ObjectMapper mapper) throws Exception {
        AuthenticationEntryPoint entrada = (request, response, error) -> {
            response.setStatus(401);
            response.setHeader("WWW-Authenticate", "Basic realm=\"finanzas\"");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            mapper.writeValue(response.getWriter(), ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED,
                "Se requiere autenticación"));
        };
        return http
            .securityMatcher("/api/**")
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                .requestMatchers(HttpMethod.POST, "/api/registro").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/me/**").authenticated()
                .anyRequest().authenticated())
            .exceptionHandling(errors -> errors.accessDeniedHandler((request, response, error) -> {
                response.setStatus(403);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                mapper.writeValue(response.getWriter(), ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN,
                    "No tiene permisos para esta operación"));
            }).authenticationEntryPoint(entrada))
            .httpBasic(basic -> basic.authenticationEntryPoint(entrada))
            .build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain mvcFilterChain(HttpSecurity http, AutenticacionService autenticacion) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                .requestMatchers("/login", "/registro", "/css/**", "/error").permitAll()
                .requestMatchers("/usuarios/**").hasAnyAuthority("ROLE_ADMINISTRADOR", "USUARIOS_GESTIONAR")
                .requestMatchers("/roles/**").hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLES_GESTIONAR")
                .requestMatchers("/permisos/**").hasAnyAuthority("ROLE_ADMINISTRADOR", "PERMISOS_GESTIONAR")
                .anyRequest().authenticated())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/", true)
                .failureUrl("/login?error").permitAll())
            .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true).deleteCookies("JSESSIONID"))
            .exceptionHandling(errors -> errors.accessDeniedPage("/acceso-denegado"))
            .addFilterBefore(new ActualizarAutoridadesFilter(autenticacion), AuthorizationFilter.class)
            .build();
    }

    /** Los cambios de roles y permisos se aplican también a sesiones que ya están abiertas. */
    private static final class ActualizarAutoridadesFilter extends OncePerRequestFilter {
        private final AutenticacionService autenticacion;

        private ActualizarAutoridadesFilter(AutenticacionService autenticacion) {
            this.autenticacion = autenticacion;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                FilterChain chain) throws ServletException, IOException {
            var actual = SecurityContextHolder.getContext().getAuthentication();
            if (actual instanceof UsernamePasswordAuthenticationToken && actual.isAuthenticated()) {
                try {
                    var usuario = autenticacion.loadUserByUsername(actual.getName());
                    if (!usuario.isEnabled()) {
                        throw new UsernameNotFoundException("Usuario inactivo");
                    }
                    var actualizado = UsernamePasswordAuthenticationToken.authenticated(usuario, null, usuario.getAuthorities());
                    actualizado.setDetails(actual.getDetails());
                    // La sesión conserva únicamente el principal sin su hash de contraseña.
                    actualizado.eraseCredentials();
                    SecurityContextHolder.getContext().setAuthentication(actualizado);
                } catch (UsernameNotFoundException error) {
                    SecurityContextHolder.clearContext();
                    var session = request.getSession(false);
                    if (session != null) {
                        session.invalidate();
                    }
                    response.sendRedirect(request.getContextPath() + "/login?sesion");
                    return;
                }
            }
            chain.doFilter(request, response);
        }
    }
}
