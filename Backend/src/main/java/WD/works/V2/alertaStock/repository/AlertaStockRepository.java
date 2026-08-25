package WD.works.V2.alertaStock.repository;

import WD.works.V2.alertaStock.entity.AlertaStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlertaStockRepository
        extends JpaRepository<AlertaStock, Long> {

    Optional<AlertaStock> findByEstoqueIdAndAtivoTrue(
            Long estoqueId
    );

    Page<AlertaStock> findByEmpresaIdOrderByCriadoEmDesc(
            Long empresaId,
            Pageable pageable
    );

    Page<AlertaStock> findByEmpresaIdAndAtivoTrueOrderByCriadoEmDesc(
            Long empresaId,
            Pageable pageable
    );

    Optional<AlertaStock> findByIdAndEmpresaId(
            Long id,
            Long empresaId
    );
    long countByEmpresaIdAndAtivoTrue( Long empresaId );
}