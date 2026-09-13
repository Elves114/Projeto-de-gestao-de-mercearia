package WD.works.V2.produtos.service;

import WD.works.V2.categorias.entity.CategoriaProduto;
import WD.works.V2.categorias.repository.CategoriaProdutoRepository;
import WD.works.V2.configuracao.context.EmpresaContext;
import WD.works.V2.configuracao.context.UsuarioContext;
import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.empresa.repository.EmpresaRepository;
import WD.works.V2.estoque.entity.Estoque;
import WD.works.V2.estoque.repository.EstoqueRepository;
import WD.works.V2.exception.RecursoNaoEncontradoException;
import WD.works.V2.exception.RegraNegocioException;
import WD.works.V2.produtos.dto.ProdutoRequest;
import WD.works.V2.produtos.dto.ProdutoResponse;
import WD.works.V2.produtos.entity.Produto;
import WD.works.V2.produtos.repository.ProdutoRepository;
import WD.works.V2.produtos.status.StatusProduto;
import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.auditoria.dto.AuditoriaDetalhes;
import WD.works.V2.auditoria.dto.AuditoriaRequest;
import WD.works.V2.auditoria.service.AuditoriaService;
import WD.works.V2.auditoria.tipo.TipoAuditoria;
import WD.works.V2.auditoria.gravidade.GravidadeAuditoria;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final EmpresaRepository empresaRepository;
    private final CategoriaProdutoRepository categoriaRepository;
    private final EstoqueRepository estoqueRepository;
    private final EmpresaContext empresaContext;
    private final AuditoriaService auditoriaService;
    private final ObjectMapper objectMapper;
    private final UsuarioContext usuarioContext;

    @Transactional
    public ProdutoResponse criar(
            ProdutoRequest request
    ) {

        Long empresaId =
                empresaContext.getEmpresaIdAtual();

        validarNomeDuplicado(
                request.getNome(),
                empresaId
        );

        Empresa empresa =
                buscarEmpresa(empresaId);

        CategoriaProduto categoria =
                buscarCategoriaDaEmpresa(
                        request.getCategoriaId(),
                        empresaId
                );

        validarPrecos(
                request.getPrecoCompra(),
                request.getPrecoVenda()
        );

        Usuario usuario =
                usuarioContext.getUsuarioAtual();

        Produto produto = new Produto();

        produto.setNome(request.getNome());
        produto.setPrecoCompra(request.getPrecoCompra());
        produto.setPrecoVenda(request.getPrecoVenda());
        produto.setEmpresa(empresa);
        produto.setCategoria(categoria);
        produto.setStatus(StatusProduto.ATIVO);

        /*
         * Registo do autor da criação.
         */
        produto.setCriadoPor(usuario);

        Produto produtoSalvo =
                produtoRepository.save(produto);

        Estoque estoque =
                criarEstoqueInicial(
                        produtoSalvo,
                        empresa
                );

        /*
         * Auditoria.
         */
        auditoriaService.registrar(
                new AuditoriaRequest(
                        TipoAuditoria.CRIACAO,
                        "produto",
                        produtoSalvo.getId().toString(),
                        "Produto '" +
                                produtoSalvo.getNome() +
                                "' foi criado."
                )
        );

        return converterParaResponse(
                produtoSalvo,
                estoque
        );
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(
            Long id
    ) {

        Long empresaId =
                empresaContext.getEmpresaIdAtual();

        Produto produto =
                buscarProdutoDaEmpresa(
                        id,
                        empresaId
                );

        Estoque estoque =
                buscarEstoqueDoProduto(
                        produto.getId(),
                        empresaId
                );

        return converterParaResponse(
                produto,
                estoque
        );
    }

    @Transactional(readOnly = true)
    public Page<ProdutoResponse> listarPorEmpresa(
            Pageable pageable
    ) {

        Long empresaId =
                empresaContext.getEmpresaIdAtual();

        Page<Produto> produtos =
                produtoRepository
                        .findByEmpresaIdOrderByNomeAsc(
                                empresaId,
                                pageable
                        );

        return converterPaginaParaResponse(
                produtos,
                empresaId
        );
    }

    @Transactional(readOnly = true)
    public Page<ProdutoResponse> pesquisarComFiltros(
            String nome,
            StatusProduto status,
            Long categoriaId,
            BigDecimal precoMin,
            BigDecimal precoMax,
            Integer quantidadeMin,
            Integer quantidadeMax,
            Pageable pageable
    ) {

        Long empresaId =
                empresaContext.getEmpresaIdAtual();

        validarFiltros(
                precoMin,
                precoMax,
                quantidadeMin,
                quantidadeMax
        );

        Page<Produto> produtos =
                produtoRepository.pesquisarComFiltros(
                        empresaId,
                        status,
                        normalizarNome(nome),
                        categoriaId,
                        precoMin,
                        precoMax,
                        quantidadeMin,
                        quantidadeMax,
                        pageable
                );

        return converterPaginaParaResponse(
                produtos,
                empresaId
        );
    }

    @Transactional
    public void ativar(Long id) {

        Long empresaId =
                empresaContext.getEmpresaIdAtual();

        Produto produto =
                buscarProdutoDaEmpresa(
                        id,
                        empresaId
                );

        if (produto.getStatus() == StatusProduto.ATIVO) {

            throw new RegraNegocioException(
                    "O produto já está ativo."
            );
        }

        produto.setStatus(StatusProduto.ATIVO);

        produto.setUltimaAlteracaoPor(
                usuarioContext.getUsuarioAtual()
        );

        produtoRepository.save(produto);

        auditoriaService.registrar(
                new AuditoriaRequest(
                        TipoAuditoria.ALTERACAO,
                        "produto",
                        produto.getId().toString(),
                        "Produto '" +
                                produto.getNome() +
                                "' foi ativado."
                )
        );
    }

    @Transactional
    public ProdutoResponse atualizar(
            Long id,
            ProdutoRequest request
    ) {

        Long empresaId =
                empresaContext.getEmpresaIdAtual();

        Produto produto =
                buscarProdutoDaEmpresa(
                        id,
                        empresaId
                );

        /*
         * ============================================================
         * ESTADO ANTERIOR
         * ============================================================
         */

        Estoque estoque =
                buscarEstoqueDoProduto(
                        produto.getId(),
                        empresaId
                );

        ProdutoResponse dadosAntigos =
                converterParaResponse(
                        produto,
                        estoque
                );

        validarNomeNaAtualizacao(
                request.getNome(),
                produto.getId(),
                empresaId
        );

        CategoriaProduto categoria =
                buscarCategoriaDaEmpresa(
                        request.getCategoriaId(),
                        empresaId
                );

        validarPrecos(
                request.getPrecoCompra(),
                request.getPrecoVenda()
        );

        /*
         * ============================================================
         * ALTERAÇÃO
         * ============================================================
         */

        produto.setNome(
                request.getNome()
        );

        produto.setPrecoCompra(
                request.getPrecoCompra()
        );

        produto.setPrecoVenda(
                request.getPrecoVenda()
        );

        produto.setCategoria(
                categoria
        );

        /*
         * Registo do autor da alteração.
         */
        produto.setUltimaAlteracaoPor(
                usuarioContext.getUsuarioAtual()
        );

        Produto produtoAtualizado =
                produtoRepository.save(produto);

        /*
         * ============================================================
         * ESTADO NOVO
         * ============================================================
         */

        ProdutoResponse dadosNovos =
                converterParaResponse(
                        produtoAtualizado,
                        estoque
                );

        /*
         * ============================================================
         * AUDITORIA
         * ============================================================
         */

        AuditoriaRequest auditoriaRequest =
                new AuditoriaRequest(
                        TipoAuditoria.ALTERACAO,
                        "produto",
                        produtoAtualizado.getId().toString(),
                        "Produto atualizado."
                );

        AuditoriaDetalhes detalhes =
                new AuditoriaDetalhes(
                        converterParaJson(dadosAntigos),
                        converterParaJson(dadosNovos),
                        converterParaJson(request)
                );

        auditoriaService.registrar(
                auditoriaRequest,
                usuarioContext.getUsuarioAtual(),
                empresaContext.getEmpresaAtual(),
                GravidadeAuditoria.WARNING,
                detalhes
        );

        return dadosNovos;
    }

    @Transactional
    public void desativar(Long id) {

        Long empresaId =
                empresaContext.getEmpresaIdAtual();

        Produto produto =
                buscarProdutoDaEmpresa(
                        id,
                        empresaId
                );

        if (produto.getStatus() == StatusProduto.INATIVO) {

            throw new RegraNegocioException(
                    "O produto já está inativo."
            );
        }

        produto.setStatus(StatusProduto.INATIVO);

        produto.setUltimaAlteracaoPor(
                usuarioContext.getUsuarioAtual()
        );

        produtoRepository.save(produto);

        auditoriaService.registrar(
                new AuditoriaRequest(
                        TipoAuditoria.ALTERACAO,
                        "produto",
                        produto.getId().toString(),
                        "Produto '" +
                                produto.getNome() +
                                "' foi desativado."
                )
        );
    }

    /*
     * ============================================================
     * MÉTODOS INTERNOS
     * ============================================================
     */

    private String converterParaJson(
            Object objeto
    ) {

        try {

            return objectMapper.writeValueAsString(
                    objeto
            );

        } catch (JsonProcessingException e) {

            throw new RegraNegocioException(
                    "Não foi possível registrar os detalhes da auditoria."
            );
        }
    }

    private String normalizarNome(
            String nome
    ) {

        if (nome == null || nome.isBlank()) {
            return null;
        }

        return nome.trim();
    }

    private void validarFiltros(
            BigDecimal precoMin,
            BigDecimal precoMax,
            Integer quantidadeMin,
            Integer quantidadeMax
    ) {

        if (precoMin != null && precoMin.signum() < 0) {

            throw new RegraNegocioException(
                    "O preço mínimo não pode ser negativo."
            );
        }

        if (precoMax != null && precoMax.signum() < 0) {

            throw new RegraNegocioException(
                    "O preço máximo não pode ser negativo."
            );
        }

        if (precoMin != null
                && precoMax != null
                && precoMin.compareTo(precoMax) > 0) {

            throw new RegraNegocioException(
                    "O preço mínimo não pode ser maior que o preço máximo."
            );
        }

        if (quantidadeMin != null && quantidadeMin < 0) {

            throw new RegraNegocioException(
                    "A quantidade mínima não pode ser negativa."
            );
        }

        if (quantidadeMax != null && quantidadeMax < 0) {

            throw new RegraNegocioException(
                    "A quantidade máxima não pode ser negativa."
            );
        }

        if (quantidadeMin != null
                && quantidadeMax != null
                && quantidadeMin > quantidadeMax) {

            throw new RegraNegocioException(
                    "A quantidade mínima não pode ser maior que a quantidade máxima."
            );
        }
    }

    private Empresa buscarEmpresa(
            Long empresaId
    ) {

        return empresaRepository
                .findById(empresaId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Empresa não encontrada."
                        )
                );
    }

    private CategoriaProduto buscarCategoriaDaEmpresa(
            Long categoriaId,
            Long empresaId
    ) {

        return categoriaRepository
                .findByIdAndEmpresaId(
                        categoriaId,
                        empresaId
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Categoria não encontrada ou não pertence à empresa."
                        )
                );
    }

    private Produto buscarProdutoDaEmpresa(
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

    private Estoque buscarEstoqueDoProduto(
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

    private void validarNomeDuplicado(
            String nome,
            Long empresaId
    ) {

        if (produtoRepository.existsByNomeAndEmpresaId(
                nome,
                empresaId
        )) {

            throw new RegraNegocioException(
                    "Já existe um produto com este nome."
            );
        }
    }

    private void validarNomeNaAtualizacao(
            String nome,
            Long produtoId,
            Long empresaId
    ) {

        produtoRepository
                .findByNomeAndEmpresaId(
                        nome,
                        empresaId
                )
                .ifPresent(produto -> {

                    if (!produto.getId().equals(produtoId)) {

                        throw new RegraNegocioException(
                                "Já existe outro produto com este nome."
                        );
                    }
                });
    }

    private void validarPrecos(
            BigDecimal precoCompra,
            BigDecimal precoVenda
    ) {

        if (precoCompra.signum() < 0) {

            throw new RegraNegocioException(
                    "O preço de compra não pode ser negativo."
            );
        }

        if (precoVenda.signum() < 0) {

            throw new RegraNegocioException(
                    "O preço de venda não pode ser negativo."
            );
        }

        if (precoVenda.compareTo(precoCompra) < 0) {

            throw new RegraNegocioException(
                    "O preço de venda não pode ser inferior ao preço de compra."
            );
        }
    }

    private Estoque criarEstoqueInicial(
            Produto produto,
            Empresa empresa
    ) {

        Estoque estoque = new Estoque();

        estoque.setProduto(produto);
        estoque.setEmpresa(empresa);
        estoque.setQuantidade(0);

        return estoqueRepository.save(estoque);
    }

    /*
     * ============================================================
     * CONVERSÃO INDIVIDUAL
     * ============================================================
     */

    private ProdutoResponse converterParaResponse(
            Produto produto,
            Estoque estoque
    ) {

        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getPrecoCompra(),
                produto.getPrecoVenda(),
                estoque.getQuantidade(),
                produto.getCategoria().getId(),
                produto.getCategoria().getNome(),
                produto.getEmpresa().getId(),
                produto.getStatus()
        );
    }

    /*
     * ============================================================
     * CONVERSÃO EM LOTE
     *
     * Evita:
     *
     * 1 query para produtos
     * +
     * N queries para estoques
     *
     * Agora:
     *
     * 1 query para produtos
     * +
     * 1 query para estoques
     * ============================================================
     */

    private Page<ProdutoResponse> converterPaginaParaResponse(
            Page<Produto> produtos,
            Long empresaId
    ) {

        if (produtos.isEmpty()) {
            return produtos.map(
                    produto -> null
            );
        }

        List<Long> produtoIds =
                produtos.getContent()
                        .stream()
                        .map(Produto::getId)
                        .toList();

        List<Estoque> estoques =
                estoqueRepository
                        .findByProdutoIdInAndEmpresaId(
                                produtoIds,
                                empresaId
                        );

        Map<Long, Estoque> estoquePorProduto =
                estoques.stream()
                        .collect(Collectors.toMap(
                                estoque ->
                                        estoque.getProduto().getId(),
                                Function.identity()
                        ));

        return produtos.map(produto -> {

            Estoque estoque =
                    estoquePorProduto.get(
                            produto.getId()
                    );

            if (estoque == null) {

                throw new RecursoNaoEncontradoException(
                        "Estoque do produto não encontrado."
                );
            }

            return converterParaResponse(
                    produto,
                    estoque
            );
        });
    }
}

