package WD.works.V2.estoque.service;

import WD.works.V2.alertaStock.event.StockAlteradoEvent;
import WD.works.V2.configuracao.context.EmpresaContext;
import WD.works.V2.estoque.dto.EstoqueResponse;
import WD.works.V2.estoque.entity.Estoque;
import WD.works.V2.estoque.repository.EstoqueRepository;
import WD.works.V2.exception.RecursoNaoEncontradoException;
import WD.works.V2.exception.RegraNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final EmpresaContext empresaContext;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional(readOnly = true)
    public EstoqueResponse buscarPorId(Long id) {
        Long empresaId = empresaContext.getEmpresaIdAtual();
        Estoque estoque = buscarEstoqueDaEmpresa(id, empresaId);
        return converterParaResponse(estoque);
    }

    @Transactional(readOnly = true)
    public EstoqueResponse buscarPorProduto(Long produtoId) {
        Long empresaId = empresaContext.getEmpresaIdAtual();
        Estoque estoque = estoqueRepository
                .findByProdutoIdAndEmpresaId(produtoId, empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Estoque não encontrado."
                ));
        return converterParaResponse(estoque);
    }

    @Transactional(readOnly = true)
    public Page<EstoqueResponse> listarPorEmpresa(Pageable pageable) {
        Long empresaId = empresaContext.getEmpresaIdAtual();
        return estoqueRepository
                .findByEmpresaId(empresaId, pageable)
                .map(this::converterParaResponse);
    }

    @Transactional
    public EstoqueResponse alterarQuantidadeMinima(Long produtoId, Integer quantidadeMinima) {

        Long empresaId = empresaContext.getEmpresaIdAtual();

        if (quantidadeMinima == null || quantidadeMinima < 0) {
            throw new RegraNegocioException(
                    "A quantidade mínima não pode ser negativa."
            );
        }

        Estoque estoque = estoqueRepository
                .findByProdutoIdAndEmpresaId(produtoId, empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Estoque do produto não encontrado."
                ));

        estoque.setQuantidadeMinima(quantidadeMinima);
        Estoque estoqueAtualizado = estoqueRepository.save(estoque);

        // Publica evento (I6)
        applicationEventPublisher.publishEvent(
                new StockAlteradoEvent(estoqueAtualizado)
        );

        return converterParaResponse(estoqueAtualizado);
    }

    /* ============================================================
       MÉTODOS INTERNOS
       ============================================================ */

    private Estoque buscarEstoqueDaEmpresa(Long id, Long empresaId) {
        return estoqueRepository
                .findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Estoque não encontrado."
                ));
    }

    private EstoqueResponse converterParaResponse(Estoque estoque) {
        return new EstoqueResponse(
                estoque.getId(),
                estoque.getProduto().getId(),
                estoque.getProduto().getNome(),
                estoque.getQuantidade(),
                estoque.getQuantidadeMinima(),
                estoque.getEmpresa().getId()
        );
    }
}