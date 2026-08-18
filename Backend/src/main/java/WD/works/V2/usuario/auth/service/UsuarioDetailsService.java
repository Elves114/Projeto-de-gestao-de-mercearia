package WD.works.V2.usuario.auth.service;

import WD.works.V2.usuario.auth.security.UsuarioDetails;
import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioDetailsService
        implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(
            String email
    ) throws UsernameNotFoundException {

        Usuario usuario =
                usuarioRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "Usuário não encontrado."
                                )
                        );

        return new UsuarioDetails(usuario);
    }
}