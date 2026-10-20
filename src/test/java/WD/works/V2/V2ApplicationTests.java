package WD.works.V2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test para verificar se o contexto da aplicação carrega corretamente.
 * Apanha erros de wiring (beans, config, JPA) antes de produção.
 */
@SpringBootTest
class V2ApplicationTests {

    @Test
    @DisplayName("O contexto da aplicação deve carregar sem erros")
    void contextLoads() {
        // Se chegou aqui, o contexto carregou com sucesso.
    }
}
