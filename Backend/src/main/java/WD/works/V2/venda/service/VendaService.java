package WD.works.V2.venda.service;

import WD.works.V2.auditoria.dto.AuditoriaRequest;
import WD.works.V2.auditoria.service.AuditoriaService;
import WD.works.V2.auditoria.tipo.TipoAuditoria;
import WD.works.V2.configuracao.context.EmpresaContext;
import WD.works.V2.configuracao.context.UsuarioContext;
import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.estoque.entity.Estoque;
import WD.works.V2.estoque.repository.EstoqueRepository;
import WD.works.V2.exception.RecursoNaoEncontradoException;
import WD.works.V2.exception.RegraNegocioException;
import WD.works.V2.movimentoStock.acoes.Acao;
import WD.works.V2.movimentoStock.entity.MovimentoStock;
import WD.works.V2.movimentoStock.repository.MovimentoStockRepository;
import WD.works.V2.produtos.entity.Produto;
import WD.works.V2.produtos.repository.ProdutoRepository;
import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.venda.dto.VendaRequest;
import WD.works.V2.venda.dto.VendaResponse;
import WD.works.V2.venda.entity.Venda;
import WD.works.V2.venda.itensVenda.dto.ItemVendaRequest;
import WD.works.V2.venda.itensVenda.dto.ItemVendaResponse;
import WD.works.V2.venda.itensVenda.entity.ItemVenda;
import WD.works.V2.venda.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;
    private final MovimentoStockRepository movimentoRepository;

    private final UsuarioContext usuarioContext;
    private final EmpresaContext empresaContext;

    private final AuditoriaService auditoriaService;


    /**
     * Cria uma nova venda para a empresa do usuário autenticado.
     */
    @Transactional
    public VendaResponse criar(
            VendaRequest request
    ) {

        validarItens(request);

        /*
         * Usuário autenticado.
         */
        Usuario usuario =
                usuarioContext.getUsuarioAtual();

        /*
         * Empresa associada ao usuário autenticado.
         */
        Empresa empresa =
                empresaContext.getEmpresaAtual();


        /*
         * Verificamos se os produtos e quantidades
         * são válidos antes de alterar qualquer estoque.
         */
        validarProdutosEStock(
                request,
                empresa.getId()
        );


        Venda venda = new Venda();

        venda.setEmpresa(empresa);
        venda.setUsuario(usuario);
        venda.setDataVenda(LocalDateTime.now());
        venda.setTotal(BigDecimal.ZERO);
        venda.setLucroTotal(BigDecimal.ZERO);
        venda.setItens(new ArrayList<>());


        BigDecimal total =
                BigDecimal.ZERO;

        BigDecimal lucroTotal =
                BigDecimal.ZERO;


        /*
         * ========================================================
         * PROCESSAMENTO DA VENDA
         * ========================================================
         */

        for (ItemVendaRequest itemRequest :
                request.getItens()) {

            Produto produto =
                    buscarProduto(
                            itemRequest.getProdutoId(),
                            empresa.getId()
                    );

            Estoque estoque =
                    buscarEstoque(
                            produto.getId(),
                            empresa.getId()
                    );

            int quantidade =
                    itemRequest.getQuantidade();


            BigDecimal precoVenda =
                    produto.getPrecoVenda();

            BigDecimal precoCompra =
                    produto.getPrecoCompra();


            /*
             * Valor total deste item.
             */
            BigDecimal subtotal =
                    precoVenda.multiply(
                            BigDecimal.valueOf(quantidade)
                    );


            /*
             * Lucro deste item.
             */
            BigDecimal lucro =
                    precoVenda
                            .subtract(precoCompra)
                            .multiply(
                                    BigDecimal.valueOf(quantidade)
                            );


            /*
             * ====================================================
             * ITEM DA VENDA
             * ====================================================
             */

            ItemVenda item =
                    new ItemVenda();

            item.setVenda(venda);
            item.setProduto(produto);
            item.setQuantidade(quantidade);
            item.setPrecoUnitario(precoVenda);
            item.setSubtotal(subtotal);
            item.setLucro(lucro);

            venda.getItens().add(item);


            total =
                    total.add(subtotal);

            lucroTotal =
                    lucroTotal.add(lucro);


            /*
             * ====================================================
             * ESTOQUE
             * ====================================================
             */

            int quantidadeAnterior =
                    estoque.getQuantidade();

            int quantidadePosterior =
                    quantidadeAnterior - quantidade;


            estoque.setQuantidade(
                    quantidadePosterior
            );

            estoqueRepository.save(
                    estoque
            );


            /*
             * ====================================================
             * MOVIMENTO DE STOCK
             * ====================================================
             */

            MovimentoStock movimento =
                    new MovimentoStock();

            movimento.setAcao(
                    Acao.SAIDA
            );

            movimento.setQuantidade(
                    quantidade
            );

            movimento.setQuantidadeAnterior(
                    quantidadeAnterior
            );

            movimento.setQuantidadePosterior(
                    quantidadePosterior
            );

            movimento.setDescricao(
                    "Saída referente à venda"
            );

            movimento.setData(
                    LocalDateTime.now()
            );

            movimento.setEstoque(
                    estoque
            );

            movimento.setProduto(
                    produto
            );

            movimento.setEmpresa(
                    empresa
            );

            movimento.setUsuario(
                    usuario
            );

            movimentoRepository.save(
                    movimento
            );
        }


        /*
         * ========================================================
         * FINALIZAR VENDA
         * ========================================================
         */

        venda.setTotal(total);
        venda.setLucroTotal(lucroTotal);


        Venda vendaSalva =
                vendaRepository.save(venda);


        /*
         * ========================================================
         * AUDITORIA
         * ========================================================
         */

        AuditoriaRequest auditoria =
                new AuditoriaRequest();

        auditoria.setTipo(
                TipoAuditoria.VENDA
        );

        auditoria.setTabela(
                "Venda"
        );

        auditoria.setRegisto(
                vendaSalva.getId().toString()
        );

        auditoria.setDescricao(
                "Venda realizada no valor de "
                        + vendaSalva.getTotal()
                        + " MT."
        );

        auditoriaService.registrar(
                auditoria
        );


        return converterParaResponse(
                vendaSalva
        );
    }


    /**
     * Busca uma venda pertencente à empresa do usuário autenticado.
     */
    @Transactional(readOnly = true)
    public VendaResponse buscarPorId(
            Long id
    ) {

        Long empresaId =
                empresaContext.getEmpresaIdAtual();

        Venda venda =
                vendaRepository
                        .findByIdAndEmpresaId(
                                id,
                                empresaId
                        )
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Venda não encontrada."
                                )
                        );

        return converterParaResponse(
                venda
        );
    }

    /**
     * Lista/pesquisa vendas da empresa do usuário autenticado.
     * <p>
     * Todos os filtros são opcionais.
     */
    @Transactional(readOnly = true)
    public Page<VendaResponse> listarPorEmpresa(
            Long vendaId,
            LocalDateTime inicio,
            LocalDateTime fim,
            Long usuarioId,
            Pageable pageable
    ) {

        Long empresaId =
                empresaContext.getEmpresaIdAtual();

        /*
         * Validação do período.
         */
        if (inicio != null
                && fim != null
                && inicio.isAfter(fim)) {

            throw new RegraNegocioException(
                    "A data/hora inicial não pode ser maior que a data/hora final."
            );
        }

        return vendaRepository
                .pesquisar(
                        empresaId,
                        vendaId,
                        inicio,
                        fim,
                        usuarioId,
                        pageable
                )
                .map(this::converterParaResponse);
    }




    /*
     * ============================================================
     * VALIDAÇÕES
     * ============================================================
     */

    private void validarItens(
            VendaRequest request
    ) {

        if (request == null) {

            throw new RegraNegocioException(
                    "Os dados da venda são obrigatórios."
            );
        }

        if (request.getItens() == null
                || request.getItens().isEmpty()) {

            throw new RegraNegocioException(
                    "A venda deve possuir pelo menos um item."
            );
        }


        /*
         * Impede o mesmo produto aparecer duas vezes
         * na mesma venda.
         */
        Set<Long> produtos =
                new HashSet<>();

        for (ItemVendaRequest item :
                request.getItens()) {

            if (item.getProdutoId() == null) {

                throw new RegraNegocioException(
                        "O produto é obrigatório."
                );
            }

            validarQuantidade(
                    item.getQuantidade()
            );

            if (!produtos.add(
                    item.getProdutoId()
            )) {

                throw new RegraNegocioException(
                        "O produto "
                                + item.getProdutoId()
                                + " foi adicionado mais de uma vez."
                );
            }
        }
    }


    /**
     * Valida todos os produtos e respectivos estoques
     * antes de modificar qualquer informação.
     */
    private void validarProdutosEStock(
            VendaRequest request,
            Long empresaId
    ) {

        for (ItemVendaRequest item :
                request.getItens()) {

            Produto produto =
                    buscarProduto(
                            item.getProdutoId(),
                            empresaId
                    );

            Estoque estoque =
                    buscarEstoque(
                            produto.getId(),
                            empresaId
                    );


            if (produto.getPrecoVenda() == null
                    || produto.getPrecoCompra() == null) {

                throw new RegraNegocioException(
                        "O produto "
                                + produto.getNome()
                                + " não possui preços configurados."
                );
            }


            if (estoque.getQuantidade()
                    < item.getQuantidade()) {

                throw new RegraNegocioException(
                        "Stock insuficiente para o produto: "
                                + produto.getNome()
                                + ". Disponível: "
                                + estoque.getQuantidade()
                );
            }
        }
    }


    private void validarQuantidade(
            Integer quantidade
    ) {

        if (quantidade == null
                || quantidade <= 0) {

            throw new RegraNegocioException("A quantidade deve ser maior que zero.");
        }
    }


    /*
     * ============================================================
     * BUSCAS
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
                                "Estoque não encontrado."
                        )
                );
    }


    /*
     * ============================================================
     * CONVERSÃO
     * ============================================================
     */

    private VendaResponse converterParaResponse(
            Venda venda
    ) {

        List<ItemVendaResponse> itens =
                venda.getItens()
                        .stream()
                        .map(item ->
                                new ItemVendaResponse(
                                        item.getId(),
                                        item.getProduto().getId(),
                                        item.getProduto().getNome(),
                                        item.getQuantidade(),
                                        item.getPrecoUnitario(),
                                        item.getSubtotal(),
                                        item.getLucro()
                                )
                        )
                        .toList();


        return new VendaResponse(
                venda.getId(),
                venda.getDataVenda(),
                venda.getTotal(),
                venda.getLucroTotal(),
                venda.getUsuario().getId(),
                venda.getUsuario().getNome(),
                venda.getEmpresa().getId(),
                itens
        );
    }
}