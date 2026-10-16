package WD.works.V2.movimentoStock.repository;

import WD.works.V2.movimentoStock.entity.MovimentoStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimentoStockRepository
        extends JpaRepository<MovimentoStock, Long> {

    Page<MovimentoStock> findByEmpresaIdOrderByDataDesc(
            Long empresaId,
            Pageable pageable
    );

    Page<MovimentoStock> findByProdutoIdAndEmpresaIdOrderByDataDesc(
            Long produtoId,
            Long empresaId,
            Pageable pageable
    );

    Page<MovimentoStock> findByEmpresaIdAndProdutoNomeContainingIgnoreCaseOrderByDataDesc(
            Long empresaId,
            String nome,
            Pageable pageable
    );

    @Query("""
    SELECT m FROM MovimentoStock m
    WHERE m.empresa.id = :empresaId
      AND m.data >= :inicio
      AND m.data < :fim
    ORDER BY m.data DESC
    """)
    List<MovimentoStock> findByEmpresaIdAndDataBetween(
            @Param("empresaId") Long empresaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}