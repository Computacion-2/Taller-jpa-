package edu.co.icesi.finanzas.security;

import edu.co.icesi.finanzas.entity.enums.Estado;
import edu.co.icesi.finanzas.repo.UsuarioRepository;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AutenticacionService implements UserDetailsService {
    private final UsuarioRepository usuarios;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String correo) {
        var usuario = usuarios.findForLogin(correo)
            .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas"));
        Set<String> autoridades = new LinkedHashSet<>();
        for (var asignacion : usuario.getRoles()) {
            var rol = asignacion.getRol();
            if (rol.getEstado() == Estado.ACTIVO) {
                autoridades.add("ROLE_" + rol.getNombre().trim().toUpperCase(Locale.ROOT));
                // Se inicializa cada colección dentro de la transacción, sin unir dos listas JPA.
                for (var asociacion : rol.getPermisos()) {
                    var permiso = asociacion.getPermiso();
                    if (permiso.getEstado() == Estado.ACTIVO) {
                        autoridades.add(permiso.getNombre());
                    }
                }
            }
        }
        return User.withUsername(usuario.getCorreo()).password(usuario.getPasswordHash())
            .disabled(usuario.getEstado() != Estado.ACTIVO)
            .authorities(autoridades.toArray(String[]::new)).build();
    }
}
