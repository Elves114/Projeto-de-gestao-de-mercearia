package WD.works.V2.usuario.dto;

import jakarta.validation.constraints.Email;
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
public class UsuarioAtualizacaoRequest {

    @NotBlank(message = "O nome é obrigatório")
    @Size(
            max = 150,
            message = "O nome não pode ultrapassar 150 caracteres"
    )
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Informe um email válido")
    private String email;
}

