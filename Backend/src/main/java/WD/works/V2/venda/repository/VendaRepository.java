package WD.works.V2.venda.repository;

import WD.works.V2.venda.entity.Venda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VendaRepository
        extends JpaRepository<Venda, Long> {

    Optional<Venda> findByIdAndEmpresaId(
            Long id,
            Long empresaId
    );

    Page<Venda> findByEmpresaIdOrderByDataVendaDesc(
            Long empresaId,
            Pageable pageable
    );

    boolean existsByIdAndEmpresaId(
            Long id,
            Long empresaId
    );
}