package edu.co.icesi.finanzas.integration;

import edu.co.icesi.finanzas.dto.*;
import edu.co.icesi.finanzas.entity.enums.Estado;
import edu.co.icesi.finanzas.repo.*;
import edu.co.icesi.finanzas.service.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
@Transactional
class MvcIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired UsuarioRepository usuarios;
    @Autowired UsuarioService usuarioService;
    @Autowired RolService rolService;
    @Autowired PermisoService permisoService;
    @Autowired PasswordEncoder encoder;
    @Autowired EntityManager em;

    private MockHttpSession login(String correo) throws Exception {
        return (MockHttpSession) mvc.perform(post("/login").with(csrf())
                .param("username", correo).param("password", "Demo2026!"))
            .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/"))
            .andReturn().getRequest().getSession(false);
    }
    private MockHttpSession admin() throws Exception { return login("admin@u.icesi.edu.co"); }
    private void flush() { em.flush(); em.clear(); }

    @Test void paginasPublicasYModuloProtegido() throws Exception {
        for (String ruta : new String[]{"/login", "/registro"}) {
            mvc.perform(get(ruta)).andExpect(status().isOk())
                .andExpect(content().string(containsString("/css/app.css")))
                .andExpect(content().string(containsString("name=\"_csrf\"")));
        }
        mvc.perform(get("/css/app.css")).andExpect(status().isOk());
        for (String ruta : new String[]{"/", "/usuarios", "/roles", "/permisos"}) {
            mvc.perform(get(ruta)).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        }
    }

    @Test void loginLogoutYCsrfReales() throws Exception {
        mvc.perform(post("/login").param("username", "admin@u.icesi.edu.co").param("password", "Demo2026!"))
            .andExpect(status().isForbidden());
        mvc.perform(post("/login").with(csrf()).param("username", "admin@u.icesi.edu.co").param("password", "incorrecta"))
            .andExpect(redirectedUrl("/login?error"));
        var sesion = admin();
        mvc.perform(get("/").session(sesion)).andExpect(status().isOk());
        mvc.perform(post("/roles").session(sesion).param("nombre", "SIN_CSRF").param("estado", "ACTIVO").param("permisoIds", "1"))
            .andExpect(status().isForbidden());
        mvc.perform(post("/acceso-denegado").session(sesion).with(csrf()))
            .andExpect(status().isForbidden()).andExpect(view().name("auth/acceso-denegado"));
        mvc.perform(post("/logout").session(sesion).with(csrf())).andExpect(status().is3xxRedirection());
        assertTrue(sesion.isInvalid());
    }

    @Test void todasLasVistasDeGestionRenderizanConCsrf() throws Exception {
        var sesion = admin();
        for (String ruta : new String[]{"/usuarios", "/usuarios/nuevo", "/usuarios/2/editar", "/roles", "/roles/nuevo", "/roles/2/editar", "/permisos", "/permisos/nuevo", "/permisos/2/editar"}) {
            mvc.perform(get(ruta).session(sesion)).andExpect(status().isOk())
                .andExpect(content().string(containsString("/css/app.css")))
                .andExpect(content().string(containsString("name=\"_csrf\"")));
        }
        mvc.perform(get("/usuarios").session(sesion)).andExpect(content().string(containsString(">USUARIO</span>")));
        mvc.perform(get("/roles").session(sesion)).andExpect(content().string(containsString(">ADMINISTRAR_CUENTAS</span>")));
    }

    @Test void usuarioNormalNoVeNiEjecutaGestion() throws Exception {
        var sesion = login("estudiante@u.icesi.edu.co");
        mvc.perform(get("/").session(sesion)).andExpect(status().isOk())
            .andExpect(content().string(not(containsString("href=\"/usuarios\""))));
        for (String ruta : new String[]{"/usuarios", "/roles", "/permisos"}) {
            mvc.perform(get(ruta).session(sesion)).andExpect(status().isForbidden());
            mvc.perform(post(ruta).session(sesion).with(csrf())).andExpect(status().isForbidden());
        }
    }

    @Test void registroRedirigeGuardaHashYSoloAsignaRolUsuario() throws Exception {
        mvc.perform(post("/registro").with(csrf()).param("nombreCompleto", "Nueva estudiante")
                .param("correo", "nueva@u.icesi.edu.co").param("password", "Demo2026!").param("monedaId", "1")
                .param("rolIds", "1"))
            .andExpect(status().is3xxRedirection()).andExpect(redirectedUrlPattern("/login*"));
        flush();
        var nueva = usuarios.findForLogin("nueva@u.icesi.edu.co").orElseThrow();
        assertTrue(encoder.matches("Demo2026!", nueva.getPasswordHash()));
        assertEquals(Set.of(2L), usuarioService.obtener(nueva.getId()).rolIds());
        mvc.perform(get("/").session(login("nueva@u.icesi.edu.co"))).andExpect(status().isOk());
    }

    @Test void formulariosInvalidosUsanPrgSinGuardarPasswordEnSesion() throws Exception {
        MvcResult fallo = mvc.perform(post("/registro").with(csrf()).param("nombreCompleto", "")
                .param("correo", "invalido").param("password", "SecretoQueNoDebePersistir!").param("monedaId", "1"))
            .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/registro")).andReturn();
        assertFalse(fallo.getFlashMap().toString().contains("SecretoQueNoDebePersistir!"));
        mvc.perform(get("/registro").flashAttrs(fallo.getFlashMap())).andExpect(status().isOk());
        assertTrue(usuarios.findByCorreoIgnoreCase("invalido").isEmpty());
        var sesion = admin();
        var rol = mvc.perform(post("/roles").session(sesion).with(csrf()).param("nombre", "VACIO").param("estado", "ACTIVO"))
            .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/roles/nuevo")).andReturn();
        mvc.perform(get("/roles/nuevo").session(sesion).flashAttrs(rol.getFlashMap())).andExpect(status().isOk());
    }

    @Test void crudYAsignacionesDesdeFormularios() throws Exception {
        var sesion = admin();
        mvc.perform(post("/permisos").session(sesion).with(csrf()).param("nombre", "AUDITAR_MVC")
                .param("descripcion", "Auditoría").param("estado", "ACTIVO"))
            .andExpect(redirectedUrl("/permisos"));
        long permiso = permisoService.listar().stream().filter(p -> p.nombre().equals("AUDITAR_MVC")).findFirst().orElseThrow().id();
        mvc.perform(post("/permisos/" + permiso).session(sesion).with(csrf()).param("nombre", "AUDITAR_MVC")
                .param("descripcion", "Actualizado").param("estado", "ACTIVO"))
            .andExpect(redirectedUrl("/permisos"));
        assertEquals("Actualizado", permisoService.obtener(permiso).descripcion());
        mvc.perform(post("/roles").session(sesion).with(csrf()).param("nombre", "AUDITOR_MVC")
                .param("estado", "ACTIVO").param("permisoIds", Long.toString(permiso)))
            .andExpect(redirectedUrl("/roles"));
        long rol = rolService.listar().stream().filter(r -> r.nombre().equals("AUDITOR_MVC")).findFirst().orElseThrow().id();
        flush();
        mvc.perform(post("/roles/" + rol).session(sesion).with(csrf()).param("nombre", "AUDITOR_MVC")
                .param("estado", "ACTIVO").param("permisoIds", Long.toString(permiso), "2"))
            .andExpect(redirectedUrl("/roles"));
        assertEquals(Set.of(permiso, 2L), rolService.obtener(rol).permisoIds());
        mvc.perform(post("/usuarios").session(sesion).with(csrf()).param("nombreCompleto", "Auditora MVC")
                .param("correo", "auditoria@u.icesi.edu.co").param("password", "Demo2026!").param("monedaId", "1")
                .param("rolIds", Long.toString(rol), "2"))
            .andExpect(redirectedUrl("/usuarios"));
        flush();
        long usuario = usuarios.findByCorreoIgnoreCase("auditoria@u.icesi.edu.co").orElseThrow().getId();
        assertEquals(Set.of(rol, 2L), usuarioService.obtener(usuario).rolIds());
        mvc.perform(post("/roles/" + rol + "/eliminar").session(sesion).with(csrf()))
            .andExpect(redirectedUrl("/roles")).andExpect(flash().attributeExists("error"));
        mvc.perform(post("/usuarios/" + usuario).session(sesion).with(csrf()).param("nombreCompleto", "Auditora editada")
                .param("correo", "auditoria@u.icesi.edu.co").param("monedaId", "1").param("estado", "ACTIVO").param("rolIds", "2"))
            .andExpect(redirectedUrl("/usuarios"));
        flush();
        assertEquals(Set.of(2L), usuarioService.obtener(usuario).rolIds());
        mvc.perform(post("/roles/" + rol + "/eliminar").session(sesion).with(csrf())).andExpect(redirectedUrl("/roles"));
        flush();
        mvc.perform(post("/permisos/" + permiso + "/eliminar").session(sesion).with(csrf())).andExpect(redirectedUrl("/permisos"));
        mvc.perform(post("/usuarios/" + usuario + "/eliminar").session(sesion).with(csrf())).andExpect(redirectedUrl("/usuarios"));
        flush();
        assertFalse(usuarios.existsById(usuario));
    }

    @Test void autoridadEspecificaYRevocacionSinVolverAIniciarSesion() throws Exception {
        long permiso = permisoService.crear(new PermisoRequest("ROLES_GESTIONAR", "Gestión delegada", Estado.ACTIVO)).id();
        long rol = rolService.crear(new RolRequest("GESTOR_MVC", "", Estado.ACTIVO, Set.of(permiso))).id();
        long usuario = usuarioService.crear(new UsuarioCrear("Gestor", "gestor@u.icesi.edu.co", "Demo2026!", 1L, Set.of(rol, 2L))).id();
        flush();
        var sesion = login("gestor@u.icesi.edu.co");
        mvc.perform(get("/roles").session(sesion)).andExpect(status().isOk());
        mvc.perform(get("/usuarios").session(sesion)).andExpect(status().isForbidden());
        usuarioService.editar(usuario, new UsuarioEditar("Gestor", "gestor@u.icesi.edu.co", 1L, Estado.ACTIVO, Set.of(2L)));
        flush();
        mvc.perform(get("/roles").session(sesion)).andExpect(status().isForbidden());
    }

    @Test void apiAnteriorConservaContratoYAutenticacionBasic() throws Exception {
        mvc.perform(get("/api/admin/usuarios")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/admin/usuarios").with(httpBasic("admin@u.icesi.edu.co", "Demo2026!")))
            .andExpect(status().isOk()).andExpect(jsonPath("content").isArray());
        mvc.perform(get("/api/admin/roles").with(httpBasic("estudiante@u.icesi.edu.co", "Demo2026!")))
            .andExpect(status().isForbidden());
    }

    @Test void permisoDesactivadoRetiraAccesoEnSesionAbierta() throws Exception {
        long permiso = permisoService.crear(new PermisoRequest("ROLES_GESTIONAR", "Delegado", Estado.ACTIVO)).id();
        long rol = rolService.crear(new RolRequest("GESTOR_PERMISO", "", Estado.ACTIVO, Set.of(permiso))).id();
        usuarioService.crear(new UsuarioCrear("Gestora", "gestora@u.icesi.edu.co", "Demo2026!", 1L, Set.of(rol)));
        flush();
        var sesion = login("gestora@u.icesi.edu.co");
        mvc.perform(get("/roles").session(sesion)).andExpect(status().isOk());
        permisoService.actualizar(permiso, new PermisoRequest("ROLES_GESTIONAR", "Delegado", Estado.INACTIVO));
        flush();
        mvc.perform(get("/roles").session(sesion)).andExpect(status().isForbidden());
    }

    @Test void desactivarCuentaInvalidaLaSesionExistente() throws Exception {
        var sesion = login("estudiante@u.icesi.edu.co");
        usuarioService.cambiarEstado(2L, Estado.INACTIVO);
        flush();
        mvc.perform(get("/").session(sesion)).andExpect(redirectedUrl("/login?sesion"));
        assertTrue(sesion.isInvalid());
    }

    @Test void valoresDeFormularioMalformadosNoProducenErrorInterno() throws Exception {
        var resultado = mvc.perform(post("/registro").with(csrf()).param("nombreCompleto", "Prueba")
                .param("correo", "prueba@u.icesi.edu.co").param("password", "Demo2026!").param("monedaId", "texto"))
            .andExpect(redirectedUrl("/registro")).andReturn();
        mvc.perform(get("/registro").flashAttrs(resultado.getFlashMap())).andExpect(status().isOk());
        mvc.perform(get("/usuarios/999999/editar").session(admin())).andExpect(status().isNotFound());
    }
}
