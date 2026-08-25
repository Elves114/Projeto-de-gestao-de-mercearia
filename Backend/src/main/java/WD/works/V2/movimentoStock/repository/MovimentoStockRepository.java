package WD.works.V2.movimentoStock.repository;

import WD.works.V2.movimentoStock.entity.MovimentoStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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
}