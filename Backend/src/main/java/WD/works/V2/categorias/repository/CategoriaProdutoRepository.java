package WD.works.V2.categorias.repository;

import WD.works.V2.categorias.entity.CategoriaProduto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaProdutoRepository
        extends JpaRepository<CategoriaProduto, Long> {

    Page<CategoriaProduto> findByEmpresaIdOrderByNomeAsc(
            Long empresaId,
            Pageable pageable
    );
    Optional<CategoriaProduto> findByIdAndEmpresaId(
            Long id,
            Long empresaId
    );

    boolean existsByNomeAndEmpresaId(
            String nome,
            Long empresaId
    );
    Optional<CategoriaProduto> findByNomeAndEmpresaId(
            String nome,
            Long empresaId
    );
    Page<CategoriaProduto> findByEmpresaIdAndNomeContainingIgnoreCaseOrderByNomeAsc(
            Long empresaId,
            String nome,
            Pageable pageable
    );
}