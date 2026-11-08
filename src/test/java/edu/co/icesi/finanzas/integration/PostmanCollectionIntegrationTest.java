package edu.co.icesi.finanzas.integration;

import com.fasterxml.jackson.databind.*;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:postman;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
@ActiveProfiles("demo")
class PostmanCollectionIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    @Timeout(120)
    void coleccionCompletaConSusScripts() throws Exception {
        Process runner;
        try {
            runner = new ProcessBuilder("node", "scripts/verificar-api.mjs",
                "postman/Local.postman_environment.json", "http://localhost", "--mockmvc")
                .redirectErrorStream(true).start();
        } catch (IOException unavailable) {
            Assumptions.assumeTrue(false, "Node.js no disponible; ejecutar la coleccion desde Postman");
            return;
        }
        int requests = 0;
        StringBuilder output = new StringBuilder();
        try (var reader = runner.inputReader(StandardCharsets.UTF_8);
             var writer = runner.outputWriter(StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("@HTTP:")) { output.append(line).append('\n'); continue; }
                JsonNode payload = mapper.readTree(line.substring(6));
                var call = request(HttpMethod.valueOf(payload.get("method").asText()), URI.create(payload.get("url").asText()));
                payload.get("headers").fields().forEachRemaining(header -> call.header(header.getKey(), header.getValue().asText()));
                if (payload.has("body")) call.content(payload.get("body").asText());
                var response = mvc.perform(call).andReturn().getResponse();
                var reply = mapper.createObjectNode().put("status", response.getStatus())
                    .put("body", response.getContentAsString(StandardCharsets.UTF_8));
                writer.write(mapper.writeValueAsString(reply));
                writer.newLine();
                writer.flush();
                requests++;
            }
            assertTrue(runner.waitFor(10, TimeUnit.SECONDS), "El ejecutor no termino");
            assertEquals(0, runner.exitValue(), output.toString());
            int expected = mapper.readTree(Files.readString(Path.of("postman/Finanzas.postman_collection.json"))).get("item").size();
            assertEquals(expected, requests, output.toString());
            System.out.println("Coleccion Postman: " + requests + " solicitudes verificadas con MockMvc.");
        } finally {
            runner.destroyForcibly();
        }
    }
}
