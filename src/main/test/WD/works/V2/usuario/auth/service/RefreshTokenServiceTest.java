package WD.works.V2.usuario.auth.service;


import WD.works.V2.exception.TokenRecuperacaoException;
import WD.works.V2.usuario.auth.entity.RefreshToken;
import WD.works.V2.usuario.auth.repository.RefreshTokenRepository;
import WD.works.V2.usuario.auth.service.RefreshTokenService;
import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.usuario.perfil.Perfil;
import WD.works.V2.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
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

    @Mock
    private RefreshTokenRepository repository;

    @InjectMocks
    private RefreshTokenService service;

    private Usuario usuario;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(service, "expiracaoDias", 7L);

        usuario = TestDataFactory.usuario(1L, 10L, Perfil.FUNCIONARIO);
    }

    @Test
    @DisplayName("Cria token e guarda apenas o hash")
    void deveCriarToken() {

        when(repository.save(any(RefreshToken.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        String token = service.criar(usuario, "127.0.0.1", "Test Agent");

        assertThat(token).isNotBlank();

        ArgumentCaptor<RefreshToken> captor =
                ArgumentCaptor.forClass(RefreshToken.class);

        verify(repository).save(captor.capture());

        RefreshToken salvo = captor.getValue();

        assertThat(salvo.getTokenHash()).isNotEqualTo(token);
        assertThat(salvo.getTokenHash()).isEqualTo(service.gerarHash(token));
        assertThat(salvo.getUsuario()).isEqualTo(usuario);
        assertThat(salvo.isRevogado()).isFalse();
        assertThat(salvo.getExpiracao()).isAfter(LocalDateTime.now());
        assertThat(salvo.getIp()).isEqualTo("127.0.0.1");
    }

    @Test
    @DisplayName("Valida token correto")
    void deveValidarTokenCorreto() {

        String token = "abc";
        String hash = service.gerarHash(token);

        RefreshToken entity = RefreshToken.builder()
                .tokenHash(hash)
                .usuario(usuario)
                .expiracao(LocalDateTime.now().plusDays(1))
                .revogado(false)
                .build();

        when(repository.findByTokenHash(hash))
                .thenReturn(Optional.of(entity));

        Usuario resultado = service.validar(token);

        assertThat(resultado).isEqualTo(usuario);
    }

    @Test
    @DisplayName("Rejeita token inexistente")
    void deveRejeitarTokenInexistente() {

        when(repository.findByTokenHash(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.validar("inexistente"))
                .isInstanceOf(TokenRecuperacaoException.class)
                .hasMessageContaining("inválido");
    }

    @Test
    @DisplayName("Rejeita token já revogado")
    void deveRejeitarTokenRevogado() {

        String token = "abc";
        String hash = service.gerarHash(token);

        RefreshToken entity = RefreshToken.builder()
                .tokenHash(hash)
                .usuario(usuario)
                .expiracao(LocalDateTime.now().plusDays(1))
                .revogado(true)
                .build();

        when(repository.findByTokenHash(hash))
                .thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.validar(token))
                .isInstanceOf(TokenRecuperacaoException.class)
                .hasMessageContaining("revogado");
    }

    @Test
    @DisplayName("Rejeita token expirado")
    void deveRejeitarTokenExpirado() {

        String token = "abc";
        String hash = service.gerarHash(token);

        RefreshToken entity = RefreshToken.builder()
                .tokenHash(hash)
                .usuario(usuario)
                .expiracao(LocalDateTime.now().minusMinutes(1))
                .revogado(false)
                .build();

        when(repository.findByTokenHash(hash))
                .thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.validar(token))
                .isInstanceOf(TokenRecuperacaoException.class)
                .hasMessageContaining("expirado");
    }

    @Test
    @DisplayName("Revoga token específico")
    void deveRevogarToken() {

        String token = "abc";
        String hash = service.gerarHash(token);

        RefreshToken entity = RefreshToken.builder()
                .tokenHash(hash)
                .usuario(usuario)
                .expiracao(LocalDateTime.now().plusDays(1))
                .revogado(false)
                .build();

        when(repository.findByTokenHash(hash))
                .thenReturn(Optional.of(entity));

        service.revogar(token);

        assertThat(entity.isRevogado()).isTrue();
        assertThat(entity.getRevogadoEm()).isNotNull();

        verify(repository).save(entity);
    }

    @Test
    @DisplayName("Revogar token inexistente não faz nada")
    void revogarTokenInexistente() {

        when(repository.findByTokenHash(any()))
                .thenReturn(Optional.empty());

        service.revogar("inexistente");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Revoga todos os tokens de um utilizador")
    void deveRevogarTodos() {

        when(repository.revogarTodosDoUsuario(eq(1L), any()))
                .thenReturn(3);

        int total = service.revogarTodos(1L);

        assertThat(total).isEqualTo(3);
    }
}