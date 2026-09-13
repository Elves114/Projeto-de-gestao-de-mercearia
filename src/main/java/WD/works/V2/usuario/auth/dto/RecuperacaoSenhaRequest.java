package WD.works.V2.usuario.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RecuperacaoSenhaRequest(

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Email inválido")
        String email

) {}