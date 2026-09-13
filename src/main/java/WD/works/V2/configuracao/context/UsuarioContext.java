package WD.works.V2.configuracao.context;

import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.usuario.auth.security.UsuarioDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioContext {

    public Usuario getUsuarioAtual() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new SecurityException(
                    "Usuário não autenticado."
            );
        }

        Object principal =
                authentication.getPrincipal();

        if (!(principal instanceof UsuarioDetails usuarioDetails)) {

            throw new SecurityException(
                    "Usuário autenticado inválido."
            );
        }

        return usuarioDetails.getUsuario();
    }
}