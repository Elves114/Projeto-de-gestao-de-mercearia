package WD.works.V2.alertaStock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertaStockResponse {

    private Long id;

    private boolean ativo;

    private LocalDateTime criadoEm;

    private LocalDateTime resolvidoEm;

    private Long estoqueId;

    private Long produtoId;

    private String produtoNome;

    private Integer quantidadeAtual;

    private Integer quantidadeMinima;

    private Long empresaId;
}