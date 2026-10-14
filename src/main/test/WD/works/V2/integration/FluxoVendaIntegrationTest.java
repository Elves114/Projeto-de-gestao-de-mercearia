package WD.works.V2.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Fluxo de venda ponta a ponta")
class FluxoVendaIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper om;

    @Test
    @DisplayName("Fluxo completo: cadastro → login → categoria → produto → stock → venda")
    void fluxoCompleto() throws Exception {

        String sufixo = UUID.randomUUID().toString().substring(0, 8);
        String nuit = "1" + (int)(Math.random() * 900000000 + 100000000);
        String contacto = "8" + (int)(Math.random() * 90000000 + 10000000);

        // 1) Cadastro empresa + admin
        String cadastroJson = """
            {
              "nomeEmpresa": "Empresa Teste %s",
              "nuit": "%s",
              "emailEmpresa": "empresa%s@teste.com",
              "contacto": "%s",
              "endereco": "Av. Teste 1",
              "nomeAdministrador": "Admin Teste",
              "emailAdministrador": "admin%s@teste.com",
              "senhaAdministrador": "Admin@12345"
            }
            """.formatted(sufixo, nuit, sufixo, contacto, sufixo);

        MvcResult cadastro = mockMvc.perform(post("/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cadastroJson))
                .andExpect(status().isCreated())
                .andReturn();

        String token = om.readTree(cadastro.getResponse().getContentAsString())
                .get("token").asText();
        assertThat(token).isNotBlank();

        // 2) Criar categoria
        MvcResult categoria = mockMvc.perform(post("/api/categorias")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            { "nome": "Bebidas %s" }
                            """.formatted(sufixo)))
                .andExpect(status().isCreated())
                .andReturn();

        Long categoriaId = om.readTree(categoria.getResponse().getContentAsString())
                .get("id").asLong();

        // 3) Criar produto
        MvcResult produto = mockMvc.perform(post("/api/produtos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "nome": "Arroz %s",
                              "precoCompra": 10.00,
                              "precoVenda": 15.00,
                              "categoriaId": %d
                            }
                            """.formatted(sufixo, categoriaId)))
                .andExpect(status().isCreated())
                .andReturn();

        Long produtoId = om.readTree(produto.getResponse().getContentAsString())
                .get("id").asLong();

        // 4) Adicionar stock
        mockMvc.perform(post("/api/movimentos-stock")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "acao": "ENTRADA",
                              "quantidade": 100,
                              "produtoId": %d,
                              "descricao": "Stock inicial"
                            }
                            """.formatted(produtoId)))
                .andExpect(status().isCreated());

        // 5) Criar venda
        MvcResult venda = mockMvc.perform(post("/api/vendas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "itens": [
                                { "produtoId": %d, "quantidade": 5 }
                              ]
                            }
                            """.formatted(produtoId)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode vendaJson = om.readTree(venda.getResponse().getContentAsString());
        assertThat(vendaJson.get("total").asDouble()).isEqualTo(75.00); // 5 × 15
        assertThat(vendaJson.get("lucroTotal").asDouble()).isEqualTo(25.00); // 5 × 5

        // 6) Confirmar que o stock baixou para 95
        MvcResult estoque = mockMvc.perform(get("/api/estoques/produto/" + produtoId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(om.readTree(estoque.getResponse().getContentAsString())
                .get("quantidade").asInt()).isEqualTo(95);

        // 7) Dashboard responde
        mockMvc.perform(get("/api/dashboard?periodo=7")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
