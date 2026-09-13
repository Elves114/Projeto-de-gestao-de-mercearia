package WD.works.V2.usuario.dto;

import WD.works.V2.usuario.perfil.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequest {

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 150, message = "O nome não pode ultrapassar 150 caracteres")
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Informe um email válido")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(
            min = 8,
            max = 100,
            message = "A senha deve ter entre 8 e 100 caracteres"
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;

    @NotNull(message = "O perfil é obrigatório")
    private Perfil perfil;
}