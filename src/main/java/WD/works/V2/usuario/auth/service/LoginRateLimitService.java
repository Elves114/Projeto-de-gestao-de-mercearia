package WD.works.V2.usuario.auth.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginRateLimitService {

    /*
     * Um bucket separado para cada IP.
     */
    private final Map<String, Bucket> buckets =
            new ConcurrentHashMap<>();


    /*
     * Configuração do limite.
     *
     * Cada IP começa com 10 tokens.
     *
     * Depois de consumir os 10 tokens,
     * recebe 1 novo token a cada 6 segundos.
     */
    private static final int CAPACIDADE = 10;

    private static final int TOKENS_POR_PERIODO = 1;

    private static final Duration PERIODO =
            Duration.ofSeconds(6);


    /**
     * Obtém o bucket do IP.
     *
     * Se ainda não existir, cria um novo.
     */
    private Bucket obterBucket(String ip) {

        return buckets.computeIfAbsent(
                ip,
                chave -> criarBucket()
        );
    }


    /**
     * Cria o controle de limite.
     */
    private Bucket criarBucket() {

        Refill refill =
                Refill.intervally(
                        TOKENS_POR_PERIODO,
                        PERIODO
                );

        Bandwidth limit =
                Bandwidth.classic(
                        CAPACIDADE,
                        refill
                );

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }


    /**
     * Verifica se o IP pode realizar
     * mais uma requisição de login.
     *
     * @return true se permitido
     * @return false se excedeu o limite
     */
    public boolean permitido(String ip) {

        Bucket bucket =
                obterBucket(ip);

        return bucket.tryConsume(1);
    }
}

