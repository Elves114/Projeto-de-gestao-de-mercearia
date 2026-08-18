package WD.works.V2.estoque.repository;

import WD.works.V2.estoque.entity.Estoque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    Optional<Estoque> findByProdutoId(Long produtoId);

    Optional<Estoque> findByIdAndEmpresaId(
            Long id,
            Long empresaId
    );

    Optional<Estoque> findByProdutoIdAndEmpresaId(
            Long produtoId,
            Long empresaId
    );

    Page<Estoque> findByEmpresaId(
            Long empresaId,
            Pageable pageable
    );
}