package WD.works.V2.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockCriticoResponse {

    private Long produtoId;
    private String produtoNome;
    private Integer quantidade;
    private Integer quantidadeMinima;
}