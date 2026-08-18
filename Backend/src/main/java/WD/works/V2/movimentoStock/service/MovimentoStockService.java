package WD.works.V2.movimentoStock.service;

import WD.works.V2.empresa.repository.EmpresaRepository;
import WD.works.V2.estoque.entity.Estoque;
import WD.works.V2.estoque.repository.EstoqueRepository;
import WD.works.V2.exception.RecursoNaoEncontradoException;
import WD.works.V2.exception.RegraNegocioException;
import WD.works.V2.movimentoStock.dto.MovimentoStockRequest;
import WD.works.V2.movimentoStock.dto.MovimentoStockResponse;
import WD.works.V2.movimentoStock.entity.MovimentoStock;
import WD.works.V2.movimentoStock.repository.MovimentoStockRepository;
import WD.works.V2.produtos.entity.Produto;
import WD.works.V2.produtos.repository.ProdutoRepository;
import WD.works.V2.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MovimentoStockService {

    private final MovimentoStockRepository movimentoRepository;
    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;
    private final EmpresaRepository empresaRepository;

    @Transactional
    public MovimentoStockResponse criar(
            MovimentoStockRequest request,
            Long empresaId,
            Usuario usuario
    ) {

        validarEmpresa(empresaId);

        Produto produto = buscarProduto(
                request.getProdutoId(),
                empresaId
        );

        Estoque estoque = buscarEstoque(
                request.getProdutoId(),
                empresaId
        );

        int quantidadeAnterior =
                estoque.getQuantidade();

        int quantidadePosterior;

        int quantidadeMovimentada;

        switch (request.getAcao()) {

            case ENTRADA -> {

                validarQuantidade(
                        request.getQuantidade()
                );

                quantidadeMovimentada =
                        request.getQuantidade();

                quantidadePosterior =
                        quantidadeAnterior
                                + quantidadeMovimentada;
            }

            case SAIDA -> {

                validarQuantidade(
                        request.getQuantidade()
                );

                quantidadeMovimentada =
                        request.getQuantidade();

                validarStockDisponivel(
                        quantidadeAnterior,
                        quantidadeMovimentada
                );

                quantidadePosterior =
                        quantidadeAnterior
                                - quantidadeMovimentada;
            }

            case DEVOLUCAO -> {

                validarQuantidade(
                        request.getQuantidade()
                );

                quantidadeMovimentada =
                        request.getQuantidade();

                quantidadePosterior =
                        quantidadeAnterior
                                + quantidadeMovimentada;
            }

            case AJUSTE -> {

                validarQuantidadeAjuste(
                        request.getQuantidadeAjuste()
                );

                quantidadePosterior =
                        request.getQuantidadeAjuste();

                quantidadeMovimentada =
                        Math.abs(
                                quantidadePosterior
                                        - quantidadeAnterior
                        );
            }

            default -> throw new RegraNegocioException(
                    "Ação de estoque inválida."
            );
        }

        /*
         * Atualiza o estoque
         */
        estoque.setQuantidade(
                quantidadePosterior
        );

        estoqueRepository.save(estoque);

        /*
         * Cria o histórico do movimento
         */
        MovimentoStock movimento =
                new MovimentoStock();

        movimento.setAcao(
                request.getAcao()
        );

        movimento.setQuantidade(
                quantidadeMovimentada
        );

        movimento.setQuantidadeAnterior(
                quantidadeAnterior
        );

        movimento.setQuantidadePosterior(
                quantidadePosterior
        );

        movimento.setDescricao(
                request.getDescricao()
        );

        movimento.setData(
                LocalDateTime.now()
        );

        /*
         * Relações
         */
        movimento.setEstoque(estoque);

        movimento.setProduto(produto);

        movimento.setEmpresa(
                produto.getEmpresa()
        );

        movimento.setUsuario(usuario);

        MovimentoStock movimentoSalvo =
                movimentoRepository.save(movimento);

        return converterParaResponse(
                movimentoSalvo
        );
    }

    @Transactional(readOnly = true)
    public Page<MovimentoStockResponse> listarPorEmpresa(
            Long empresaId,
            Pageable pageable
    ) {

        validarEmpresa(empresaId);

        return movimentoRepository
                .findByEmpresaIdOrderByDataDesc(
                        empresaId,
                        pageable
                )
                .map(this::converterParaResponse);
    }

    @Transactional(readOnly = true)
    public Page<MovimentoStockResponse> listarPorProduto(
            Long produtoId,
            Long empresaId,
            Pageable pageable
    ) {

        buscarProduto(
                produtoId,
                empresaId
        );

        return movimentoRepository
                .findByProdutoIdAndEmpresaIdOrderByDataDesc(
                        produtoId,
                        empresaId,
                        pageable
                )
                .map(this::converterParaResponse);
    }

    /*
     * ============================================================
     * MÉTODOS INTERNOS
     * ============================================================
     */

    private Produto buscarProduto(
            Long produtoId,
            Long empresaId
    ) {

        return produtoRepository
                .findByIdAndEmpresaId(
                        produtoId,
                        empresaId
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Produto não encontrado."
                        )
                );
    }

    private Estoque buscarEstoque(
            Long produtoId,
            Long empresaId
    ) {

        return estoqueRepository
                .findByProdutoIdAndEmpresaId(
                        produtoId,
                        empresaId
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Estoque do produto não encontrado."
                        )
                );
    }

    private void validarEmpresa(
            Long empresaId
    ) {

        empresaRepository.findById(empresaId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Empresa não encontrada."
                        )
                );
    }

    private void validarQuantidade(
            Integer quantidade
    ) {

        if (quantidade == null || quantidade <= 0) {

            new RegraNegocioException(
    "A quantidade deve ser maior que zero."
);
        }
    }

    private void validarQuantidadeAjuste(
            Integer quantidade
    ) {

        if (quantidade == null || quantidade < 0) {

            throw new RegraNegocioException(
                    "A quantidade do ajuste não pode ser negativa."
            );
        }
    }

    private void validarStockDisponivel(
            int quantidadeAtual,
            int quantidadeSaida
    ) {

        if (quantidadeSaida > quantidadeAtual) {

            throw new RegraNegocioException(
                    "Stock insuficiente. "
                            + "Disponível: "
                            + quantidadeAtual
                            + ", solicitado: "
                            + quantidadeSaida
            );
        }
    }

    private MovimentoStockResponse converterParaResponse(
            MovimentoStock movimento
    ) {

        Usuario usuario =
                movimento.getUsuario();

        return new MovimentoStockResponse(
                movimento.getId(),
                movimento.getAcao(),
                movimento.getQuantidade(),
                movimento.getQuantidadeAnterior(),
                movimento.getQuantidadePosterior(),
                movimento.getDescricao(),
                movimento.getData(),
                movimento.getProduto().getId(),
                movimento.getProduto().getNome(),
                usuario != null
                        ? usuario.getId()
                        : null,
                usuario != null
                        ? usuario.getNome()
                        : null,
                movimento.getEmpresa().getId()
        );
    }
}