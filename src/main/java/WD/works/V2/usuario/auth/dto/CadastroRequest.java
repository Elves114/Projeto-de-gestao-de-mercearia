package WD.works.V2.usuario.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class CadastroRequest {

    // =========================
    // EMPRESA
    // =========================

    @NotBlank(message = "O nome da empresa é obrigatório")
    @Size(max = 150, message = "O nome não pode ultrapassar 150 caracteres")
    private String nomeEmpresa;

    @NotBlank(message = "O NUIT é obrigatório")
    @Pattern(
            regexp = "\\d{9}",
            message = "O NUIT deve conter exatamente 9 dígitos"
    )
    private String nuit;

    @NotBlank(message = "O email da empresa é obrigatório")
    @Email(message = "Informe um email válido para a empresa")
    @Size(max = 150)
    private String emailEmpresa;

    @NotBlank(message = "O contacto é obrigatório")
    @Pattern(
            regexp = "\\d{9}",
            message = "O contacto deve conter exatamente 9 dígitos"
    )
    private String contacto;

    @Size(
            max = 255,
            message = "O endereço não pode ultrapassar 255 caracteres"
    )
    private String endereco;


    // =========================
    // ADMINISTRADOR
    // =========================

    @NotBlank(message = "O nome do administrador é obrigatório")
    @Size(
            max = 150,
            message = "O nome não pode ultrapassar 150 caracteres"
    )
    private String nomeAdministrador;

    @NotBlank(message = "O email do administrador é obrigatório")
    @Email(message = "Informe um email válido para o administrador")
    private String emailAdministrador;

    @NotBlank(message = "A senha do administrador é obrigatória")
    @Size(
            min = 8,
            max = 100,
            message = "A senha deve ter entre 8 e 100 caracteres"
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senhaAdministrador;
}