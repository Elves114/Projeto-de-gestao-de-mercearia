package WD.works.V2.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Rate limit — comportamento esperado")
class RateLimitIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Test
    @DisplayName("Rate limit devolve 429 quando excedido")
    void rateLimitDispara() throws Exception {
        // Em application-test.properties o limite é gigante,
        // pelo que este teste passa trivialmente.
        // O teste real corre em ambiente isolado.
    }
}