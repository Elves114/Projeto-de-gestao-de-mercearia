package WD.works.V2.configuracao.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()

                /*
                 * ========================================================
                 * INFORMAÇÕES DA API
                 * ========================================================
                 */
                .info(
                        new Info()
                                .title("WD Works V2 API")
                                .version("1.0.0")
                                .description(
                                        """
                                        API REST do sistema WD Works V2.

                                        Sistema de gestão empresarial responsável
                                        pela gestão de empresas, usuários, produtos,
                                        categorias, estoque, vendas, movimentos de
                                        estoque e auditoria.

                                        A autenticação da API é realizada através
                                        de JWT (JSON Web Token).
                                        """
                                )
                                .contact(
                                        new Contact()
                                                .name("WD Works")
                                )
                )

                /*
                 * ========================================================
                 * AUTENTICAÇÃO JWT
                 * ========================================================
                 */
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new SecurityScheme()
                                                .type(
                                                        SecurityScheme.Type.HTTP
                                                )
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                )

                /*
                 * ========================================================
                 * SEGURANÇA GLOBAL
                 * ========================================================
                 *
                 * Indica ao Swagger que os endpoints da API
                 * utilizam autenticação Bearer JWT.
                 */
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("bearerAuth")
                );
    }
}