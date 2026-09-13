package WD.works.V2.usuario.auth.service;

import WD.works.V2.exception.TokenRecuperacaoException;
import WD.works.V2.usuario.auth.entity.TokenRecuperacaoSenha;
import WD.works.V2.usuario.auth.repository.TokenRecuperacaoSenhaRepository;
import WD.works.V2.usuario.entity.Usuario;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Getter
public class TokenRecuperacaoSenhaService {

    private final TokenRecuperacaoSenhaRepository tokenRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Gera um token aleatório e guarda somente o seu HASH na base de dados.
     */
    public String criarToken(Usuario usuario) {

        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        String tokenHash = gerarHash(token);

        TokenRecuperacaoSenha recuperacao = TokenRecuperacaoSenha.builder()
                .tokenHash(tokenHash)
                .usuario(usuario)
                .expiracao(LocalDateTime.now().plusMinutes(15))
                .usado(false)
                .criadoEm(LocalDateTime.now())
                .build();

        tokenRepository.save(recuperacao);

        return token;
    }

    /**
     * Converte o token recebido para o mesmo HASH
     * que foi armazenado na base de dados.
     */
    String gerarHash(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            token.getBytes(StandardCharsets.UTF_8)
                    );

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hash);

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "SHA-256 não disponível",
                    e
            );
        }
    }

    public TokenRecuperacaoSenha validarToken(String token) {

        String tokenHash = gerarHash(token);

        TokenRecuperacaoSenha recuperacao =
                tokenRepository.findByTokenHash(tokenHash)
                        .orElseThrow(() ->
        new TokenRecuperacaoException(
                "Token de recuperação inválido"
        ));

        if (recuperacao.isUsado()) {
            throw new TokenRecuperacaoException(
                    "Token de recuperação já utilizado"
            );
        }

        if (recuperacao.getExpiracao().isBefore(LocalDateTime.now())) {
            throw new TokenRecuperacaoException(
                    "Token de recuperação expirado"
            );
        }

        return recuperacao;
    }

    public void marcarComoUsado(TokenRecuperacaoSenha token) {

        token.setUsado(true);

        tokenRepository.save(token);
    }
}