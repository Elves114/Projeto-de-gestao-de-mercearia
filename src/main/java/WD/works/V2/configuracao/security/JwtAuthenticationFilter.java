package WD.works.V2.configuracao.security;

import WD.works.V2.usuario.auth.service.UsuarioDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UsuarioDetailsService usuarioDetailsService;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {


        String authorizationHeader =
                request.getHeader(
                        "Authorization"
                );


        /*
         * Se não existe Authorization,
         * continua a requisição.
         */
        if (authorizationHeader == null
                || !authorizationHeader.startsWith(
                "Bearer "
        )) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }


        /*
         * Remove "Bearer ".
         */
        String token =
                authorizationHeader.substring(
                        7
                );


        String email;

        try {

            email =
                    jwtService.extrairEmail(
                            token
                    );

        } catch (Exception e) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }


        /*
         * Verifica se já existe autenticação
         * no SecurityContext.
         */
        if (email != null
                && SecurityContextHolder
                .getContext()
                .getAuthentication()
                == null) {


            UserDetails userDetails =
                    usuarioDetailsService
                            .loadUserByUsername(
                                    email
                            );


            if (jwtService.tokenValido(
                    token,
                    userDetails
            )) {


                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );


                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );


                SecurityContextHolder
                        .getContext()
                        .setAuthentication(
                                authentication
                        );
            }

        }


        filterChain.doFilter(
                request,
                response
        );
    }
}