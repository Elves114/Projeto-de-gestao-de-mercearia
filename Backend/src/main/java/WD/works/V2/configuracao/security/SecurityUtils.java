package WD.works.V2.configuracao.security;

import WD.works.V2.usuario.auth.security.UsuarioDetails;
import WD.works.V2.usuario.entity.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public Usuario getUsuarioAutenticado() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "Usuário não autenticado."
            );
        }

        Object principal =
                authentication.getPrincipal();

        if (!(principal instanceof UsuarioDetails usuarioDetails)) {

            throw new IllegalStateException(
                    "Usuário autenticado inválido."
            );
        }

        return usuarioDetails.getUsuario();
    }

    public Long getUsuarioId() {
        return getUsuarioAutenticado().getId();
    }

    public Long getEmpresaId() {
        return getUsuarioAutenticado()
                .getEmpresa()
                .getId();
    }
}