package WD.works.V2.categorias.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaProdutoResponse {

    private Long id;
    private String nome;
    private Long empresaId;
}