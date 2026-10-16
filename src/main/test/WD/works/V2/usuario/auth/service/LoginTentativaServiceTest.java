package WD.works.V2.usuario.auth.service;


import WD.works.V2.usuario.auth.service.LoginTentativaService;
import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.usuario.perfil.Perfil;
import WD.works.V2.usuario.repository.UsuarioRepository;
import WD.works.V2.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginTentativaService — bloqueio por tentativas")
class LoginTentativaServiceTest {

    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks
    private LoginTentativaService service;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = TestDataFactory.usuario(1L, 10L, Perfil.FUNCIONARIO);
    }

    @Test
    @DisplayName("Incrementa tentativas em cada falha")
    void deveIncrementarTentativas() {

        usuario.setTentativasLogin(2);

        service.registrarFalha(usuario);

        assertThat(usuario.getTentativasLogin()).isEqualTo(3);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Bloqueia após 5 tentativas")
    void deveBloquearApos5Tentativas() {

        usuario.setTentativasLogin(4);

        service.registrarFalha(usuario);

        assertThat(usuario.getTentativasLogin()).isEqualTo(5);
        assertThat(usuario.getBloqueadoAte()).isAfter(LocalDateTime.now());
    }

    @Test
    @DisplayName("Reset em sucesso")
    void deveResetarEmSucesso() {

        usuario.setTentativasLogin(3);
        usuario.setBloqueadoAte(LocalDateTime.now().plusMinutes(10));

        service.registrarSucesso(usuario);

        assertThat(usuario.getTentativasLogin()).isZero();
        assertThat(usuario.getBloqueadoAte()).isNull();
    }

    @Test
    @DisplayName("Detecta bloqueio ativo")
    void deveDetectarBloqueioAtivo() {

        usuario.setBloqueadoAte(LocalDateTime.now().plusMinutes(5));

        assertThat(service.estaTemporariamenteBloqueado(usuario)).isTrue();
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Limpa bloqueio expirado e permite nova tentativa")
    void deveLimparBloqueioExpirado() {

        usuario.setBloqueadoAte(LocalDateTime.now().minusMinutes(1));
        usuario.setTentativasLogin(5);

        assertThat(service.estaTemporariamenteBloqueado(usuario)).isFalse();
        assertThat(usuario.getTentativasLogin()).isZero();
        assertThat(usuario.getBloqueadoAte()).isNull();
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Sem bloqueio quando bloqueadoAte é null")
    void semBloqueioQuandoNull() {

        usuario.setBloqueadoAte(null);

        assertThat(service.estaTemporariamenteBloqueado(usuario)).isFalse();
    }
}