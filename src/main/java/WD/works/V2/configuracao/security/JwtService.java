package WD.works.V2.configuracao.security;

import WD.works.V2.exception.RegraNegocioException;
import WD.works.V2.usuario.auth.security.UsuarioDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey secretKey;

    private final long expiration;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration:86400000}") long expiration
    ) {

        if (secret == null || secret.length() < 32) {
            throw new RegraNegocioException(
                    "A chave JWT deve possuir pelo menos 32 caracteres. Tamanho atual: "
                            + (secret != null ? secret.length() : 0)
            );
        }

        this.secretKey =
                Keys.hmacShaKeyFor(
                        secret.getBytes(StandardCharsets.UTF_8)
                );

        this.expiration = expiration;
    }


    /**
     * Gera um JWT para o usuário autenticado.
     */
    public String gerarToken(
            UserDetails userDetails
    ) {

        UsuarioDetails usuarioDetails =
                (UsuarioDetails) userDetails;

        return Jwts.builder()

                .subject(
                        usuarioDetails
                                .getUsername()
                )

                .claim(
                        "usuarioId",
                        usuarioDetails
                                .getUsuario()
                                .getId()
                )

                .claim(
                        "empresaId",
                        usuarioDetails
                                .getUsuario()
                                .getEmpresa()
                                .getId()
                )

                .claim(
                        "perfil",
                        usuarioDetails
                                .getUsuario()
                                .getPerfil()
                                .name()
                )

                .issuedAt(
                        new Date()
                )

                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + expiration
                        )
                )

                .signWith(
                        secretKey
                )

                .compact();
    }


    /**
     * Extrai o email do usuário do JWT.
     */
    public String extrairEmail(
            String token
    ) {

        return extrairClaim(
                token,
                Claims::getSubject
        );
    }


    /**
     * Extrai um Claim específico.
     */
    public <T> T extrairClaim(
            String token,
            Function<Claims, T> resolver
    ) {

        Claims claims =
                Jwts.parser()

                        .verifyWith(
                                secretKey
                        )

                        .build()

                        .parseSignedClaims(
                                token
                        )

                        .getPayload();

        return resolver.apply(
                claims
        );
    }


    /**
     * Verifica se o token pertence ao usuário
     * e ainda está válido.
     */
    public boolean tokenValido(
            String token,
            UserDetails userDetails
    ) {

        try {

            String email =
                    extrairEmail(token);

            return email.equals(
                    userDetails.getUsername()
            )
                    && !tokenExpirado(token);

        } catch (Exception e) {

            return false;
        }
    }


    /**
     * Verifica se o JWT expirou.
     */
    private boolean tokenExpirado(
            String token
    ) {

        Date expiration =
                extrairClaim(
                        token,
                        Claims::getExpiration
                );

        return expiration.before(
                new Date()
        );
    }
}