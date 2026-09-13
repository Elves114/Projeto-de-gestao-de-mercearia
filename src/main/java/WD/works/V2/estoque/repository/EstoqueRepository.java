package WD.works.V2.estoque.repository;

import WD.works.V2.estoque.entity.Estoque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EstoqueRepository
        extends JpaRepository<Estoque, Long> {

    Optional<Estoque> findByProdutoId(Long produtoId);

    Optional<Estoque> findByIdAndEmpresaId(
            Long id,
            Long empresaId
    );

    Optional<Estoque> findByProdutoIdAndEmpresaId(
            Long produtoId,
            Long empresaId
    );

    List<Estoque> findByProdutoIdInAndEmpresaId(
            List<Long> produtoIds,
            Long empresaId
    );

    Page<Estoque> findByEmpresaId(
            Long empresaId,
            Pageable pageable
    );
   @Query(" SELECT COALESCE(SUM(e.quantidade), 0) FROM Estoque e WHERE e.empresa.id = :empresaId ")
    long somarStockPorEmpresa(@Param("empresaId") Long empresaId);

    @Query("""
            SELECT e
            FROM Estoque e
            WHERE e.empresa.id = :empresaId
              AND e.quantidade <= e.quantidadeMinima
            ORDER BY
                (e.quantidade - e.quantidadeMinima) ASC,
                e.quantidade ASC
            """)
    Page<Estoque> findEstoqueBaixoPorEmpresa(
            @Param("empresaId") Long empresaId,
            Pageable pageable
    );

    @Query(" SELECT COUNT(e) FROM Estoque e WHERE e.empresa.id = :empresaId AND e.quantidade <= e.quantidadeMinima ")
    long countEstoqueBaixoPorEmpresa( @Param("empresaId") Long empresaId );

    long countByEmpresaIdAndQuantidade(Long empresaId, Integer quantidade);

}