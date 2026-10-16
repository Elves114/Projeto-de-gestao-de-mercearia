package WD.works.V2.configuracao.security;


import WD.works.V2.configuracao.security.JwtService;
import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.exception.RegraNegocioException;
import WD.works.V2.usuario.auth.security.UsuarioDetails;
import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.usuario.perfil.Perfil;
import WD.works.V2.usuario.status.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtService — geração e validação de tokens")
class JwtServiceTest {

    private static final String SECRET =
            "chave-secreta-de-teste-com-pelo-menos-32-caracteres-1234567890";

    private JwtService jwtService;
    private UsuarioDetails usuarioDetails;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService(SECRET, 3600000L);  // 1h

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João");
        usuario.setEmail("joao@teste.com");
        usuario.setSenha("$2a$10$hash");
        usuario.setPerfil(Perfil.ADMIN);
        usuario.setStatus(Status.ATIVO);
        usuario.setEmpresa(new Empresa());
        usuario.getEmpresa().setId(10L);

        usuarioDetails = new UsuarioDetails(usuario);
    }

    @Test
    @DisplayName("Gera token válido com o email do utilizador")
    void deveGerarTokenValido() {

        String token = jwtService.gerarToken(usuarioDetails);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extrairEmail(token)).isEqualTo("joao@teste.com");
    }

    @Test
    @DisplayName("Token gerado é válido para o utilizador correto")
    void tokenDeveSerValidoParaOMesmoUtilizador() {

        String token = jwtService.gerarToken(usuarioDetails);

        assertThat(jwtService.tokenValido(token, usuarioDetails)).isTrue();
    }

    @Test
    @DisplayName("Token é inválido para outro utilizador")
    void tokenDeveSerInvalidoParaOutroUtilizador() {

        String token = jwtService.gerarToken(usuarioDetails);

        Usuario outro = new Usuario();
        outro.setEmail("outro@teste.com");
        outro.setEmpresa(new Empresa());
        outro.getEmpresa().setId(20L);
        outro.setPerfil(Perfil.FUNCIONARIO);
        outro.setStatus(Status.ATIVO);

        UsuarioDetails outroDetails = new UsuarioDetails(outro);

        assertThat(jwtService.tokenValido(token, outroDetails)).isFalse();
    }

    @Test
    @DisplayName("Rejeita construção com chave demasiado curta")
    void deveRejeitarChaveCurta() {

        assertThatThrownBy(() -> new JwtService("curta", 3600000L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("pelo menos 32 caracteres");
    }

    @Test
    @DisplayName("Token com assinatura inválida não é aceite")
    void tokenComAssinaturaInvalida() {

        String token = jwtService.gerarToken(usuarioDetails);

        String tokenAdulterado = token.substring(0, token.length() - 5) + "XXXXX";

        assertThat(jwtService.tokenValido(tokenAdulterado, usuarioDetails))
                .isFalse();
    }
}