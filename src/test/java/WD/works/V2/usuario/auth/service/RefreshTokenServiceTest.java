package WD.works.V2.usuario.auth.service;

import WD.works.V2.exception.TokenRecuperacaoException;
import WD.works.V2.usuario.auth.entity.RefreshToken;
import WD.works.V2.usuario.auth.repository.RefreshTokenRepository;
import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.usuario.perfil.Perfil;
import WD.works.V2.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenService — criação e validação")
class RefreshTokenServiceTest {

    @Mock private RefreshTokenRepository repository;
    @InjectMocks private RefreshTokenService service;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "expiracaoDias", 7L);
        usuario = TestDataFactory.usuario(1L, 10L, Perfil.FUNCIONARIO);
    }

    @Test
    @DisplayName("Cria token e guarda apenas o hash")
    void deveCriarToken() {
        when(repository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));
        String token = service.criar(usuario, "127.0.0.1", "Test Agent");
        assertThat(token).isNotBlank();

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(repository).save(captor.capture());
        RefreshToken salvo = captor.getValue();

        assertThat(salvo.getTokenHash()).isNotEqualTo(token);
        assertThat(salvo.getUsuario()).isEqualTo(usuario);
    }

    @Test
    @DisplayName("Valida e revoga token correto")
    void validarERevogarDeveFuncionar() {
        String token = "abc";
        String hash = service.gerarHash(token);

        RefreshToken entity = RefreshToken.builder()
                .tokenHash(hash)
                .usuario(usuario)
                .expiracao(LocalDateTime.now().plusDays(1))
                .revogado(false)
                .build();

        when(repository.findByTokenHashComLock(hash)).thenReturn(Optional.of(entity));

        Usuario resultado = service.validarERevogar(token);

        assertThat(resultado).isEqualTo(usuario);
        assertThat(entity.isRevogado()).isTrue();
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("Rejeita token já revogado")
    void validarERevogarComTokenRevogado() {
        String token = "abc";
        RefreshToken entity = RefreshToken.builder()
                .tokenHash(service.gerarHash(token))
                .usuario(usuario)
                .expiracao(LocalDateTime.now().plusDays(1))
                .revogado(true)
                .build();

        when(repository.findByTokenHashComLock(any())).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.validarERevogar(token))
                .isInstanceOf(TokenRecuperacaoException.class);
    }

    @Test
    @DisplayName("Rejeita token expirado")
    void validarERevogarComTokenExpirado() {
        String token = "abc";
        RefreshToken entity = RefreshToken.builder()
                .tokenHash(service.gerarHash(token))
                .usuario(usuario)
                .expiracao(LocalDateTime.now().minusMinutes(1))
                .revogado(false)
                .build();

        when(repository.findByTokenHashComLock(any())).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.validarERevogar(token))
                .isInstanceOf(TokenRecuperacaoException.class);
    }

    @Test
    @DisplayName("Rejeita token não encontrado")
    void validarERevogarComTokenNaoEncontrado() {
        when(repository.findByTokenHashComLock(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.validarERevogar("inexistente"))
                .isInstanceOf(TokenRecuperacaoException.class);
    }

    @Test
    @DisplayName("Lança exceção para token nulo")
    void validarERevogarComTokenNulo() {
        assertThatThrownBy(() -> service.validarERevogar(null))
                .isInstanceOf(TokenRecuperacaoException.class);
    }

    @Test
    @DisplayName("Revoga token específico")
    void revogarDeveMarcarComoRevogado() {
        String token = "abc";
        String hash = service.gerarHash(token);
        RefreshToken entity = RefreshToken.builder()
                .tokenHash(hash)
                .usuario(usuario)
                .expiracao(LocalDateTime.now().plusDays(1))
                .revogado(false)
                .build();

        when(repository.findByTokenHash(hash)).thenReturn(Optional.of(entity));
        service.revogar(token);

        assertThat(entity.isRevogado()).isTrue();
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("Revogar token nulo não faz nada")
    void revogarComTokenNulo() {
        service.revogar(null);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Gerar hash é consistente")
    void gerarHashDeveSerConsistente() {
        assertThat(service.gerarHash("abc")).isEqualTo(service.gerarHash("abc"));
    }

    @Test
    @Disabled("H2 em memória não suporta PESSIMISTIC_WRITE entre threads. Mover para Testcontainers/PostgreSQL.")
    @DisplayName("Apenas uma thread consegue revogar o mesmo token")
    void concorrenciaValidarERevogarMesmoToken() {
        // Este teste exige uma base de dados real (PostgreSQL) para validar o Lock.
    }
}