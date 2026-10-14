package WD.works.V2.configuracao.security;

import WD.works.V2.configuracao.context.AuditoriaContextFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Desactiva o registo automático dos filtros que já fazem parte
 * da SecurityFilterChain ou da cadeia de filtros do Spring MVC.
 *
 * Sem isto, cada filtro é executado duas vezes por pedido:
 *   1. Dentro da Security chain / Filter chain
 *   2. Como bean @Component auto-registado pelo Spring Boot
 *
 * Consequências observadas:
 *   - LoginRateLimitFilter consumia 2 tokens por tentativa
 *   - JwtAuthenticationFilter corria 2x
 *   - AuditoriaContextFilter reescrevia o contexto 2x,
 *     o que podia fazer a auditoria apontar para o
 *     endpoint errado em pedidos concorrentes.
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

    /**
     * ★ NOVO — antes em falta!
     *
     * Sem isto, o AuditoriaContextFilter corria 2x por pedido,
     * sobrescrevendo o RequestScope do AuditoriaContext e
     * fazendo a auditoria registar IP/método/endpoint errados.
     */
    @Bean
    public FilterRegistrationBean<AuditoriaContextFilter>
    auditoriaContextFilterRegistration(
            AuditoriaContextFilter filter
    ) {
        FilterRegistrationBean<AuditoriaContextFilter> registration =
                new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}