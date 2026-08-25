package WD.works.V2.dashboard.service;

import WD.works.V2.alertaStock.repository.AlertaStockRepository;
import WD.works.V2.configuracao.context.EmpresaContext;
import WD.works.V2.dashboard.dto.DashboardResponse;
import WD.works.V2.dashboard.dto.VendaGraficoResponse;
import WD.works.V2.estoque.repository.EstoqueRepository;
import WD.works.V2.produtos.repository.ProdutoRepository;
import WD.works.V2.produtos.status.StatusProduto;
import WD.works.V2.venda.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;
    private final AlertaStockRepository alertaStockRepository;
    private final VendaRepository vendaRepository;

    private final EmpresaContext empresaContext;


    @Transactional(readOnly = true)
    public DashboardResponse obterDashboard(int periodo) {

        /*
         * ========================================================
         * EMPRESA
         * ========================================================
         */

        Long empresaId =
                empresaContext.getEmpresaIdAtual();


        /*
         * ========================================================
         * PRODUTOS
         * ========================================================
         */

        long produtosAtivos =
                produtoRepository.countByEmpresaIdAndStatus(
                        empresaId,
                        StatusProduto.ATIVO
                );

        long produtosInativos =
                produtoRepository.countByEmpresaIdAndStatus(
                        empresaId,
                        StatusProduto.INATIVO
                );


        /*
         * ========================================================
         * STOCK
         * ========================================================
         */

        long quantidadeTotalStock =
                estoqueRepository.somarStockPorEmpresa(
                        empresaId
                );

        long stockBaixo =
                estoqueRepository.countEstoqueBaixoPorEmpresa(
                        empresaId
                );

        long semStock =
                estoqueRepository.countByEmpresaIdAndQuantidade(
                        empresaId,
                        0
                );


        /*
         * ========================================================
         * ALERTAS
         * ========================================================
         */

        long alertasAtivos =
                alertaStockRepository.countByEmpresaIdAndAtivoTrue(
                        empresaId
                );


        /*
         * ========================================================
         * PERÍODOS
         * ========================================================
         */

        LocalDate hoje =
                LocalDate.now();

        LocalDateTime inicioHoje =
                hoje.atStartOfDay();

        LocalDateTime fimHoje =
                hoje.plusDays(1)
                        .atStartOfDay();


        LocalDate primeiroDiaMes =
                hoje.withDayOfMonth(1);

        LocalDateTime inicioMes =
                primeiroDiaMes.atStartOfDay();

        LocalDateTime fimMes =
                primeiroDiaMes
                        .plusMonths(1)
                        .atStartOfDay();


        /*
         * ========================================================
         * VENDAS DE HOJE
         * ========================================================
         */

        long vendasHoje =
                vendaRepository.countVendasPorPeriodo(
                        empresaId,
                        inicioHoje,
                        fimHoje
                );

        BigDecimal valorVendasHoje =
                vendaRepository.somarVendasPorPeriodo(
                        empresaId,
                        inicioHoje,
                        fimHoje
                );


        /*
         * ========================================================
         * VENDAS DO MÊS
         * ========================================================
         */

        long vendasMes =
                vendaRepository.countVendasPorPeriodo(
                        empresaId,
                        inicioMes,
                        fimMes
                );

        BigDecimal valorVendasMes =
                vendaRepository.somarVendasPorPeriodo(
                        empresaId,
                        inicioMes,
                        fimMes
                );

        BigDecimal lucroMes =
                vendaRepository.somarLucroPorPeriodo(
                        empresaId,
                        inicioMes,
                        fimMes
                );



        /*
         * ========================================================
         * GRÁFICO DE VENDAS
         *
         * Últimos 7 dias, incluindo hoje.
         * ========================================================
         */
        LocalDate inicioGrafico;

        if (periodo == 7) {

            inicioGrafico = hoje.minusDays(6);

        } else if (periodo == 30) {

            inicioGrafico = hoje.minusDays(29);

        } else if (periodo == 365) {

            inicioGrafico = hoje.withDayOfYear(1);

        } else {

            throw new IllegalArgumentException(
                    "Período inválido."
            );
        }

        LocalDateTime inicioGraficoDateTime =
                inicioGrafico.atStartOfDay();

        LocalDateTime fimGraficoDateTime =
                hoje.plusDays(1).atStartOfDay();


        List<Object[]> resultadosGrafico =
                vendaRepository.vendasPorPeriodo(
                        empresaId,
                        inicioGraficoDateTime,
                        fimGraficoDateTime
                );

        Map<LocalDate, BigDecimal> vendasPorDia =
                new LinkedHashMap<>();

        for (Object[] resultado : resultadosGrafico) {

            LocalDate data =
                    ((java.sql.Date) resultado[0]).toLocalDate();

            BigDecimal valor =
                    (BigDecimal) resultado[1];

            vendasPorDia.put(data, valor);
        }

        /*
         * ========================================================
         * DADOS DO GRÁFICO
         * ========================================================
         */
        List<VendaGraficoResponse> vendasGrafico =
                new ArrayList<>();

        LocalDate dataAtual = inicioGrafico;

        while (!dataAtual.isAfter(hoje)) {

            BigDecimal valor =
                    vendasPorDia.getOrDefault(
                            dataAtual,
                            BigDecimal.ZERO
                    );

            vendasGrafico.add(
                    new VendaGraficoResponse(
                            dataAtual,
                            valor
                    )
            );

            dataAtual = dataAtual.plusDays(1);
        }



        /*
         * ========================================================
         * RESPONSE
         * ========================================================
         */

        return new DashboardResponse(

                produtosAtivos,
                produtosInativos,

                quantidadeTotalStock,
                stockBaixo,
                semStock,

                alertasAtivos,

                vendasHoje,
                valorVendasHoje,

                vendasMes,
                valorVendasMes,

                lucroMes,

                vendasGrafico
        );
    }
}

