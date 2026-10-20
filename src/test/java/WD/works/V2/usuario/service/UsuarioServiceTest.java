package WD.works.V2.usuario.service;

import WD.works.V2.auditoria.service.AuditoriaService;
import WD.works.V2.configuracao.security.SecurityUtils;
import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.empresa.repository.EmpresaRepository;
import WD.works.V2.exception.RegraNegocioException;
import WD.works.V2.usuario.auth.service.RefreshTokenService;
import WD.works.V2.usuario.dto.AlterarSenhaRequest;
import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private AuditoriaService auditoriaService;

    @Mock
    private RefreshTokenService refreshTokenService;

    private UsuarioService service;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        service = new UsuarioService(
                usuarioRepository,
                empresaRepository,
                passwordEncoder,
                securityUtils,
                auditoriaService,
                refreshTokenService
        );

        Empresa empresa = new Empresa();
        empresa.setId(1L);
        empresa.setNome("Empresa Teste");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário Teste");
        usuario.setEmail("user@teste.com");
        usuario.setSenha("$2a$10$hashedPassword");
        usuario.setEmpresa(empresa);
    }

    @Test
    @DisplayName("Alterar senha deve revogar todos os refresh tokens")
    void alterarSenhaDeveRevogarRefreshTokens() {
        // Setup: senha atual correta
        when(securityUtils.getUsuarioAutenticado()).thenReturn(usuario);
        when(passwordEncoder.matches("senha123", usuario.getSenha())).thenReturn(true);
        when(passwordEncoder.matches("novaSenha123", usuario.getSenha())).thenReturn(false);
        when(passwordEncoder.encode("novaSenha123")).thenReturn("$2a$10$newHashedPassword");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        // Executar
        service.alterarMinhaSenha(new AlterarSenhaRequest("senha123", "novaSenha123"));

        // Verificar que os refresh tokens foram revogados
        verify(refreshTokenService).revogarTodos(usuario.getId());
        verify(usuarioRepository).save(usuario);
        verify(auditoriaService).registrar(any());
    }

    @Test
    @DisplayName("Alterar senha com senha atual incorreta deve lançar exceção")
    void alterarSenhaComSenhaAtualIncorreta() {
        when(securityUtils.getUsuarioAutenticado()).thenReturn(usuario);
        when(passwordEncoder.matches("senhaErrada", usuario.getSenha())).thenReturn(false);

        assertThatThrownBy(() ->
                service.alterarMinhaSenha(new AlterarSenhaRequest("senhaErrada", "novaSenha123"))
        ).isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("incorreta");

        verify(refreshTokenService, never()).revogarTodos(any());
    }

    @Test
    @DisplayName("Alterar senha igual à atual deve lançar exceção")
    void alterarSenhaIgualAtual() {
        when(securityUtils.getUsuarioAutenticado()).thenReturn(usuario);
        when(passwordEncoder.matches("senha123", usuario.getSenha())).thenReturn(true);
        when(passwordEncoder.matches("senha123", usuario.getSenha())).thenReturn(true);

        assertThatThrownBy(() ->
                service.alterarMinhaSenha(new AlterarSenhaRequest("senha123", "senha123"))
        ).isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("igual");

        verify(refreshTokenService, never()).revogarTodos(any());
    }
}
