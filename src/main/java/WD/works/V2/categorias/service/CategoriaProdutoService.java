package WD.works.V2.categorias.service;

import WD.works.V2.auditoria.dto.AuditoriaRequest;
import WD.works.V2.auditoria.service.AuditoriaService;
import WD.works.V2.auditoria.tipo.TipoAuditoria;
import WD.works.V2.categorias.entity.CategoriaProduto;
import WD.works.V2.categorias.dto.CategoriaProdutoRequest;
import WD.works.V2.categorias.dto.CategoriaProdutoResponse;
import WD.works.V2.categorias.repository.CategoriaProdutoRepository;
import WD.works.V2.configuracao.context.EmpresaContext;
import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.empresa.repository.EmpresaRepository;
import WD.works.V2.exception.RecursoNaoEncontradoException;
import WD.works.V2.exception.RegraNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoriaProdutoService {

    private final CategoriaProdutoRepository categoriaRepository;
    private final EmpresaContext empresaContext;
    private final AuditoriaService auditoriaService;


    /*
     * ============================================================
     * Criar categoria
     * ============================================================
     */

    @Transactional
    public CategoriaProdutoResponse criar(
            CategoriaProdutoRequest request
    ) {

        Long empresaId =
                empresaContext.getEmpresaIdAtual();

        validarNomeDuplicado(
                request.getNome(),
                empresaId
        );

        Empresa empresa =
                empresaContext.getEmpresaAtual();

        CategoriaProduto categoria =
                new CategoriaProduto();

        categoria.setNome(request.getNome());
        categoria.setEmpresa(empresa);

        CategoriaProduto categoriaSalva =
                categoriaRepository.save(categoria);

        auditoriaService.registrar(
                new AuditoriaRequest(
                        TipoAuditoria.CRIACAO,
                        "categoria",
                        categoriaSalva.getId().toString(),
                        "Categoria '" +
                                categoriaSalva.getNome() +
                                "' foi criada."
                )
        );

        return converterParaResponse(categoriaSalva);
    }


    /*
     * ============================================================
     * Buscar categoria por ID
     * ============================================================
     */

    @Transactional(readOnly = true)
    public CategoriaProdutoResponse buscarPorId(
            Long id
    ) {

        Long empresaId =
                empresaContext.getEmpresaIdAtual();

        CategoriaProduto categoria =
                buscarCategoriaDaEmpresa(
                        id,
                        empresaId
                );

        return converterParaResponse(categoria);
    }


    /*
     * ============================================================
     * Listar categorias da empresa atual
     * ============================================================
     */

    @Transactional(readOnly = true)
    public Page<CategoriaProdutoResponse> listarPorEmpresa(
            String nome,
            Pageable pageable
    ) {

        Long empresaId =
                empresaContext.getEmpresaIdAtual();

        if (nome == null || nome.isBlank()) {

            return categoriaRepository
                    .findByEmpresaIdOrderByNomeAsc(
                            empresaId,
                            pageable
                    )
                    .map(this::converterParaResponse);
        }

        return categoriaRepository
                .findByEmpresaIdAndNomeContainingIgnoreCaseOrderByNomeAsc(
                        empresaId,
                        nome.trim(),
                        pageable
                )
                .map(this::converterParaResponse);
    }

    /*
     * ============================================================
     * Atualizar categoria
     * ============================================================
     */

    @Transactional
    public CategoriaProdutoResponse atualizar(
            Long id,
            CategoriaProdutoRequest request
    ) {

        Long empresaId =
                empresaContext.getEmpresaIdAtual();

        CategoriaProduto categoria =
                buscarCategoriaDaEmpresa(
                        id,
                        empresaId
                );

        validarNomeNaAtualizacao(
                request.getNome(),
                categoria.getId(),
                empresaId
        );

        categoria.setNome(
                request.getNome()
        );

        CategoriaProduto categoriaAtualizada =
                categoriaRepository.save(categoria);

        auditoriaService.registrar(
                new AuditoriaRequest(
                        TipoAuditoria.ALTERACAO,
                        "categoria",
                        categoriaAtualizada.getId().toString(),
                        "Categoria '" +
                                categoriaAtualizada.getNome() +
                                "' foi atualizada."
                )
        );

        return converterParaResponse(
                categoriaAtualizada
        );
    }


    /*
     * ============================================================
     * Eliminar categoria
     * ============================================================
     */

    @Transactional
    public void eliminar(
            Long id
    ) {

        Long empresaId =
                empresaContext.getEmpresaIdAtual();

        CategoriaProduto categoria =
                buscarCategoriaDaEmpresa(
                        id,
                        empresaId
                );

        /*
         * Registamos a auditoria ANTES do delete,
         * porque depois da entidade ser removida
         * deixamos de ter acesso garantido aos
         * seus dados.
         */
        auditoriaService.registrar(
                new AuditoriaRequest(
                        TipoAuditoria.EXCLUSAO,
                        "categoria",
                        categoria.getId().toString(),
                        "Categoria '" +
                                categoria.getNome() +
                                "' foi eliminada."
                )
        );

        categoriaRepository.delete(categoria);
    }


    /*
     * ============================================================
     * Métodos internos
     * ============================================================
     */

    private CategoriaProduto buscarCategoriaDaEmpresa(
            Long id,
            Long empresaId
    ) {

        return categoriaRepository
                .findByIdAndEmpresaId(
                        id,
                        empresaId
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Categoria não encontrada."
                        )
                );
    }


    private void validarNomeDuplicado(
            String nome,
            Long empresaId
    ) {

        if (categoriaRepository
                .existsByNomeAndEmpresaId(
                        nome,
                        empresaId
                )) {

            throw new RegraNegocioException(
                    "Já existe uma categoria com este nome."
            );
        }
    }


    private void validarNomeNaAtualizacao(
            String nome,
            Long categoriaId,
            Long empresaId
    ) {

        categoriaRepository
                .findByNomeAndEmpresaId(
                        nome,
                        empresaId
                )
                .ifPresent(categoria -> {

                    if (!categoria.getId()
                            .equals(categoriaId)) {

                        throw new RegraNegocioException(
                                "Já existe outra categoria com este nome."
                        );
                    }
                });
    }


    private CategoriaProdutoResponse converterParaResponse(
            CategoriaProduto categoria
    ) {

        return new CategoriaProdutoResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getEmpresa().getId()
        );
    }
}