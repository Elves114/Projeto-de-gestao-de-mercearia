package WD.works.V2.movimentoStock.dto;

import WD.works.V2.movimentoStock.acoes.Acao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimentoStockRequest {

    @NotNull(message = "A ação é obrigatória")
    private Acao acao;

    @Positive(message = "A quantidade deve ser maior que zero")
    private Integer quantidade;

    @PositiveOrZero(
            message = "A quantidade do ajuste não pode ser negativa"
    )
    private Integer quantidadeAjuste;

    @NotNull(message = "O produto é obrigatório")
    private Long produtoId;

    @Size(
            max = 500,
            message = "A descrição não pode ultrapassar 500 caracteres"
    )
    private String descricao;
}