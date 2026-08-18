package WD.works.V2.venda.itensVenda.repository;

import WD.works.V2.venda.itensVenda.entity.ItemVenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemVendaRepository
        extends JpaRepository<ItemVenda, Long> {

    List<ItemVenda> findByVendaId(Long vendaId);

    boolean existsByProdutoId(Long produtoId);
}