package WD.works.V2.dashboard.service;

import WD.works.V2.alertaStock.repository.AlertaStockRepository;
import WD.works.V2.auditoria.entity.Auditoria;
import WD.works.V2.auditoria.repository.AuditoriaRepository;
import WD.works.V2.configuracao.context.EmpresaContext;
import WD.works.V2.dashboard.dto.*;
import WD.works.V2.estoque.repository.EstoqueRepository;
import WD.works.V2.exception.RegraNegocioException;
import WD.works.V2.produtos.repository.ProdutoRepository;
import WD.works.V2.produtos.status.StatusProduto;
import WD.works.V2.venda.itensVenda.repository.ItemVendaRepository;
import WD.works.V2.venda.repository.VendaRepository;
import WD.works.V2.venda.entity.Venda;
import WD.works.V2.estoque.entity.Estoque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final ItemVendaRepository itemVendaRepository;
    private final AuditoriaRepository auditoriaRepository;

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

            throw new RegraNegocioException(
                    "Período inválido."
            );
        }

        LocalDateTime inicioGraficoDateTime =
                inicioGrafico.atStartOfDay();

        LocalDateTime fimGraficoDateTime =
                hoje.plusDays(1).atStartOfDay();


        List<VendaGraficoResponse> resultadosGrafico =
                vendaRepository.vendasPorPeriodo(
                        empresaId,
                        inicioGraficoDateTime,
                        fimGraficoDateTime
                );

        Map<LocalDate, BigDecimal> vendasPorDia =
                new LinkedHashMap<>();

        for (VendaGraficoResponse r : resultadosGrafico) {
            vendasPorDia.put(r.getData(), r.getValor());
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
         * ÚLTIMAS VENDAS
         * ========================================================
         */

        Page<Venda> ultimasVendasPage =
                vendaRepository.findByEmpresaIdOrderByDataVendaDesc(
                        empresaId,
                        PageRequest.of(0, 5)
                );

        List<UltimaVendaResponse> ultimasVendas =
                ultimasVendasPage
                        .getContent()
                        .stream()
                        .map(v -> new UltimaVendaResponse(
                                v.getId(),
                                v.getDataVenda(),
                                v.getTotal(),
                                v.getUsuario().getNome()
                        ))
                        .toList();


        /*
         * ========================================================
         * TICKET MÉDIO DO MÊS
         * ========================================================
         *
         * Fórmula:
         *      valorVendasMes / vendasMes
         *
         * Se não houve vendas no mês, o ticket médio é zero
         * (evitamos divisão por zero).
         */

        BigDecimal ticketMedioMes =
                vendasMes > 0
                        ? valorVendasMes.divide(
                        BigDecimal.valueOf(vendasMes),
                        2,
                        RoundingMode.HALF_UP
                )
                        : BigDecimal.ZERO;


        /*
         * ========================================================
         * STOCK CRÍTICO
         * ========================================================
         *
         * Lista dos produtos com quantidade <= quantidadeMinima,
         * ordenados por severidade (mais crítico primeiro).
         *
         * Inclui produtos sem stock (quantidade = 0), pois
         * satisfazem naturalmente a condição.
         */

        Page<Estoque> stockCriticoPage =
                estoqueRepository.findEstoqueBaixoPorEmpresa(
                        empresaId,
                        PageRequest.of(0, 5)
                );

        List<StockCriticoResponse> stockCritico =
                stockCriticoPage
                        .getContent()
                        .stream()
                        .map(e -> new StockCriticoResponse(
                                e.getProduto().getId(),
                                e.getProduto().getNome(),
                                e.getQuantidade(),
                                e.getQuantidadeMinima()
                        ))
                        .toList();

        /*
         * ========================================================
         * PRODUTOS MAIS VENDIDOS
         * ========================================================
         *
         * Top 5 produtos do mês actual, agregados por produto
         * e ordenados por quantidade total vendida.
         *
         * Usa o mesmo intervalo temporal do mês que os KPIs
         * vendasMes / valorVendasMes / lucroMes / ticketMedioMes,
         * garantindo consistência entre os cartões.
         */

        List<Object[]> produtosMaisVendidosResult =
                itemVendaRepository.produtosMaisVendidos(
                        empresaId,
                        inicioMes,
                        fimMes,
                        PageRequest.of(0, 5)
                );

        List<ProdutoMaisVendidoResponse> produtosMaisVendidos =
                produtosMaisVendidosResult
                        .stream()
                        .map(r -> new ProdutoMaisVendidoResponse(
                                (Long) r[0],
                                (String) r[1],
                                ((Number) r[2]).longValue()
                        ))
                        .toList();

        /*
         * ========================================================
         * ATIVIDADE RECENTE
         * ========================================================
         *
         * Últimas 5 auditorias da empresa do utilizador,
         * ordenadas da mais recente para a mais antiga.
         *
         * Cobre vendas, criação/alteração de produtos,
         * movimentos, logins e tudo o resto que passa
         * pelo AuditoriaService.
         */

        Page<Auditoria> atividadeRecentePage =
                auditoriaRepository.findByEmpresaIdOrderByDataDesc(
                        empresaId,
                        PageRequest.of(0, 5)
                );

        List<AtividadeRecenteResponse> atividadeRecente =
                atividadeRecentePage
                        .getContent()
                        .stream()
                        .map(a -> new AtividadeRecenteResponse(
                                a.getTipo(),
                                a.getGravidade(),
                                a.getDescricao(),
                                a.getData(),
                                a.getUsuario().getNome()
                        ))
                        .toList();

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

                vendasGrafico,

                ultimasVendas,
                ticketMedioMes,
                stockCritico ,
                produtosMaisVendidos,
                atividadeRecente
        );
    }
}

