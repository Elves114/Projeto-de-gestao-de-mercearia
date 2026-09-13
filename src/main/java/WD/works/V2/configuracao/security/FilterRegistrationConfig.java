package WD.works.V2.configuracao.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Desactiva o registo automático de filtros que já fazem parte da
 * SecurityFilterChain.
 *
 * Sem isto, cada filtro é executado duas vezes por pedido:
 *   1. Dentro da Security chain (via addFilterBefore)
 *   2. Como bean @Component auto-registado pelo Spring Boot
 *
 * No caso do LoginRateLimitFilter, isto consumia 2 tokens por
 * tentativa de login, reduzindo o limite efectivo a metade.
 */
@Configuration
public class FilterRegistrationConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter>
    jwtAuthenticationFilterRegistration(
            JwtAuthenticationFilter filter
    ) {

        FilterRegistrationBean<JwtAuthenticationFilter> registration =
                new FilterRegistrationBean<>(filter);

        registration.setEnabled(false);

        return registration;
    }


    @Bean
    public FilterRegistrationBean<LoginRateLimitFilter>
    loginRateLimitFilterRegistration(
            LoginRateLimitFilter filter
    ) {

        FilterRegistrationBean<LoginRateLimitFilter> registration =
                new FilterRegistrationBean<>(filter);

        registration.setEnabled(false);

        return registration;
    }
}