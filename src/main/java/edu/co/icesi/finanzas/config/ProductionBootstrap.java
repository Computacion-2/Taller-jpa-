package edu.co.icesi.finanzas.config;

import edu.co.icesi.finanzas.dto.UsuarioCrear;
import edu.co.icesi.finanzas.repo.UsuarioRepository;
import edu.co.icesi.finanzas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.*;
import java.util.Set;

/** Crea el administrador inicial solo en una base de produccion vacia. */
@Configuration
@Profile("prod")
public class ProductionBootstrap {
    @Bean
    ApplicationRunner bootstrap(UsuarioRepository repository, UsuarioService service,
            @Value("${BOOTSTRAP_ADMIN_EMAIL:}") String correo,
            @Value("${BOOTSTRAP_ADMIN_PASSWORD:}") String password) {
        return args -> {
            if (repository.count() == 0) {
                if (correo.isBlank() || password.isBlank()) {
                    throw new IllegalStateException("Primer arranque: configure BOOTSTRAP_ADMIN_EMAIL y BOOTSTRAP_ADMIN_PASSWORD");
                }
                service.crear(new UsuarioCrear("Administrador inicial", correo, password, 1L, Set.of(1L)));
            }
        };
    }
}
