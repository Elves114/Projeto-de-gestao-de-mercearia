package WD.works.V2.estoque.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstoqueQuantidadeMinimaRequest {

    @NotNull(message = "A quantidade mínima é obrigatória")
    @PositiveOrZero(
            message = "A quantidade mínima não pode ser negativa"
    )
    private Integer quantidadeMinima;
}