package WD.works.V2.venda.dto;

import WD.works.V2.venda.itensVenda.dto.ItemVendaResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendaResponse {

    private Long id;

    private LocalDateTime dataVenda;

    private BigDecimal total;

    private BigDecimal lucroTotal;

    private Long usuarioId;
    private String usuarioNome;

    private Long empresaId;

    private List<ItemVendaResponse> itens;
}