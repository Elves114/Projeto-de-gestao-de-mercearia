package WD.works.V2.usuario.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;



public record RedefinirSenhaRequest(

        @NotBlank(message = "O token é obrigatório")
        String token,

        @NotBlank(message = "A nova senha é obrigatória")
        @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres")
        String novaSenha

) {}