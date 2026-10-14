package WD.works.V2.configuracao.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuração de cache da aplicação.
 *
 * Usa Caffeine (in-memory) por ser:
 *   - Simples
 *   - Rápido
 *   - Sem dependências externas
 *   - Adequado para uma única instância
 *
 * Se um dia precisares de escalar para várias instâncias,
 * trocas para Redis sem alterar os serviços — só a configuração.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {

        CaffeineCacheManager manager =
                new CaffeineCacheManager(
                        "dashboard",
                        "produtos",
                        "categorias",
                        "estoques"
                );

        manager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(1000)
                        .expireAfterWrite(30, TimeUnit.SECONDS)
                        .recordStats()
        );

        return manager;
    }
}