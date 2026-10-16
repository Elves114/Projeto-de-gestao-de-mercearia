package WD.works.V2.relatorios.service;

import WD.works.V2.auditoria.entity.Auditoria;
import WD.works.V2.auditoria.repository.AuditoriaRepository;
import WD.works.V2.configuracao.context.EmpresaContext;
import WD.works.V2.estoque.entity.Estoque;
import WD.works.V2.estoque.repository.EstoqueRepository;
import WD.works.V2.movimentoStock.entity.MovimentoStock;
import WD.works.V2.movimentoStock.repository.MovimentoStockRepository;
import WD.works.V2.venda.entity.Venda;
import WD.works.V2.venda.itensVenda.entity.ItemVenda;
import WD.works.V2.venda.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private static final DateTimeFormatter DATA_HORA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final VendaRepository vendaRepository;
    private final EstoqueRepository estoqueRepository;
    private final MovimentoStockRepository movimentoRepository;
    private final AuditoriaRepository auditoriaRepository;
    private final EmpresaContext empresaContext;

    /* ============================================================
       VENDAS
       ============================================================ */

    @Transactional(readOnly = true)
    public byte[] gerarCsvVendas(LocalDate inicio, LocalDate fim) {

        Long empresaId = empresaContext.getEmpresaIdAtual();

        LocalDateTime ini = (inicio != null)
                ? inicio.atStartOfDay()
                : LocalDate.now().minusMonths(1).atStartOfDay();

        LocalDateTime f = (fim != null)
                ? fim.plusDays(1).atStartOfDay()
                : LocalDate.now().plusDays(1).atStartOfDay();

        List<Venda> vendas = vendaRepository
                .findByEmpresaIdAndDataVendaBetween(empresaId, ini, f);

        StringBuilder sb = new StringBuilder();

        // Cabeçalho
        sb.append("ID,Data,Status,Funcionário,Total (MT),Lucro (MT),")
                .append("Produto,Quantidade,Preço Unitário (MT),Subtotal (MT)\n");

        for (Venda v : vendas) {

            for (ItemVenda item : v.getItens()) {
                sb.append(v.getId()).append(",");
                sb.append(v.getDataVenda().format(DATA_HORA)).append(",");
                sb.append(v.getStatus()).append(",");
                sb.append(escape(v.getUsuario().getNome())).append(",");
                sb.append(formatar(v.getTotal())).append(",");
                sb.append(formatar(v.getLucroTotal())).append(",");
                sb.append(escape(item.getProduto().getNome())).append(",");
                sb.append(item.getQuantidade()).append(",");
                sb.append(formatar(item.getPrecoUnitario())).append(",");
                sb.append(formatar(item.getSubtotal())).append("\n");
            }
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    /* ============================================================
       STOCK
       ============================================================ */

    @Transactional(readOnly = true)
    public byte[] gerarCsvStock() {

        Long empresaId = empresaContext.getEmpresaIdAtual();

        List<Estoque> estoques = estoqueRepository
                .findByEmpresaId(empresaId, PageRequest.of(0, 10000))
                .getContent();

        StringBuilder sb = new StringBuilder();

        sb.append("Produto,Quantidade,Quantidade Mínima,Estado,Preço Compra (MT),Preço Venda (MT)\n");

        for (Estoque e : estoques) {

            String estado;

            if (e.getQuantidade() == 0) {
                estado = "SEM STOCK";
            } else if (e.getQuantidade() <= e.getQuantidadeMinima()) {
                estado = "BAIXO";
            } else {
                estado = "NORMAL";
            }

            sb.append(escape(e.getProduto().getNome())).append(",");
            sb.append(e.getQuantidade()).append(",");
            sb.append(e.getQuantidadeMinima()).append(",");
            sb.append(estado).append(",");
            sb.append(formatar(e.getProduto().getPrecoCompra())).append(",");
            sb.append(formatar(e.getProduto().getPrecoVenda())).append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    /* ============================================================
       MOVIMENTOS
       ============================================================ */

    @Transactional(readOnly = true)
    public byte[] gerarCsvMovimentos(LocalDate inicio, LocalDate fim) {

        Long empresaId = empresaContext.getEmpresaIdAtual();

        LocalDateTime ini = (inicio != null)
                ? inicio.atStartOfDay()
                : LocalDate.now().minusMonths(1).atStartOfDay();

        LocalDateTime f = (fim != null)
                ? fim.plusDays(1).atStartOfDay()
                : LocalDate.now().plusDays(1).atStartOfDay();

        List<MovimentoStock> movimentos = movimentoRepository
                .findByEmpresaIdAndDataBetween(empresaId, ini, f);

        StringBuilder sb = new StringBuilder();

        sb.append("ID,Data,Ação,Produto,Quantidade,Anterior,Posterior,Funcionário,Descrição\n");

        for (MovimentoStock m : movimentos) {
            sb.append(m.getId()).append(",");
            sb.append(m.getData().format(DATA_HORA)).append(",");
            sb.append(m.getAcao()).append(",");
            sb.append(escape(m.getProduto().getNome())).append(",");
            sb.append(m.getQuantidade()).append(",");
            sb.append(m.getQuantidadeAnterior()).append(",");
            sb.append(m.getQuantidadePosterior()).append(",");
            sb.append(escape(m.getUsuario().getNome())).append(",");
            sb.append(escape(m.getDescricao() != null ? m.getDescricao() : "")).append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    /* ============================================================
       AUDITORIA
       ============================================================ */

    @Transactional(readOnly = true)
    public byte[] gerarCsvAuditoria(LocalDate inicio, LocalDate fim) {

        Long empresaId = empresaContext.getEmpresaIdAtual();

        LocalDateTime ini = (inicio != null)
                ? inicio.atStartOfDay()
                : LocalDate.now().minusMonths(1).atStartOfDay();

        LocalDateTime f = (fim != null)
                ? fim.plusDays(1).atStartOfDay()
                : LocalDate.now().plusDays(1).atStartOfDay();

        List<Auditoria> auditorias = auditoriaRepository
                .findByEmpresaIdAndDataBetween(empresaId, ini, f);

        StringBuilder sb = new StringBuilder();

        sb.append("ID,Data,Tipo,Gravidade,Tabela,Registo,Descrição,Usuário,IP\n");

        for (Auditoria a : auditorias) {
            sb.append(a.getId()).append(",");
            sb.append(a.getData().format(DATA_HORA)).append(",");
            sb.append(a.getTipo()).append(",");
            sb.append(a.getGravidade()).append(",");
            sb.append(escape(a.getTabela())).append(",");
            sb.append(escape(a.getRegisto())).append(",");
            sb.append(escape(a.getDescricao())).append(",");
            sb.append(escape(a.getUsuario().getNome())).append(",");
            sb.append(escape(a.getIp() != null ? a.getIp() : "")).append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    /* ============================================================
       UTILITÁRIOS
       ============================================================ */

    private String formatar(BigDecimal valor) {
        if (valor == null) return "0.00";
        return valor.setScale(2, java.math.RoundingMode.HALF_UP).toString();
    }

    /**
     * Escapa campos para CSV.
     * Se o valor contém vírgula, aspas ou newline, coloca entre aspas duplas
     * e duplica as aspas internas.
     */
    private String escape(String valor) {

        if (valor == null) {
            return "";
        }

        boolean precisa = valor.contains(",")
                || valor.contains("\"")
                || valor.contains("\n")
                || valor.contains("\r");

        if (!precisa) {
            return valor;
        }

        return "\"" + valor.replace("\"", "\"\"") + "\"";
    }
}