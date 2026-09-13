package WD.works.V2.configuracao.security;

import WD.works.V2.usuario.auth.service.LoginRateLimitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private final LoginRateLimitService loginRateLimitService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        /*
         * O rate limit será aplicado somente ao login.
         */
        if (!request.getRequestURI().equals("/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        /*
         * Permite somente POST.
         */
        if (!request.getMethod().equalsIgnoreCase("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        /*
         * Obtém o IP do cliente.
         */
        String ip = obterIp(request);

        /*
         * Verifica o limite.
         */
        if (!loginRateLimitService.permitido(ip)) {

            response.setStatus(429);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write("""
            {
                "status": 429,
                "erro": "Too Many Requests",
                "mensagem": "Muitas tentativas de login. Tente novamente mais tarde."
            }
            """);

            return;
        }

        /*
         * Continua o processamento normal.
         */
        filterChain.doFilter(request, response);
    }


    /**
     * Obtém o IP do cliente.
     *
     * Em produção, se existir um proxy/load balancer,
     * devemos tratar X-Forwarded-For de forma segura.
     */
    private String obterIp(HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");

        if (ip != null && !ip.isBlank()) {
            return ip.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}