package WD.works.V2.caixa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FechoCaixaResponse {

    private LocalDate data;
    private long numeroVendas;
    private BigDecimal totalVendas;
    private BigDecimal lucroTotal;
    private BigDecimal ticketMedio;
    private BigDecimal totalCancelado;
    private long vendasCanceladas;
    private List<VendaPorUtilizador> vendasPorUtilizador;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendaPorUtilizador {
        private Long usuarioId;
        private String usuarioNome;
        private long numeroVendas;
        private BigDecimal totalVendas;
    }
}