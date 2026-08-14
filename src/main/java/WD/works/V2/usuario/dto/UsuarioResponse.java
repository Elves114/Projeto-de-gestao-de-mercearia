package WD.works.V2.usuario.dto;

import WD.works.V2.usuario.perfil.Perfil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private Long id;
    private String nome;
    private String email;
    private Perfil perfil;
    private Long empresaId;
}