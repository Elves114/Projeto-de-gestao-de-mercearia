package WD.works.V2.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    /*
     * ============================================================
     * PRODUTOS
     * ============================================================
     */

    private long produtosAtivos;

    private long produtosInativos;


    /*
     * ============================================================
     * STOCK
     * ============================================================
     */

    private long quantidadeTotalStock;

    private long stockBaixo;

    private long semStock;


    /*
     * ============================================================
     * ALERTAS
     * ============================================================
     */

    private long alertasAtivos;


    /*
     * ============================================================
     * VENDAS
     * ============================================================
     */

    private long vendasHoje;

    private BigDecimal valorVendasHoje;

    private long vendasMes;

    private BigDecimal valorVendasMes;

    private BigDecimal lucroMes;


    /*
     * ============================================================
     * GRÁFICO DE VENDAS
     * ============================================================
     */

    private List<VendaGraficoResponse> vendasGrafico;
}