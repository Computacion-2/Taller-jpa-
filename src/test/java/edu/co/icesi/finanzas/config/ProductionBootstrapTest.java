package edu.co.icesi.finanzas.config;

import edu.co.icesi.finanzas.dto.UsuarioCrear;
import edu.co.icesi.finanzas.repo.UsuarioRepository;
import edu.co.icesi.finanzas.service.UsuarioService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Set;

class ProductionBootstrapTest {
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final UsuarioService service = mock(UsuarioService.class);
    private final ProductionBootstrap config = new ProductionBootstrap();

    @Test void baseVaciaExigeCredencialesYDelegaCreacionConHashAlServicio() throws Exception {
        assertThrows(IllegalStateException.class, () -> config.bootstrap(usuarios, service, "", "").run(null));
        assertThrows(IllegalStateException.class, () -> config.bootstrap(usuarios, service, "admin@u.icesi.edu.co", "").run(null));
        config.bootstrap(usuarios, service, "admin@u.icesi.edu.co", "Segura2026!").run(null);
        verify(service).crear(new UsuarioCrear("Administrador inicial", "admin@u.icesi.edu.co", "Segura2026!", 1L, Set.of(1L)));
    }

    @Test void conservaLaCuentaExistenteAlReiniciar() throws Exception {
        when(usuarios.count()).thenReturn(1L);
        config.bootstrap(usuarios, service, "", "").run(null);
        verifyNoInteractions(service);
    }
}
