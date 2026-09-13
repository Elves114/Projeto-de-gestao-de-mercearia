package WD.works.V2.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProdutoMaisVendidoResponse {

    private Long produtoId;
    private String produtoNome;
    private Long quantidadeVendida;
}