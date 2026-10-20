package WD.works.V2.usuario.auth.service;

import WD.works.V2.auditoria.service.AuditoriaService;
import WD.works.V2.configuracao.security.JwtService;
import WD.works.V2.exception.RegraNegocioException;
import WD.works.V2.exception.TokenRecuperacaoException;
import WD.works.V2.usuario.auth.dto.AuthResponse;
import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.usuario.perfil.Perfil;
import WD.works.V2.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService — refresh de tokens")
class AuthServiceTest {

    @Mock private JwtService jwtService;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private AuditoriaService auditoriaService;

    @InjectMocks
    private AuthService service;

    private Usuario usuario;
    private final String tokenValido = "token-valido";

    @BeforeEach
    void setUp() {
        // Injetar as dependências necessárias
        ReflectionTestUtils.setField(service, "jwtService", jwtService);
        ReflectionTestUtils.setField(service, "refreshTokenService", refreshTokenService);
        ReflectionTestUtils.setField(service, "auditoriaService", auditoriaService);

        usuario = TestDataFactory.usuario(1L, 10L, Perfil.FUNCIONARIO);
    }

    @Test
    @DisplayName("Refresh com token válido devolve nova autenticação")
    void refreshValidoDeveDevolverAuthResponse() {
        // Configurações movidas para aqui
        when(jwtService.gerarToken(any())).thenReturn("access-token-falso");
        when(jwtService.getExpirationSeconds()).thenReturn(3600L);
        when(refreshTokenService.validarERevogar(tokenValido)).thenReturn(usuario);
        when(refreshTokenService.criar(any(Usuario.class), any(), any())).thenReturn("novo-refresh-token");

        AuthResponse response = service.refresh(tokenValido, null);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("access-token-falso");
        assertThat(response.getRefreshToken()).isEqualTo("novo-refresh-token");
    }

    @Test
    @DisplayName("Falha na auditoria não deve impedir a devolução do novo token")
    void falhaAuditoriaNaoImpedeRefresh() {
        // Configurações movidas para aqui
        when(jwtService.gerarToken(any())).thenReturn("access-token-falso");
        when(jwtService.getExpirationSeconds()).thenReturn(3600L);
        when(refreshTokenService.validarERevogar(tokenValido)).thenReturn(usuario);
        when(refreshTokenService.criar(any(Usuario.class), any(), any())).thenReturn("novo-refresh-token");

        // Simula uma exceção na auditoria
        when(auditoriaService.registrar(any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Falha na base de dados de auditoria"));

        AuthResponse response = service.refresh(tokenValido, null);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("access-token-falso");
    }

    @Test
    @DisplayName("Refresh inválido propaga a exceção")
    void refreshInvalidoDevePropagarException() {
        when(refreshTokenService.validarERevogar(tokenValido))
                .thenThrow(new TokenRecuperacaoException("Token inválido"));

        assertThatThrownBy(() -> service.refresh(tokenValido, null))
                .isInstanceOf(TokenRecuperacaoException.class);
    }

    @Test
    @DisplayName("Refresh com token nulo lança exceção de regra de negócio")
    void refreshComTokenNulo() {
        // CORREÇÃO: A exceção esperada é RegraNegocioException, não IllegalArgumentException
        assertThatThrownBy(() -> service.refresh(null, null))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Refresh token não informado");
    }
}