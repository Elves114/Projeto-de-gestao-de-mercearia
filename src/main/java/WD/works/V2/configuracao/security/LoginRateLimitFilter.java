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

        String path = request.getRequestURI();

        if (!request.getMethod().equalsIgnoreCase("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        String chave = null;

        // /auth/login → chave = IP + email
        if (path.equals("/auth/login")) {
            CachedBodyHttpServletRequest cached =
                    new CachedBodyHttpServletRequest(request);
            String ip = obterIp(cached);
            String email = extrairEmail(cached);
            chave = "login:" + ip + "|" +
                    (email != null ? email.toLowerCase() : "anon");
            if (!loginRateLimitService.permitido(chave)) {
                responder429(response);
                return;
            }
            filterChain.doFilter(cached, response);
            return;
        }

        // /auth/refresh → chave = IP
        if (path.equals("/auth/refresh")) {
            String ip = obterIp(request);
            chave = "refresh:" + ip;
            if (!loginRateLimitService.permitido(chave)) {
                responder429(response);
                return;
            }
            filterChain.doFilter(request, response);
            return;
        }

        // /auth/recuperar-senha → chave = IP + email
        if (path.equals("/auth/recuperar-senha")) {
            CachedBodyHttpServletRequest cached =
                    new CachedBodyHttpServletRequest(request);
            String ip = obterIp(cached);
            String email = extrairEmail(cached);
            chave = "recuperar:" + ip + "|" +
                    (email != null ? email.toLowerCase() : "anon");
            if (!loginRateLimitService.permitido(chave)) {
                responder429(response);
                return;
            }
            filterChain.doFilter(cached, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void responder429(HttpServletResponse response) throws IOException {
        response.setStatus(429);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("""
            {
              "status": 429,
              "erro": "Too Many Requests",
              "mensagem": "Demasiados pedidos. Tente novamente em alguns segundos."
            }
        """);
    }

    private String obterIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) return realIp;
        return request.getRemoteAddr();
    }

    private String extrairEmail(CachedBodyHttpServletRequest request) {
        try {
            byte[] body = request.getCachedBody();
            if (body == null || body.length == 0) return null;
            String json = new String(body, StandardCharsets.UTF_8);
            JsonNode node = objectMapper.readTree(json);
            return node.has("email") ? node.get("email").asText() : null;
        } catch (Exception e) {
            return null;
        }
    }
}