package WD.works.V2.produtos.repository;

import WD.works.V2.produtos.entity.Produto;
import WD.works.V2.produtos.status.StatusProduto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProdutoRepository
        extends JpaRepository<Produto, Long> {

    Page<Produto> findByEmpresaIdOrderByNomeAsc(
            Long empresaId,
            Pageable pageable
    );

    boolean existsByIdAndEmpresaId(
            Long id,
            Long empresaId
    );

    List<Produto> findByEmpresaId(
            Long empresaId
    );

    Optional<Produto> findByIdAndEmpresaId(
            Long id,
            Long empresaId
    );

    Optional<Produto> findByNomeAndEmpresaId(
            String nome,
            Long empresaId
    );

    boolean existsByNomeAndEmpresaId(
            String nome,
            Long empresaId
    );

    Page<Produto> findByEmpresaIdAndNomeContainingIgnoreCase(
            Long empresaId,
            String nome,
            Pageable pageable
    );

    boolean existsByCategoriaIdAndEmpresaId(
            Long categoriaId,
            Long empresaId
    );

    long countByEmpresaIdAndStatus(
            Long empresaId,
            StatusProduto status
    );



    @Query("""
    SELECT p
    FROM Produto p
    JOIN Estoque e ON e.produto.id = p.id
    WHERE p.empresa.id = :empresaId
      AND (:status IS NULL OR p.status = :status)
      AND (:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
      AND (:categoriaId IS NULL OR p.categoria.id = :categoriaId)
      AND (:precoMin IS NULL OR p.precoVenda >= :precoMin)
      AND (:precoMax IS NULL OR p.precoVenda <= :precoMax)
      AND (:quantidadeMin IS NULL OR e.quantidade >= :quantidadeMin)
      AND (:quantidadeMax IS NULL OR e.quantidade <= :quantidadeMax)
    ORDER BY p.nome ASC
    """)
    Page<Produto> pesquisarComFiltros(
            @Param("empresaId") Long empresaId,
            @Param("status") StatusProduto status,
            @Param("nome") String nome,
            @Param("categoriaId") Long categoriaId,
            @Param("precoMin") BigDecimal precoMin,
            @Param("precoMax") BigDecimal precoMax,
            @Param("quantidadeMin") Integer quantidadeMin,
            @Param("quantidadeMax") Integer quantidadeMax,
            Pageable pageable
    );
}