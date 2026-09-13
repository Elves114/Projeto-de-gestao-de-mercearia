package WD.works.V2.usuario.auth.dto;

import WD.works.V2.usuario.perfil.Perfil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthMeResponse {

    private Long id;
    private String nome;
    private String email;
    private Perfil perfil;
    private Long empresaId;
}