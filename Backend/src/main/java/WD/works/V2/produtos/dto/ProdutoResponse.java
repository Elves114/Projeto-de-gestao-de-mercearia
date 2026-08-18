package WD.works.V2.produtos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoResponse {

    private Long id;

    private String nome;

    private BigDecimal precoCompra;

    private BigDecimal precoVenda;

    private Integer quantidade;

    private Long categoriaId;

    private String categoriaNome;

    private Long empresaId;
}