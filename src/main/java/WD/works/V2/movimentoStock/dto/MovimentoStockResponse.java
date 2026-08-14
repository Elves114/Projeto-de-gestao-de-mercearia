package WD.works.V2.movimentoStock.dto;

import WD.works.V2.movimentoStock.acoes.Acao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimentoStockResponse {

    private Long id;

    private Acao acao;

    private Integer quantidade;

    private Integer quantidadeAnterior;

    private Integer quantidadePosterior;

    private String descricao;

    private LocalDateTime data;

    private Long produtoId;

    private String produtoNome;

    private Long usuarioId;

    private String usuarioNome;

    private Long empresaId;
}