package WD.works.V2.empresa.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaRequest {

    @NotBlank(message = "O nome da empresa é obrigatório")
    @Size(max = 150, message = "O nome não pode ultrapassar 150 caracteres")
    private String nome;

    @NotBlank(message = "O NUIT é obrigatório")
    @Pattern(
            regexp = "\\d{9}",
            message = "O NUIT deve conter exatamente 9 dígitos"
    )
    private String nuit;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Informe um email válido")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "O contacto é obrigatório")
    @Pattern(
            regexp = "\\d{9}",
            message = "O contacto deve conter exatamente 9 dígitos"
    )
    private String contacto;

    @Size(max = 255, message = "O endereço não pode ultrapassar 255 caracteres")
    private String endereco;
}