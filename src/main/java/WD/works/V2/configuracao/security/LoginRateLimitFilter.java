package WD.works.V2.configuracao.security;

import WD.works.V2.usuario.auth.service.LoginRateLimitService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Aplica rate limit ao endpoint /auth/login.
 *
 * Chave = IP + email.
 *
 * Lê o corpo do pedido UMA VEZ (via CachedBodyHttpServletRequest)
 * para extrair o email, sem impedir o controller de o ler depois.
 */
@Component
@RequiredArgsConstructor
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private final LoginRateLimitService loginRateLimitService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Só se aplica a POST /auth/login
        if (!request.getRequestURI().equals("/auth/login")
                || !request.getMethod().equalsIgnoreCase("POST")) {

            filterChain.doFilter(request, response);
            return;
        }

        // Faz cache do corpo para poder ler o email
        // sem consumir o stream antes do controller
        CachedBodyHttpServletRequest cachedRequest =
                new CachedBodyHttpServletRequest(request);

        String ip = obterIp(cachedRequest);
        String email = extrairEmail(cachedRequest);

        String chave = ip + "|" + (email != null ? email.toLowerCase() : "anon");

        if (!loginRateLimitService.permitido(chave)) {

            response.setStatus(429);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write("""
                {
                  "status": 429,
                  "erro": "Too Many Requests",
                  "mensagem": "Demasiadas tentativas de login. Tente novamente em alguns segundos."
                }
            """);

            return;
        }

        // Continua com o request em cache
        filterChain.doFilter(cachedRequest, response);
    }

    /**
     * Obtém o IP real do cliente, tratando proxies.
     */
    private String obterIp(HttpServletRequest request) {

        String xff = request.getHeader("X-Forwarded-For");

        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");

        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * Extrai o campo "email" do corpo JSON.
     * Devolve null se não existir ou não for JSON válido.
     */
    private String extrairEmail(CachedBodyHttpServletRequest request) {

        try {

            byte[] body = request.getCachedBody();

            if (body == null || body.length == 0) {
                return null;
            }

            String json = new String(body, StandardCharsets.UTF_8);

            JsonNode node = objectMapper.readTree(json);

            return node.has("email")
                    ? node.get("email").asText()
                    : null;

        } catch (Exception e) {

            return null;
        }
    }
}