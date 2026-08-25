package WD.works.V2.configuracao.context;

import WD.works.V2.configuracao.context.AuditoriaContext;
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
public class AuditoriaContextFilter
        extends OncePerRequestFilter {

    private final AuditoriaContext auditoriaContext;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String ip = obterIp(request);

        String metodo = request.getMethod();

        String endpoint = request.getRequestURI();

        auditoriaContext.definir(
                ip,
                metodo,
                endpoint
        );

        filterChain.doFilter(
                request,
                response
        );
    }

    private String obterIp(
            HttpServletRequest request
    ) {

        String forwardedFor =
                request.getHeader("X-Forwarded-For");

        if (forwardedFor != null
                && !forwardedFor.isBlank()) {

            return forwardedFor
                    .split(",")[0]
                    .trim();
        }

        return request.getRemoteAddr();
    }
}