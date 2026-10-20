package WD.works.V2.usuario.auth.service;

import WD.works.V2.auditoria.dto.AuditoriaRequest;
import WD.works.V2.auditoria.service.AuditoriaService;
import WD.works.V2.configuracao.security.JwtService;
import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.empresa.repository.EmpresaRepository;
import WD.works.V2.empresa.service.EmpresaService;
import WD.works.V2.exception.RegraNegocioException;
import WD.works.V2.exception.TokenRecuperacaoException;
import WD.works.V2.usuario.auth.dto.AuthResponse;
import WD.works.V2.usuario.auth.security.UsuarioDetails;
import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.usuario.repository.UsuarioRepository;
import WD.works.V2.usuario.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AuditoriaService auditoriaService;

    @Mock
    private EmpresaService empresaService;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LoginTentativaService loginTentativaService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private HttpServletRequest httpRequest;

    private AuthService authService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                null, // AuthenticationManager (not used in refresh)
                jwtService,
                empresaService,
                usuarioService,
                usuarioRepository,
                loginTentativaService,
                null, // TokenRecuperacaoSenhaService (not used in refresh)
                refreshTokenService,
                null, // EmailService ObjectProvider (not used in refresh)
                passwordEncoder,
                auditoriaService,
                empresaRepository
        );

        Empresa empresa = new Empresa();
        empresa.setId(1L);
        empresa.setNome("Empresa Teste");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário Teste");
        usuario.setEmail("user@teste.com");
        usuario.setEmpresa(empresa);
    }

    @Test
    @DisplayName("Refresh válido deve devolver novo AuthResponse e chamar validarERevogar")
    void refreshValidoDeveDevolverAuthResponse() {
        // Setup
        String refreshToken = "valid-refresh-token";
        when(refreshTokenService.validarERevogar(refreshToken)).thenReturn(usuario);
        when(jwtService.gerarToken(any(UsuarioDetails.class))).thenReturn("new-access-token");
        when(jwtService.getExpirationSeconds()).thenReturn(3600L);
        when(httpRequest.getHeader("X-Forwarded-For")).thenReturn(null);
        when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(httpRequest.getHeader("User-Agent")).thenReturn("TestAgent");

        // Executar
        AuthResponse response = authService.refresh(refreshToken, httpRequest);

        // Verificar
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isNotNull();
        assertThat(response.getExpiresIn()).isEqualTo(3600L);

        verify(refreshTokenService).validarERevogar(refreshToken);
        verify(refreshTokenService).criar(eq(usuario), anyString(), anyString());
        verify(auditoriaService).registrar(any(AuditoriaRequest.class), eq(usuario), eq(usuario.getEmpresa()), any(), isNull());
    }

    @Test
    @DisplayName("Refresh inválido deve propagar TokenRecuperacaoException")
    void refreshInvalidoDevePropagarException() {
        // Setup
        String refreshToken = "invalid-refresh-token";
        when(refreshTokenService.validarERevogar(refreshToken))
                .thenThrow(new TokenRecuperacaoException("Refresh token inválido."));

        // Executar e verificar
        assertThatThrownBy(() -> authService.refresh(refreshToken, httpRequest))
                .isInstanceOf(TokenRecuperacaoException.class)
                .hasMessageContaining("inválido");

        verify(refreshTokenService).validarERevogar(refreshToken);
        verify(jwtService, never()).gerarToken(any());
        verify(auditoriaService, never()).registrar(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Refresh com token nulo deve lançar RegraNegocioException")
    void refreshComTokenNulo() {
        assertThatThrownBy(() -> authService.refresh(null, httpRequest))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("não informado");

        verify(refreshTokenService, never()).validarERevogar(any());
    }

    @Test
    @DisplayName("Falha de auditoria não impede o refresh (prova de N6)")
    void falhaAuditoriaNaoImpedeRefresh() {
        // Setup
        String refreshToken = "valid-refresh-token";
        when(refreshTokenService.validarERevogar(refreshToken)).thenReturn(usuario);
        when(jwtService.gerarToken(any(UsuarioDetails.class))).thenReturn("new-access-token");
        when(jwtService.getExpirationSeconds()).thenReturn(3600L);
        when(httpRequest.getHeader("X-Forwarded-For")).thenReturn(null);
        when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(httpRequest.getHeader("User-Agent")).thenReturn("TestAgent");
        when(auditoriaService.registrar(any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Falha na auditoria"));

        // Executar — não deve lançar exceção
        AuthResponse response = authService.refresh(refreshToken, httpRequest);

        // Verificar
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isNotNull();

        verify(refreshTokenService).validarERevogar(refreshToken);
        verify(auditoriaService).registrar(any(AuditoriaRequest.class), eq(usuario), eq(usuario.getEmpresa()), any(), isNull());
    }
}
