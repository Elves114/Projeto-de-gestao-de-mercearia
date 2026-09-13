package WD.works.V2.usuario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlterarSenhaRequest {

    @NotBlank(message = "A senha atual é obrigatória")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senhaAtual;

    @NotBlank(message = "A nova senha é obrigatória")
    @Size(
            min = 8,
            max = 100,
            message = "A nova senha deve ter entre 8 e 100 caracteres"
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String novaSenha;
}