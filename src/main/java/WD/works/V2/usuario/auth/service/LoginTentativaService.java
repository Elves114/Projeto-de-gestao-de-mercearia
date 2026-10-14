package WD.works.V2.usuario.auth.service;

import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginTentativaService {

    private static final int MAX_TENTATIVAS = 5;
    private static final int MINUTOS_BLOQUEIO = 15;

    private final UsuarioRepository usuarioRepository;


    @Transactional
    public void registrarFalha(Usuario usuario) {

        int tentativas =
                usuario.getTentativasLogin() == null
                        ? 0
                        : usuario.getTentativasLogin();

        tentativas++;

        usuario.setTentativasLogin(tentativas);

        if (tentativas >= MAX_TENTATIVAS) {

            usuario.setBloqueadoAte(
                    LocalDateTime.now()
                            .plusMinutes(MINUTOS_BLOQUEIO)
            );
        }

        usuarioRepository.save(usuario);
    }


    @Transactional
    public void registrarSucesso(Usuario usuario) {

        usuario.setTentativasLogin(0);
        usuario.setBloqueadoAte(null);

        usuarioRepository.save(usuario);
    }


    @Transactional
    public boolean estaTemporariamenteBloqueado(
            Usuario usuario
    ) {

        LocalDateTime bloqueadoAte =
                usuario.getBloqueadoAte();

        if (bloqueadoAte == null) {
            return false;
        }

        if (LocalDateTime.now().isBefore(bloqueadoAte)) {
            return true;
        }

        /*
         * O período de bloqueio terminou.
         *
         * Como o bloqueio temporário acabou,
         * podemos permitir novas tentativas.
         *
         * ★ Só fazemos save() se houver de facto
         *   algo para limpar. Caso contrário, evitamos
         *   uma escrita desnecessária a cada login.
         */
        boolean precisaLimpar =
                (usuario.getTentativasLogin() != null
                        && usuario.getTentativasLogin() > 0);

        if (precisaLimpar) {
            usuario.setTentativasLogin(0);
            usuario.setBloqueadoAte(null);
            usuarioRepository.save(usuario);
        } else {
            // bloqueadoAte já é null, mas garantimos consistência
            usuario.setBloqueadoAte(null);
        }

        return false;
    }
}