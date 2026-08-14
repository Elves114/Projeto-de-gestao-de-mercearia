package WD.works.V2.configuracao.security;

import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.usuario.perfil.Perfil;
import org.springframework.stereotype.Service;

@Service
public class AutorizacaoService {

    /**
     * Verifica se o usuário possui um dos perfis permitidos.
     */
    public boolean possuiPerfil(
            Usuario usuario,
            Perfil... perfis
    ) {

        if (usuario == null
                || usuario.getPerfil() == null) {

            return false;
        }

        for (Perfil perfil : perfis) {

            if (usuario.getPerfil() == perfil) {
                return true;
            }
        }

        return false;
    }

    /**
     * Verifica se o usuário pode visualizar
     * todas as auditorias da empresa.
     */
    public boolean podeVisualizarTodasAuditorias(
            Usuario usuario
    ) {

        return possuiPerfil(
                usuario,
                Perfil.ADMIN,
                Perfil.GERENTE
        );
    }
}