package WD.works.V2.usuario.auth.service;

import WD.works.V2.exception.RegraNegocioException;
import WD.works.V2.exception.TokenRecuperacaoException;
import WD.works.V2.usuario.auth.entity.RefreshToken;
import WD.works.V2.usuario.auth.repository.RefreshTokenRepository;
import WD.works.V2.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Serviço de refresh tokens.
 *
 * Fluxo:
 *   1. No login, gera um refresh token (opaco, 64 bytes aleatórios).
 *   2. Guarda apenas o HASH (SHA-256) na BD.
 *   3. Devolve o token em texto simples ao cliente.
 *   4. No /auth/refresh, valida o token, revoga-o e emite um novo.
 *
 * Rotação: cada refresh cria um novo refresh token e revoga o antigo.
 * Isto evita reutilização maliciosa de tokens antigos.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final Logger log =
            LoggerFactory.getLogger(RefreshTokenService.class);

    @Value("${security.refresh-token.expiracao-dias:7}")
    private long expiracaoDias;

    private final RefreshTokenRepository repository;
    private final SecureRandom secureRandom = new SecureRandom();

    /* ============================================================
       CRIAR
       ============================================================ */

    @Transactional
    public String criar(Usuario usuario, String ip, String userAgent) {

        // 1. Gera bytes aleatórios
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        // 2. Guarda o hash
        RefreshToken entity = RefreshToken.builder()
                .tokenHash(gerarHash(token))
                .usuario(usuario)
                .expiracao(LocalDateTime.now().plusDays(expiracaoDias))
                .revogado(false)
                .criadoEm(LocalDateTime.now())
                .ip(ip)
                .userAgent(truncar(userAgent, 255))
                .build();

        repository.save(entity);

        return token;
    }

    /* ============================================================
       VALIDAR E ROTACIONAR
       ============================================================ */

    /**
     * Valida um refresh token e devolve o utilizador associado.
     * NÃO o revoga — a rotação é feita no AuthService.
     */
    @Transactional(readOnly = true)
    public Usuario validar(String token) {

        if (token == null || token.isBlank()) {
            throw new TokenRecuperacaoException(
                    "Refresh token não informado."
            );
        }

        String hash = gerarHash(token);

        RefreshToken entity = repository.findByTokenHash(hash)
                .orElseThrow(() -> new TokenRecuperacaoException(
                        "Refresh token inválido."
                ));

        if (entity.isRevogado()) {
            throw new TokenRecuperacaoException(
                    "Refresh token já foi revogado."
            );
        }

        if (entity.isExpirado()) {
            throw new TokenRecuperacaoException(
                    "Refresh token expirado. Faça login novamente."
            );
        }

        return entity.getUsuario();
    }

    /**
     * Revoga um refresh token específico pelo seu valor em texto simples.
     */
    @Transactional
    public void revogar(String token) {

        if (token == null || token.isBlank()) {
            return;
        }

        String hash = gerarHash(token);

        repository.findByTokenHash(hash)
                .ifPresent(entity -> {
                    entity.setRevogado(true);
                    entity.setRevogadoEm(LocalDateTime.now());
                    repository.save(entity);
                });
    }

    /**
     * Revoga TODOS os refresh tokens ativos de um utilizador.
     * Útil para logout global, mudança de senha, etc.
     */
    @Transactional
    public int revogarTodos(Long usuarioId) {

        return repository.revogarTodosDoUsuario(
                usuarioId,
                LocalDateTime.now()
        );
    }

    /* ============================================================
       LIMPEZA AUTOMÁTICA
       ============================================================ */

    /**
     * Corre todos os dias às 3h da manhã.
     * Remove tokens expirados ou revogados há mais de 30 dias.
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void limparAntigos() {

        LocalDateTime limite = LocalDateTime.now().minusDays(30);

        int removidos = repository.limparAntigos(limite);

        if (removidos > 0) {
            log.info(
                    "Limpeza de refresh tokens: {} removidos.",
                    removidos
            );
        }
    }

    /* ============================================================
       UTILITÁRIOS
       ============================================================ */

    public String gerarHash(String token) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 não disponível", e);
        }
    }

    /**
     * Valida e revoga ATOMICAMENTE o refresh token.
     * Usa lock pessimista para evitar race condition.
     */
    @Transactional
    public Usuario validarERevogar(String token) {

        if (token == null || token.isBlank()) {
            throw new TokenRecuperacaoException(
                    "Refresh token não informado."
            );
        }

        String hash = gerarHash(token);

        RefreshToken entity = repository.findByTokenHashComLock(hash)
                .orElseThrow(() -> new TokenRecuperacaoException(
                        "Refresh token inválido."
                ));

        if (entity.isRevogado() || entity.isExpirado()) {
            throw new TokenRecuperacaoException(
                    "Refresh token inválido ou expirado."
            );
        }

        entity.setRevogado(true);
        entity.setRevogadoEm(LocalDateTime.now());
        repository.save(entity);

        return entity.getUsuario();
    }

    private String truncar(String valor, int max) {
        if (valor == null) return null;
        return valor.length() > max
                ? valor.substring(0, max)
                : valor;
    }
}