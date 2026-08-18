package WD.works.V2.produtos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoRequest {

    @NotBlank(message = "O nome do produto é obrigatório")
    @Size(max = 150, message = "O nome não pode ultrapassar 150 caracteres")
    private String nome;

    @NotNull(message = "O preço de compra é obrigatório")
    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "O preço de compra não pode ser negativo"
    )
    private BigDecimal precoCompra;

    @NotNull(message = "O preço de venda é obrigatório")
    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "O preço de venda não pode ser negativo"
    )
    private BigDecimal precoVenda;

    @NotNull(message = "A categoria é obrigatória")
    private Long categoriaId;
}