package WD.works.V2.estoque.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstoqueResponse {

    private Long id;

    private Long produtoId;
    private String produtoNome;

    private Integer quantidade;
    private Integer quantidadeMinima;

    private Long empresaId;
}