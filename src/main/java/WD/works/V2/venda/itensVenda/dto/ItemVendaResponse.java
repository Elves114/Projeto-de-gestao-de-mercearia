package WD.works.V2.venda.itensVenda.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemVendaResponse {

    private Long id;

    private Long produtoId;
    private String produtoNome;

    private Integer quantidade;

    private BigDecimal precoUnitario;
    private BigDecimal subtotal;
    private BigDecimal lucro;
}