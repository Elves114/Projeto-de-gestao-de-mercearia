package WD.works.V2.usuario.auth.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limit do login.
 *
 * Estratégia: chave composta (IP + email).
 *
 * Porquê?
 *   - Só por IP: um escritório com 20 pessoas no mesmo NAT
 *     esgota o limite e bloqueia todos.
 *   - Só por email: um atacante pode testar 1000 emails
 *     diferentes sem ser travado.
 *   - Combinado: protege contra brute-force SEM bloquear
 *     utilizadores legítimos que partilham rede.
 *
 * Configuração via application.properties:
 *   security.rate-limit.capacidade         (default 30)
 *   security.rate-limit.tokens-por-periodo (default 5)
 *   security.rate-limit.periodo-segundos   (default 6)
 */
@Service
public class LoginRateLimitService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private final int capacidade;
    private final int tokensPorPeriodo;
    private final Duration periodo;

    public LoginRateLimitService(
            @Value("${security.rate-limit.capacidade:30}")
            int capacidade,
            @Value("${security.rate-limit.tokens-por-periodo:5}")
            int tokensPorPeriodo,
            @Value("${security.rate-limit.periodo-segundos:6}")
            long periodoSegundos
    ) {
        this.capacidade = capacidade;
        this.tokensPorPeriodo = tokensPorPeriodo;
        this.periodo = Duration.ofSeconds(periodoSegundos);
    }

    private Bucket obterBucket(String chave) {
        return buckets.computeIfAbsent(chave, k -> criarBucket());
    }

    private Bucket criarBucket() {
        Refill refill = Refill.intervally(tokensPorPeriodo, periodo);
        Bandwidth limit = Bandwidth.classic(capacidade, refill);
        return Bucket.builder().addLimit(limit).build();
    }

    /**
     * Verifica se a chave pode realizar mais uma tentativa de login.
     *
     * @param chave normalmente "ip|email"
     * @return true se permitido, false se excedeu o limite
     */
    public boolean permitido(String chave) {
        return obterBucket(chave).tryConsume(1);
    }
}