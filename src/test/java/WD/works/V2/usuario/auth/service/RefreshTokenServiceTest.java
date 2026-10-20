package WD.works.V2.usuario.auth.service;

import WD.works.V2.exception.TokenRecuperacaoException;
import WD.works.V2.usuario.auth.entity.RefreshToken;
import WD.works.V2.usuario.auth.repository.RefreshTokenRepository;
import WD.works.V2.usuario.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository repository;

    private RefreshTokenService service;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        service = new RefreshTokenService(repository);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário Teste");
        usuario.setEmail("user@teste.com");
    }

    @Test
    @DisplayName("validarERevogar deve validar e revogar o token atomicamente")
    void validarERevogarDeveFuncionar() {
        // Setup
        String token = "some-token";
        String hash = service.gerarHash(token);

        RefreshToken entity = RefreshToken.builder()
                .id(1L)
                .tokenHash(hash)
                .usuario(usuario)
                .expiracao(LocalDateTime.now().plusDays(7))
                .revogado(false)
                .criadoEm(LocalDateTime.now())
                .build();

        when(repository.findByTokenHashComLock(hash)).thenReturn(Optional.of(entity));
        when(repository.save(any(RefreshToken.class))).thenReturn(entity);

        // Executar
        Usuario resultado = service.validarERevogar(token);

        // Verificar
        assertThat(resultado).isEqualTo(usuario);
        assertThat(entity.isRevogado()).isTrue();
        assertThat(entity.getRevogadoEm()).isNotNull();
        verify(repository).findByTokenHashComLock(hash);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("validarERevogar com token nulo deve lançar TokenRecuperacaoException")
    void validarERevogarComTokenNulo() {
        assertThatThrownBy(() -> service.validarERevogar(null))
                .isInstanceOf(TokenRecuperacaoException.class)
                .hasMessageContaining("não informado");

        verify(repository, never()).findByTokenHashComLock(anyString());
    }

    @Test
    @DisplayName("validarERevogar com token expirado deve lançar TokenRecuperacaoException")
    void validarERevogarComTokenExpirado() {
        // Setup
        String token = "expired-token";
        String hash = service.gerarHash(token);

        RefreshToken entity = RefreshToken.builder()
                .id(1L)
                .tokenHash(hash)
                .usuario(usuario)
                .expiracao(LocalDateTime.now().minusDays(1))
                .revogado(false)
                .criadoEm(LocalDateTime.now().minusDays(8))
                .build();

        when(repository.findByTokenHashComLock(hash)).thenReturn(Optional.of(entity));

        // Executar e verificar
        assertThatThrownBy(() -> service.validarERevogar(token))
                .isInstanceOf(TokenRecuperacaoException.class)
                .hasMessageContaining("expirado");
    }

    @Test
    @DisplayName("validarERevogar com token já revogado deve lançar TokenRecuperacaoException")
    void validarERevogarComTokenRevogado() {
        // Setup
        String token = "revoked-token";
        String hash = service.gerarHash(token);

        RefreshToken entity = RefreshToken.builder()
                .id(1L)
                .tokenHash(hash)
                .usuario(usuario)
                .expiracao(LocalDateTime.now().plusDays(7))
                .revogado(true)
                .revogadoEm(LocalDateTime.now())
                .criadoEm(LocalDateTime.now().minusDays(1))
                .build();

        when(repository.findByTokenHashComLock(hash)).thenReturn(Optional.of(entity));

        // Executar e verificar
        assertThatThrownBy(() -> service.validarERevogar(token))
                .isInstanceOf(TokenRecuperacaoException.class)
                .hasMessageContaining("inválido");
    }

    @Test
    @DisplayName("validarERevogar com token não encontrado deve lançar TokenRecuperacaoException")
    void validarERevogarComTokenNaoEncontrado() {
        String token = "unknown-token";
        String hash = service.gerarHash(token);

        when(repository.findByTokenHashComLock(hash)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.validarERevogar(token))
                .isInstanceOf(TokenRecuperacaoException.class)
                .hasMessageContaining("inválido");
    }

    @Test
    @DisplayName("Concorrência: 2 threads a chamar validarERevogar com o mesmo token — apenas 1 deve ter sucesso")
    void concorrenciaValidarERevogarMesmoToken() throws InterruptedException {
        // Setup — simula o comportamento do lock pessimista:
        // A primeira chamada retorna o token válido, a segunda encontra-o já revogado.
        String token = "shared-token";
        String hash = service.gerarHash(token);

        RefreshToken entity = RefreshToken.builder()
                .id(1L)
                .tokenHash(hash)
                .usuario(usuario)
                .expiracao(LocalDateTime.now().plusDays(7))
                .revogado(false)
                .criadoEm(LocalDateTime.now())
                .build();

        // Primeira chamada: token válido
        // Segunda chamada: token já revogado (simula o estado após a 1ª thread revogar)
        when(repository.findByTokenHashComLock(hash))
                .thenReturn(Optional.of(entity))
                .thenAnswer(invocation -> {
                    // Após a primeira chamada, o token já foi revogado
                    entity.setRevogado(true);
                    entity.setRevogadoEm(LocalDateTime.now());
                    return Optional.of(entity);
                });
        when(repository.save(any(RefreshToken.class))).thenReturn(entity);

        // Executar com 2 threads
        AtomicInteger sucessoCount = new AtomicInteger(0);
        AtomicInteger falhaCount = new AtomicInteger(0);
        AtomicReference<Usuario> usuarioRetornado = new AtomicReference<>();

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        for (int i = 0; i < 2; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await(); // Ambas as threads começam ao mesmo tempo
                    Usuario result = service.validarERevogar(token);
                    sucessoCount.incrementAndGet();
                    usuarioRetornado.set(result);
                } catch (TokenRecuperacaoException e) {
                    falhaCount.incrementAndGet();
                } catch (Exception e) {
                    falhaCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // Sinaliza as 2 threads para começarem
        doneLatch.await(); // Aguarda as 2 threads terminarem
        executor.shutdown();

        // Verificar: exatamente 1 sucesso e 1 falha
        assertThat(sucessoCount.get()).isEqualTo(1);
        assertThat(falhaCount.get()).isEqualTo(1);
        assertThat(usuarioRetornado.get()).isEqualTo(usuario);
    }

    @Test
    @DisplayName("gerarHash deve produzir hash consistente")
    void gerarHashDeveSerConsistente() {
        String token = "test-token";
        String hash1 = service.gerarHash(token);
        String hash2 = service.gerarHash(token);

        assertThat(hash1).isEqualTo(hash2);
        assertThat(hash1).isNotEqualTo(token);
    }

    @Test
    @DisplayName("revogar deve marcar o token como revogado")
    void revogarDeveMarcarComoRevogado() {
        String token = "token-a-revogar";
        String hash = service.gerarHash(token);

        RefreshToken entity = RefreshToken.builder()
                .id(1L)
                .tokenHash(hash)
                .usuario(usuario)
                .expiracao(LocalDateTime.now().plusDays(7))
                .revogado(false)
                .criadoEm(LocalDateTime.now())
                .build();

        when(repository.findByTokenHash(hash)).thenReturn(Optional.of(entity));
        when(repository.save(any(RefreshToken.class))).thenReturn(entity);

        service.revogar(token);

        assertThat(entity.isRevogado()).isTrue();
        assertThat(entity.getRevogadoEm()).isNotNull();
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("revogar com token nulo não deve fazer nada")
    void revogarComTokenNulo() {
        service.revogar(null);
        service.revogar("");

        verify(repository, never()).findByTokenHash(anyString());
        verify(repository, never()).save(any());
    }
}
