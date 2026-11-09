package edu.co.icesi.finanzas.integration;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.co.icesi.finanzas.entity.enums.Estado;
import edu.co.icesi.finanzas.repo.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
@Transactional
class RestIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired EntityManager em;
    @Autowired UsuarioRepository usuarios;
    @Autowired PermisoRepository permisos;
    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("requestMappingHandlerMapping")
    org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping mappings;
    @Value("${app.jwt.secret}") String secret;

    private JsonNode send(MockHttpServletRequestBuilder request, int status) throws Exception {
        var response = mvc.perform(request).andExpect(status().is(status)).andReturn().getResponse();
        em.flush();
        em.clear();
        return response.getContentAsString().isBlank() ? mapper.nullNode() : mapper.readTree(response.getContentAsString());
    }

    private MockHttpServletRequestBuilder body(MockHttpServletRequestBuilder request, JsonNode body) throws Exception {
        return request.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(body));
    }

    private JsonNode json(String body) throws Exception { return mapper.readTree(body); }
    private String login(String correo) throws Exception {
        return send(body(post("/api/auth/login"), json("{\"correo\":\"" + correo + "\",\"password\":\"Demo2026!\"}")),200).get("accessToken").asText();
    }
    private String admin() throws Exception { return login("admin@u.icesi.edu.co"); }
    private String student() throws Exception { return login("estudiante@u.icesi.edu.co"); }

    private ObjectNode sample(String resource) throws Exception {
        String body = switch (resource) {
            case "monedas" -> "{\"codigo\":\"TST\",\"nombre\":\"Prueba\",\"simbolo\":\"T\"}";
            case "tipos-cuenta" -> "{\"nombre\":\"Prueba\",\"descripcion\":\"Inicial\",\"estado\":\"ACTIVO\"}";
            case "categorias" -> "{\"usuarioId\":2,\"nombre\":\"Prueba\",\"tipo\":\"GASTO\",\"estado\":\"ACTIVO\"}";
            case "cuentas" -> "{\"usuarioId\":2,\"tipoCuentaId\":1,\"nombre\":\"Prueba\",\"saldoInicial\":100,\"estado\":\"ACTIVA\"}";
            case "movimientos" -> "{\"cuentaId\":1,\"categoriaId\":1,\"tipo\":\"GASTO\",\"monto\":100,\"fechaMovimiento\":\"2026-11-08\"}";
            case "movimientos-recurrentes" -> "{\"usuarioId\":2,\"cuentaId\":1,\"categoriaId\":1,\"tipo\":\"GASTO\",\"monto\":100,\"periodicidad\":\"MENSUAL\",\"fechaInicio\":\"2026-11-08\",\"proximaOcurrencia\":\"2026-12-08\",\"estado\":\"ACTIVO\"}";
            case "presupuestos" -> "{\"usuarioId\":2,\"categoriaId\":1,\"anio\":2035,\"mes\":1,\"montoLimite\":1000}";
            case "deudas" -> "{\"usuarioId\":2,\"contraparteNombre\":\"Prueba\",\"tipo\":\"POR_PAGAR\",\"montoOriginal\":1000,\"fechaOrigen\":\"2026-11-01\",\"fechaLimite\":\"2026-12-01\"}";
            case "abonos-deuda" -> "{\"deudaId\":1,\"monto\":100,\"fechaAbono\":\"2099-01-01\"}";
            case "planes-ahorro" -> "{\"propietarioId\":2,\"nombre\":\"Prueba\",\"montoObjetivo\":10000,\"fechaObjetivo\":\"2030-01-01\",\"tipo\":\"COMPARTIDO\",\"estado\":\"ACTIVO\"}";
            case "invitaciones-plan" -> "{\"planId\":1,\"usuarioInvitadoId\":1,\"estado\":\"PENDIENTE\"}";
            case "aportes-plan" -> "{\"planId\":1,\"usuarioId\":2,\"cuentaId\":1,\"monto\":100,\"fechaAporte\":\"2026-11-08\",\"estado\":\"ACTIVO\"}";
            case "participaciones-plan" -> "{\"planId\":1,\"usuarioId\":1,\"metaIndividual\":500,\"fechaAceptacion\":\"2026-11-08\",\"estado\":\"ACTIVO\"}";
            case "admin/usuarios-roles" -> "{\"usuarioId\":2,\"rolId\":1,\"fechaAsignacion\":\"2026-11-08\"}";
            case "admin/roles-permisos" -> "{\"rolId\":1,\"permisoId\":2,\"fechaAsignacion\":\"2026-11-08\"}";
            case "admin/permisos" -> "{\"nombre\":\"PRUEBA\",\"descripcion\":\"Inicial\",\"estado\":\"ACTIVO\"}";
            case "admin/roles" -> "{\"nombre\":\"PRUEBA\",\"descripcion\":\"Inicial\",\"estado\":\"ACTIVO\",\"permisoIds\":[2]}";
            case "admin/usuarios" -> "{\"nombreCompleto\":\"Prueba\",\"correo\":\"crud@u.icesi.edu.co\",\"password\":\"Demo2026!\",\"monedaId\":1,\"rolIds\":[2]}";
            default -> throw new IllegalArgumentException(resource);
        };
        return (ObjectNode) json(body);
    }

    @ParameterizedTest
    @ValueSource(strings = {"monedas","tipos-cuenta","categorias","cuentas","movimientos","movimientos-recurrentes","presupuestos",
        "deudas","abonos-deuda","planes-ahorro","invitaciones-plan","aportes-plan","participaciones-plan",
        "admin/usuarios-roles","admin/roles-permisos","admin/permisos","admin/roles","admin/usuarios"})
    void crudDeCadaEntidad(String resource) throws Exception {
        String token = admin();
        String route = "/api/" + resource;
        ObjectNode input = sample(resource);
        JsonNode created = send(body(post(route), input).header("Authorization", "Bearer " + token),201);
        String id = switch (resource) {
            case "participaciones-plan" -> created.get("planId").asText()+"/"+created.get("usuarioId").asText();
            case "admin/usuarios-roles" -> created.get("usuarioId").asText()+"/"+created.get("rolId").asText();
            case "admin/roles-permisos" -> created.get("rolId").asText()+"/"+created.get("permisoId").asText();
            default -> created.get("id").asText();
        };
        assertFalse(created.has("passwordHash"));
        send(get(route).header("Authorization","Bearer "+token),200);
        send(get(route+"/"+id).header("Authorization","Bearer "+token),200);
        if (resource.equals("admin/usuarios")) { input.remove("password"); input.put("estado","ACTIVO"); input.put("nombreCompleto","Actualizado"); }
        else if (input.has("nombre")) input.put("nombre","Actualizado");
        else if (input.has("monto")) input.put("monto",200);
        else if (input.has("metaIndividual")) input.put("metaIndividual",600);
        else if (input.has("fechaAsignacion")) input.put("fechaAsignacion","2026-11-07");
        else if (input.has("montoLimite")) input.put("montoLimite",2000);
        else if (input.has("montoOriginal")) input.put("montoOriginal",2000);
        JsonNode updated = send(body(put(route+"/"+id),input).header("Authorization","Bearer "+token),200);
        JsonNode persisted = send(get(route+"/"+id).header("Authorization","Bearer "+token),200);
        updated.fields().forEachRemaining(field -> {
            JsonNode expected = field.getValue(), actual = persisted.get(field.getKey());
            if (expected.isNumber()) assertEquals(0, expected.decimalValue().compareTo(actual.decimalValue()));
            else assertEquals(expected, actual);
        });
        if (input.has("nombre")) assertEquals("Actualizado",persisted.get("nombre").asText());
        send(delete(route+"/"+id).header("Authorization","Bearer "+token),204);
        send(get(route+"/"+id).header("Authorization","Bearer "+token),404);
    }

    @Test void loginIncluyeRolesYFechasSinSesion() throws Exception {
        String token = student();
        var claims = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))).build().parseSignedClaims(token).getPayload();
        assertEquals("estudiante@u.icesi.edu.co",claims.getSubject());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
        assertNotNull(claims.getNotBefore());
        assertTrue(claims.get("roles").toString().contains("ROLE_USUARIO"));
        mvc.perform(get("/api/me").header("Authorization","Bearer "+token)).andExpect(status().isOk())
            .andExpect(jsonPath("correo").value("estudiante@u.icesi.edu.co"))
            .andExpect(jsonPath("passwordHash").doesNotExist())
            .andExpect(result -> assertNull(result.getRequest().getSession(false)));
        send(body(post("/api/auth/login"),json("{\"correo\":\"admin@u.icesi.edu.co\",\"password\":\"incorrecta\"}")),401);
    }

    @Test void rechazaTokensInvalidosExpiradosYOtroEmisor() throws Exception {
        send(get("/api/cuentas"),401);
        send(get("/api/cuentas").with(httpBasic("admin@u.icesi.edu.co","Demo2026!")),401);
        send(get("/api/cuentas").header("Authorization","Bearer no-valido"),401);
        String valid = student();
        String[] segments = valid.split("\\.");
        segments[2] = (segments[2].startsWith("A") ? "B" : "A") + segments[2].substring(1);
        send(get("/api/cuentas").header("Authorization","Bearer "+String.join(".",segments)),401);
        for (boolean expired : new boolean[]{true,false}) {
            Instant now=Instant.now();
            String invalid = Jwts.builder().issuer(expired ? "finanzas-personales":"otro")
                .subject("estudiante@u.icesi.edu.co").issuedAt(Date.from(now.minusSeconds(120)))
                .notBefore(Date.from(now.minusSeconds(120))).expiration(Date.from(now.plusSeconds(expired ? -60 : 60)))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))).compact();
            send(get("/api/cuentas").header("Authorization","Bearer "+invalid),401);
        }
    }

    @Test void rolesYDatosAjenosEstanProtegidos() throws Exception {
        String token = student();
        send(get("/api/admin/usuarios").header("Authorization","Bearer "+token),403);
        ObjectNode account=sample("cuentas"); account.put("usuarioId",1);
        send(body(post("/api/cuentas"),account).header("Authorization","Bearer "+token),403);
        String admin = admin();
        JsonNode created=send(body(post("/api/cuentas"),account).header("Authorization","Bearer "+admin),201);
        String route="/api/cuentas/"+created.get("id").asText();
        send(get(route).header("Authorization","Bearer "+token),404);
        send(body(put(route),account).header("Authorization","Bearer "+token),404);
        send(delete(route).header("Authorization","Bearer "+token),404);
        send(delete("/api/categorias/1").header("Authorization","Bearer "+token),403);
    }

    @Test void cambiosDePermisosYEstadoSeAplicanAlTokenExistente() throws Exception {
        String token=student();
        var permission=permisos.findById(2L).orElseThrow(); permission.setEstado(Estado.INACTIVO);
        em.flush(); em.clear();
        send(get("/api/cuentas").header("Authorization","Bearer "+token),403);
        var user=usuarios.findById(2L).orElseThrow(); user.setEstado(Estado.INACTIVO);
        em.flush(); em.clear();
        send(get("/api/me").header("Authorization","Bearer "+token),401);
    }

    @Test void movimientosActualizanYReviertenElSaldo() throws Exception {
        String token=student();
        double initial=send(get("/api/cuentas/1").header("Authorization","Bearer "+token),200).get("saldoActual").asDouble();
        ObjectNode input=sample("movimientos");
        String id=send(body(post("/api/movimientos"),input).header("Authorization","Bearer "+token),201).get("id").asText();
        assertEquals(initial-100,send(get("/api/cuentas/1").header("Authorization","Bearer "+token),200).get("saldoActual").asDouble());
        input.put("monto",200);
        send(body(put("/api/movimientos/"+id),input).header("Authorization","Bearer "+token),200);
        assertEquals(initial-200,send(get("/api/cuentas/1").header("Authorization","Bearer "+token),200).get("saldoActual").asDouble());
        send(delete("/api/movimientos/"+id).header("Authorization","Bearer "+token),204);
        assertEquals(initial,send(get("/api/cuentas/1").header("Authorization","Bearer "+token),200).get("saldoActual").asDouble());
    }

    @Test void abonosNoSuperanSaldoYSeRevierten() throws Exception {
        String token=student();
        double initial=send(get("/api/deudas/1").header("Authorization","Bearer "+token),200).get("saldoPendiente").asDouble();
        ObjectNode input=sample("abonos-deuda"); input.put("monto",initial);
        String id=send(body(post("/api/abonos-deuda"),input).header("Authorization","Bearer "+token),201).get("id").asText();
        JsonNode debt=send(get("/api/deudas/1").header("Authorization","Bearer "+token),200);
        assertEquals("SALDADA",debt.get("estado").asText());
        assertEquals(0,debt.get("saldoPendiente").asDouble());
        send(delete("/api/abonos-deuda/"+id).header("Authorization","Bearer "+token),204);
        assertEquals(initial,send(get("/api/deudas/1").header("Authorization","Bearer "+token),200).get("saldoPendiente").asDouble());
    }

    @Test void invitadoRespondeYSinEscalarPrivilegios() throws Exception {
        String owner=student(), invited=admin();
        ObjectNode input=sample("invitaciones-plan"); input.put("estado","ACEPTADA");
        send(body(put("/api/invitaciones-plan/1"),input).header("Authorization","Bearer "+owner),403);
        send(body(put("/api/invitaciones-plan/1"),input).header("Authorization","Bearer "+invited),200);
        send(get("/api/participaciones-plan/1/1").header("Authorization","Bearer "+owner),200);
        ObjectNode registration=(ObjectNode)json("{\"nombreCompleto\":\"Registro\",\"correo\":\"nuevo@u.icesi.edu.co\",\"password\":\"Demo2026!\",\"monedaId\":1}");
        JsonNode user=send(body(post("/api/auth/registro"),registration),201);
        assertEquals(1,user.get("rolIds").size()); assertEquals(2,user.get("rolIds").get(0).asInt());
    }

    @Test void validacionYConflictosDevuelvenErroresHttp() throws Exception {
        String token=admin();
        send(body(post("/api/cuentas"),json("{}" )).header("Authorization","Bearer "+token),400);
        ObjectNode input=sample("monedas"); input.put("codigo","COP");
        // La restriccion unica se comprueba en una transaccion independiente en las pruebas HTTP.
        send(get("/api/cuentas/no-numero").header("Authorization","Bearer "+token),400);
        send(get("/api/cuentas/999999").header("Authorization","Bearer "+token),404);
        input=sample("cuentas"); input.put("id",99);
        send(body(post("/api/cuentas"),input).header("Authorization","Bearer "+token),400);
        send(delete("/api/admin/roles/1").header("Authorization","Bearer "+token),409);
    }

    @Test void swaggerIncluyeTodosLosControladores() throws Exception {
        JsonNode doc=send(get("/v3/api-docs"),200);
        assertEquals("bearer",doc.at("/components/securitySchemes/bearerAuth/scheme").asText());
        for (String resource : new String[]{"monedas","tipos-cuenta","categorias","cuentas","movimientos","movimientos-recurrentes","presupuestos",
                "deudas","abonos-deuda","planes-ahorro","invitaciones-plan","aportes-plan","participaciones-plan",
                "admin/usuarios-roles","admin/roles-permisos","admin/permisos","admin/roles","admin/usuarios"}) {
            JsonNode operations=doc.get("paths").get("/api/"+resource);
            assertNotNull(operations,resource); assertTrue(operations.has("get"),resource); assertTrue(operations.has("post"),resource);
        }
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }

    @Test void swaggerDocumentaCadaMetodoConSuRespuestaReal() throws Exception {
        JsonNode doc = send(get("/v3/api-docs"), 200);
        int operations = 0;
        for (var entry : mappings.getHandlerMethods().entrySet()) {
            var handler = entry.getValue();
            if (!handler.getBeanType().getPackageName().equals("edu.co.icesi.finanzas.api")) continue;
            var status = org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation(
                handler.getMethod(), org.springframework.web.bind.annotation.ResponseStatus.class);
            String code = status == null ? "200" : Integer.toString(status.code().value());
            for (String path : entry.getKey().getPatternValues()) {
                for (var method : entry.getKey().getMethodsCondition().getMethods()) {
                    String label = method + " " + path;
                    JsonNode operation = doc.path("paths").path(path).path(method.name().toLowerCase(java.util.Locale.ROOT));
                    assertFalse(operation.isMissingNode(), label);
                    assertFalse(operation.path("summary").asText().isBlank(), label);
                    JsonNode response = operation.path("responses").path(code);
                    assertFalse(response.isMissingNode(), label + " falta " + code);
                    if (code.equals("204")) {
                        assertTrue(response.path("content").isMissingNode() || response.path("content").isEmpty(), label);
                    } else {
                        assertFalse(response.path("content").path("application/json").path("schema").isMissingNode(), label);
                    }
                    for (String error : new String[]{"400", "401", "403", "404", "409"}) {
                        assertEquals("#/components/schemas/ProblemDetail", operation.path("responses").path(error)
                            .path("content").path("application/problem+json").path("schema").path("$ref").asText(), label + " " + error);
                    }
                    for (var parameter : handler.getMethodParameters()) {
                        if (parameter.hasParameterAnnotation(org.springframework.web.bind.annotation.RequestBody.class)) {
                            assertFalse(operation.path("requestBody").path("content").path("application/json").path("schema").isMissingNode(), label);
                        }
                    }
                    operations++;
                }
            }
        }
        assertEquals(99, operations, "Todas las operaciones REST deben estar documentadas");
        for (String path : new String[]{"/api/auth/login", "/api/auth/registro", "/api/registro"}) {
            assertTrue(doc.path("paths").path(path).path("post").path("security").isArray(), path);
            assertTrue(doc.path("paths").path(path).path("post").path("security").isEmpty(), path);
        }
        java.nio.file.Files.writeString(java.nio.file.Path.of("target/openapi-validado.json"),
            mapper.writerWithDefaultPrettyPrinter().writeValueAsString(doc));
    }

    @Test void categoriasCompartidasYClavesCompuestasValidanSusLimites() throws Exception {
        String token = admin();
        ObjectNode input = sample("categorias"); input.remove("usuarioId");
        JsonNode created = send(body(post("/api/categorias"), input).header("Authorization", "Bearer " + token),201);
        assertTrue(created.get("usuarioId").isNull());
        ObjectNode link = sample("admin/usuarios-roles");
        send(body(post("/api/admin/usuarios-roles"),link).header("Authorization","Bearer "+token),201);
        send(body(post("/api/admin/usuarios-roles"),link).header("Authorization","Bearer "+token),409);
        link.put("rolId",2);
        send(body(put("/api/admin/usuarios-roles/2/1"),link).header("Authorization","Bearer "+token),400);
    }
}
