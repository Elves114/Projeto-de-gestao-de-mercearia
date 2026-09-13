package WD.works.V2.alertaStock.service;

import WD.works.V2.alertaStock.dto.AlertaStockResponse;
import WD.works.V2.alertaStock.entity.AlertaStock;
import WD.works.V2.alertaStock.repository.AlertaStockRepository;
import WD.works.V2.configuracao.context.UsuarioContext;
import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.estoque.entity.Estoque;
import WD.works.V2.exception.RecursoNaoEncontradoException;
import WD.works.V2.exception.RegraNegocioException;
import WD.works.V2.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AlertaStockService {

    private final AlertaStockRepository alertaStockRepository;
    private final UsuarioContext usuarioContext;

    /*
     * ============================================================
     * VERIFICAR ESTADO DO STOCK
     * ============================================================
     *
     * Este é o método que será chamado pelo
     * MovimentoStockService depois de alterar o estoque.
     *
     * Regra:
     *
     * quantidade <= quantidadeMinima
     *              ↓
     *         STOCK BAIXO
     *
     * Caso exista um alerta ativo:
     *      não cria outro.
     *
     * Caso não exista:
     *      cria um novo alerta.
     *
     * Caso o stock esteja normal:
     *      resolve o alerta ativo, se existir.
     */

    @Transactional
    public void verificarEstoque(
            Estoque estoque
    ) {

        if (estoque == null) {
            throw new RegraNegocioException(
                    "O estoque não pode ser nulo."
            );
        }

        if (estoque.getEmpresa() == null) {
            throw new RegraNegocioException(
                    "O estoque não está associado a uma empresa."
            );
        }

        boolean stockBaixo =
                estoque.getQuantidade()
                        <= estoque.getQuantidadeMinima();

        AlertaStock alertaAtivo =
                alertaStockRepository
                        .findByEstoqueIdAndAtivoTrue(
                                estoque.getId()
                        )
                        .orElse(null);

        if (stockBaixo) {

            /*
             * Stock baixo.
             *
             * Se já existe alerta ativo,
             * não fazemos nada.
             */
            if (alertaAtivo != null) {
                return;
            }

            criarAlerta(estoque);

            return;
        }

        /*
         * Stock voltou ao normal.
         *
         * Se existia um alerta ativo,
         * resolvemos o alerta.
         */
        if (alertaAtivo != null) {

            alertaAtivo.setAtivo(false);

            alertaAtivo.setResolvidoEm(
                    LocalDateTime.now()
            );

            alertaStockRepository.save(
                    alertaAtivo
            );
        }
    }

    /*
     * ============================================================
     * CRIAR ALERTA
     * ============================================================
     */

    private void criarAlerta(
            Estoque estoque
    ) {

        AlertaStock alerta =
                new AlertaStock();

        alerta.setAtivo(true);

        alerta.setCriadoEm(
                LocalDateTime.now()
        );

        alerta.setEstoque(
                estoque
        );

        alerta.setEmpresa(
                estoque.getEmpresa()
        );

        alertaStockRepository.save(
                alerta
        );
    }

    /*
     * ============================================================
     * LISTAR TODOS OS ALERTAS
     * ============================================================
     */

    @Transactional(readOnly = true)
    public Page<AlertaStockResponse> listar(
            Pageable pageable
    ) {

        Empresa empresa =
                obterEmpresaDoUsuario();

        return alertaStockRepository
                .findByEmpresaIdOrderByCriadoEmDesc(
                        empresa.getId(),
                        pageable
                )
                .map(this::converterParaResponse);
    }

    /*
     * ============================================================
     * LISTAR APENAS ALERTAS ATIVOS
     * ============================================================
     */

    @Transactional(readOnly = true)
    public Page<AlertaStockResponse> listarAtivos(
            Pageable pageable
    ) {

        Empresa empresa =
                obterEmpresaDoUsuario();

        return alertaStockRepository
                .findByEmpresaIdAndAtivoTrueOrderByCriadoEmDesc(
                        empresa.getId(),
                        pageable
                )
                .map(this::converterParaResponse);
    }

    /*
     * ============================================================
     * BUSCAR ALERTA POR ID
     * ============================================================
     */

    @Transactional(readOnly = true)
    public AlertaStockResponse buscarPorId(
            Long id
    ) {

        Empresa empresa =
                obterEmpresaDoUsuario();

        AlertaStock alerta =
                alertaStockRepository
                        .findByIdAndEmpresaId(
                                id,
                                empresa.getId()
                        )
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Alerta de stock não encontrado."
                                )
                        );

        return converterParaResponse(
                alerta
        );
    }

    /*
     * ============================================================
     * MÉTODOS INTERNOS
     * ============================================================
     */

    private Empresa obterEmpresaDoUsuario() {

        Usuario usuario =
                usuarioContext.getUsuarioAtual();

        if (usuario == null) {

            throw new RecursoNaoEncontradoException(
                    "Usuário autenticado não encontrado."
            );
        }

        if (usuario.getEmpresa() == null) {

            throw new RegraNegocioException(
                    "O usuário não está associado a uma empresa."
            );
        }

        return usuario.getEmpresa();
    }

    private AlertaStockResponse converterParaResponse(
            AlertaStock alerta
    ) {

        Estoque estoque =
                alerta.getEstoque();

        return new AlertaStockResponse(
                alerta.getId(),
                alerta.isAtivo(),
                alerta.getCriadoEm(),
                alerta.getResolvidoEm(),

                estoque.getId(),

                estoque.getProduto().getId(),

                estoque.getProduto().getNome(),

                estoque.getQuantidade(),

                estoque.getQuantidadeMinima(),

                alerta.getEmpresa().getId()
        );
    }
}