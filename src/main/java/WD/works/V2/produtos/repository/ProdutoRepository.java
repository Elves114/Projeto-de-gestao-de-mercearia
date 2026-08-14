package WD.works.V2.produtos.repository;

import WD.works.V2.produtos.entity.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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
}