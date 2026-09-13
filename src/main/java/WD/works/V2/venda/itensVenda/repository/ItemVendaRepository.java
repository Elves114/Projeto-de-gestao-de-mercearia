package WD.works.V2.venda.itensVenda.repository;

import WD.works.V2.venda.itensVenda.entity.ItemVenda;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemVendaRepository
        extends JpaRepository<ItemVenda, Long> {

    List<ItemVenda> findByVendaId(Long vendaId);

    boolean existsByProdutoId(Long produtoId);

    /*
     * ============================================================
     * DASHBOARD — PRODUTOS MAIS VENDIDOS
     * ============================================================
     *
     * Retorna o top N de produtos mais vendidos da empresa
     * dentro de um intervalo temporal, agregados por produto
     * e ordenados por quantidade total vendida (descendente).
     *
     * Cada linha do resultado é um Object[] com:
     *   [0] -> Long   produtoId
     *   [1] -> String produtoNome
     *   [2] -> Long   quantidadeVendida
     *
     * O isolamento multiempresa é garantido através da
     * relação ItemVenda -> Venda -> Empresa.
     */

    @Query("""
            SELECT
                i.produto.id,
                i.produto.nome,
                SUM(i.quantidade)
            FROM ItemVenda i
            WHERE i.venda.empresa.id = :empresaId
              AND i.venda.dataVenda >= :inicio
              AND i.venda.dataVenda < :fim
            GROUP BY i.produto.id, i.produto.nome
            ORDER BY SUM(i.quantidade) DESC
            """)
    List<Object[]> produtosMaisVendidos(
            @Param("empresaId") Long empresaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            Pageable pageable);
}