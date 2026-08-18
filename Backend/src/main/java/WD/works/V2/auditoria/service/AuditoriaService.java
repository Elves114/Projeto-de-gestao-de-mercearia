package WD.works.V2.auditoria.service;

import WD.works.V2.auditoria.dto.AuditoriaRequest;
import WD.works.V2.auditoria.dto.AuditoriaResponse;
import WD.works.V2.auditoria.entity.Auditoria;
import WD.works.V2.auditoria.repository.AuditoriaRepository;
import WD.works.V2.auditoria.tipo.TipoAuditoria;
import WD.works.V2.configuracao.context.EmpresaContext;
import WD.works.V2.configuracao.context.UsuarioContext;
import WD.works.V2.configuracao.security.AutorizacaoService;
import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.exception.RecursoNaoEncontradoException;
import WD.works.V2.exception.RegraNegocioException;
import WD.works.V2.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    private final UsuarioContext usuarioContext;
    private final EmpresaContext empresaContext;
    private final AutorizacaoService autorizacaoService;


    /**
     * Registra uma nova auditoria.
     *
     * O usuário e a empresa são obtidos
     * automaticamente através do contexto
     * de autenticação.
     */
    @Transactional
    public AuditoriaResponse registrar(
            AuditoriaRequest request
    ) {

        validarRequest(request);

        Usuario usuario =
                usuarioContext.getUsuarioAtual();

        Empresa empresa =
                empresaContext.getEmpresaAtual();


        Auditoria auditoria =
                new Auditoria();

        auditoria.setTipo(
                request.getTipo()
        );

        auditoria.setTabela(
                request.getTabela()
        );

        auditoria.setRegisto(
                request.getRegisto()
        );

        auditoria.setDescricao(
                request.getDescricao()
        );

        auditoria.setEmpresa(
                empresa
        );

        auditoria.setUsuario(
                usuario
        );


        Auditoria salva =
                auditoriaRepository.save(
                        auditoria
                );


        return converterParaResponse(
                salva
        );
    }

    @Transactional
    public AuditoriaResponse registrar(
            AuditoriaRequest request,
            Usuario usuario,
            Empresa empresa
    ) {

        validarRequest(request);

        if (usuario == null) {
            throw new RegraNegocioException(
                    "O usuário responsável pela auditoria é obrigatório."
            );
        }

        if (empresa == null) {
            throw new RegraNegocioException(
                    "A empresa da auditoria é obrigatória."
            );
        }

        Auditoria auditoria = new Auditoria();

        auditoria.setTipo(
                request.getTipo()
        );

        auditoria.setTabela(
                request.getTabela()
        );

        auditoria.setRegisto(
                request.getRegisto()
        );

        auditoria.setDescricao(
                request.getDescricao()
        );

        auditoria.setEmpresa(
                empresa
        );

        auditoria.setUsuario(
                usuario
        );

        Auditoria salva =
                auditoriaRepository.save(
                        auditoria
                );

        return converterParaResponse(
                salva
        );
    }
    /**
     * Retorna todas as auditorias da empresa.
     *
     * Somente ADMIN e GERENTE podem utilizar
     * esta operação.
     */
    @Transactional(readOnly = true)
    public Page<AuditoriaResponse> listarPorEmpresa(
            Pageable pageable
    ) {

        Usuario usuario =
                usuarioContext.getUsuarioAtual();

        if (!autorizacaoService
                .podeVisualizarTodasAuditorias(usuario)) {

            throw new SecurityException(
                    "Não possui permissão para visualizar "
                            + "todas as auditorias da empresa."
            );
        }

        Long empresaId =
                empresaContext.getEmpresaIdAtual();

        return auditoriaRepository
                .findByEmpresaIdOrderByDataDesc(
                        empresaId,
                        pageable
                )
                .map(this::converterParaResponse);
    }


    /**
     * Retorna as próprias auditorias
     * do usuário autenticado.
     *
     * Qualquer usuário autenticado pode utilizar.
     */
    @Transactional(readOnly = true)
    public Page<AuditoriaResponse> minhasAuditorias(
            Pageable pageable
    ) {

        Usuario usuario =
                usuarioContext.getUsuarioAtual();

        Long empresaId =
                empresaContext.getEmpresaIdAtual();

        return auditoriaRepository
                .findByEmpresaIdAndUsuarioIdOrderByDataDesc(
                        empresaId,
                        usuario.getId(),
                        pageable
                )
                .map(this::converterParaResponse);
    }


    /**
     * ADMIN e GERENTE podem consultar
     * as auditorias de um usuário específico.
     *
     * FUNCIONÁRIO não pode utilizar este método
     * para consultar outro usuário.
     */
    @Transactional(readOnly = true)
    public Page<AuditoriaResponse> listarPorUsuario(
            Long usuarioId,
            Pageable pageable
    ) {

        Usuario usuarioAtual =
                usuarioContext.getUsuarioAtual();

        Long empresaId =
                empresaContext.getEmpresaIdAtual();

        /*
         * ADMIN e GERENTE
         */
        if (autorizacaoService
                .podeVisualizarTodasAuditorias(usuarioAtual)) {

            if (usuarioId == null) {

                throw new RegraNegocioException(
                        "O ID do usuário é obrigatório."
                );
            }

            return auditoriaRepository
                    .findByEmpresaIdAndUsuarioIdOrderByDataDesc(
                            empresaId,
                            usuarioId,
                            pageable
                    )
                    .map(this::converterParaResponse);
        }

        /*
         * FUNCIONÁRIO
         *
         * Ignora completamente o usuarioId
         * enviado na requisição.
         */
        return auditoriaRepository
                .findByEmpresaIdAndUsuarioIdOrderByDataDesc(
                        empresaId,
                        usuarioAtual.getId(),
                        pageable
                )
                .map(this::converterParaResponse);
    }


    /**
     * Lista auditorias por tipo.
     *
     * Somente ADMIN e GERENTE podem consultar
     * o histórico completo por tipo.
     */
    @Transactional(readOnly = true)
    public Page<AuditoriaResponse> listarPorTipo(
            TipoAuditoria tipo,
            Pageable pageable
    ) {

        Usuario usuario =
                usuarioContext.getUsuarioAtual();

        if (!autorizacaoService
                .podeVisualizarTodasAuditorias(usuario)) {

            throw new SecurityException(
                    "Não possui permissão para consultar "
                            + "auditorias por tipo."
            );
        }

        if (tipo == null) {

            throw new RegraNegocioException(
                    "O tipo da auditoria é obrigatório."
            );
        }

        Long empresaId =
                empresaContext.getEmpresaIdAtual();

        return auditoriaRepository
                .findByEmpresaIdAndTipoOrderByDataDesc(
                        empresaId,
                        tipo,
                        pageable
                )
                .map(this::converterParaResponse);
    }

    /**
     * Valida os dados da auditoria.
     */
    private void validarRequest(
            AuditoriaRequest request
    ) {

        if (request == null) {

            throw new RegraNegocioException(
                    "Os dados da auditoria são obrigatórios."
            );
        }


        if (request.getTipo() == null) {

            throw new RegraNegocioException(
                    "O tipo da auditoria é obrigatório."
            );
        }


        if (request.getTabela() == null
                || request.getTabela().isBlank()) {

            throw new RegraNegocioException(
                    "A tabela afetada é obrigatória."
            );
        }


        if (request.getRegisto() == null
                || request.getRegisto().isBlank()) {

            throw new RecursoNaoEncontradoException(
                    "O registro afetado é obrigatório."
            );
        }


        if (request.getDescricao() == null
                || request.getDescricao().isBlank()) {

            throw new RegraNegocioException(
                    "A descrição da auditoria é obrigatória."
            );
        }
    }


    /**
     * Converte a entidade para o DTO de resposta.
     */
    private AuditoriaResponse converterParaResponse(
            Auditoria auditoria
    ) {

        Usuario usuario =
                auditoria.getUsuario();


        return new AuditoriaResponse(
                auditoria.getId(),
                auditoria.getTipo(),
                auditoria.getTabela(),
                auditoria.getRegisto(),
                auditoria.getDescricao(),
                auditoria.getData(),
                auditoria.getEmpresa().getId(),
                usuario.getId(),
                usuario.getNome()
        );
    }
}