package WD.works.V2.estoque.service;

import WD.works.V2.empresa.repository.EmpresaRepository;
import WD.works.V2.estoque.dto.EstoqueResponse;
import WD.works.V2.estoque.entity.Estoque;
import WD.works.V2.estoque.repository.EstoqueRepository;
import WD.works.V2.exception.RecursoNaoEncontradoException;
import WD.works.V2.exception.RegraNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final EmpresaRepository empresaRepository;

    @Transactional(readOnly = true)
    public EstoqueResponse buscarPorId(
            Long id,
            Long empresaId
    ) {

        Estoque estoque = buscarEstoqueDaEmpresa(
                id,
                empresaId
        );

        return converterParaResponse(estoque);
    }

    @Transactional(readOnly = true)
    public EstoqueResponse buscarPorProduto(
            Long produtoId,
            Long empresaId
    ) {

        Estoque estoque = estoqueRepository
                .findByProdutoIdAndEmpresaId(
                        produtoId,
                        empresaId
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Estoque não encontrado."
                        )
                );

        return converterParaResponse(estoque);
    }

    @Transactional(readOnly = true)
    public Page<EstoqueResponse> listarPorEmpresa(
            Long empresaId,
            Pageable pageable
    ) {

        validarEmpresa(empresaId);

        return estoqueRepository
                .findByEmpresaId(
                        empresaId,
                        pageable
                )
                .map(this::converterParaResponse);
    }

    @Transactional(readOnly = true)
    public boolean possuiQuantidadeSuficiente(
            Long produtoId,
            Integer quantidade,
            Long empresaId
    ) {

        if (quantidade == null || quantidade <= 0) {
            new RegraNegocioException("A quantidade deve ser maior que zero.");
        }

        Estoque estoque = estoqueRepository
                .findByProdutoIdAndEmpresaId(
                        produtoId,
                        empresaId
                )
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                                "Estoque do produto não encontrado."
                        )
                );

        return estoque.getQuantidade() >= quantidade;
    }

    /*
     * ============================================================
     * Métodos internos
     * ============================================================
     */

    private Estoque buscarEstoqueDaEmpresa(
            Long id,
            Long empresaId
    ) {

        return estoqueRepository
                .findByIdAndEmpresaId(
                        id,
                        empresaId
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Estoque não encontrado."
                        )
                );
    }

    private void validarEmpresa(Long empresaId) {

        empresaRepository.findById(empresaId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Empresa não encontrada."
                        )
                );
    }

    private EstoqueResponse converterParaResponse(
            Estoque estoque
    ) {

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