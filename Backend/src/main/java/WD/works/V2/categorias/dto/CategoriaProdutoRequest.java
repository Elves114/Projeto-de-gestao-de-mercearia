package WD.works.V2.categorias.dto;

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
public class CategoriaProdutoRequest {

    @NotBlank(message = "O nome da categoria é obrigatório")
    @Size(max = 100, message = "O nome não pode ultrapassar 100 caracteres")
    private String nome;
}