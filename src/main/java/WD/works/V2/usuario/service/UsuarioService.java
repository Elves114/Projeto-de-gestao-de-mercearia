package WD.works.V2.usuario.service;

import WD.works.V2.configuracao.security.SecurityUtils;
import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.empresa.repository.EmpresaRepository;
import WD.works.V2.exception.RecursoNaoEncontradoException;
import WD.works.V2.exception.RegraNegocioException;
import WD.works.V2.usuario.dto.UsuarioAtualizacaoRequest;
import WD.works.V2.usuario.dto.UsuarioRequest;
import WD.works.V2.usuario.dto.UsuarioResponse;
import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.usuario.perfil.Perfil;
import WD.works.V2.usuario.repository.UsuarioRepository;
import WD.works.V2.usuario.status.Status;
import WD.works.V2.auditoria.dto.AuditoriaRequest;
import WD.works.V2.auditoria.service.AuditoriaService;
import WD.works.V2.auditoria.tipo.TipoAuditoria;
import WD.works.V2.exception.RegraNegocioException;
import WD.works.V2.usuario.dto.AlterarSenhaRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;
    private final AuditoriaService auditoriaService;


    /*
     * ============================================================
     * CRIAR USUÁRIO
     * ============================================================
     */

    @Transactional
    public UsuarioResponse criar(
            UsuarioRequest request
    ) {

        Long empresaId =
                securityUtils.getEmpresaId();

        validarEmail(request.getEmail());

        Empresa empresa =
                buscarEmpresa(empresaId);

        validarPerfilNaCriacao(
                request.getPerfil(),
                empresaId
        );

        Usuario usuario = new Usuario();

        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());

        usuario.setSenha(
                passwordEncoder.encode(
                        request.getSenha()
                )
        );

        usuario.setPerfil(request.getPerfil());
        usuario.setStatus(Status.ATIVO);
        usuario.setEmpresa(empresa);

        Usuario usuarioSalvo =
                usuarioRepository.save(usuario);
        auditoriaService.registrar(
                new AuditoriaRequest(
                        TipoAuditoria.CRIACAO,
                        "usuario",
                        usuarioSalvo.getId().toString(),
                        "Usuário '" + usuarioSalvo.getNome()
                                + "' criado com o perfil "
                                + usuarioSalvo.getPerfil() + "."
                )
        );

        return converterParaResponse(
                usuarioSalvo
        );
    }


    /*
     * ============================================================
     * CRIAR ADMINISTRADOR INICIAL
     * ============================================================
     *
     * Este método é diferente dos restantes porque é utilizado
     * durante a inicialização da empresa.
     *
     * Por isso ainda recebe empresaId.
     */

    @Transactional
    public UsuarioResponse criarAdminInicial(
            UsuarioRequest request,
            Long empresaId
    ) {

        Empresa empresa =
                buscarEmpresa(empresaId);

        if (request.getPerfil() != Perfil.ADMIN) {

            throw new
                    RegraNegocioException(
                    "O usuário inicial da empresa deve ser ADMIN."
            );
        }

        if (usuarioRepository.existsByEmpresaIdAndPerfil(
                empresaId,
                Perfil.ADMIN
        )) {

            throw new IllegalStateException(
                    "Esta empresa já possui um administrador."
            );
        }

        validarEmail(
                request.getEmail()
        );

        Usuario usuario = new Usuario();

        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());

        usuario.setSenha(
                passwordEncoder.encode(
                        request.getSenha()
                )
        );

        usuario.setPerfil(Perfil.ADMIN);
        usuario.setStatus(Status.ATIVO);
        usuario.setEmpresa(empresa);

        Usuario usuarioSalvo =
                usuarioRepository.save(usuario);

        return converterParaResponse(
                usuarioSalvo
        );
    }


    /*
     * ============================================================
     * BUSCAR POR ID
     * ============================================================
     */

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(
            Long id
    ) {

        Long empresaId =
                securityUtils.getEmpresaId();

        Usuario usuario =
                buscarUsuarioDaEmpresa(
                        id,
                        empresaId
                );

        return converterParaResponse(
                usuario
        );
    }


    /*
     * ============================================================
     * BUSCAR POR EMAIL
     * ============================================================
     */

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorEmail(
            String email
    ) {

        Long empresaId =
                securityUtils.getEmpresaId();

        Usuario usuario =
                usuarioRepository
                        .findByEmailAndEmpresaId(
                                email,
                                empresaId
                        )
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Usuário não encontrado."
                                )
                        );

        return converterParaResponse(
                usuario
        );
    }


    /*
     * ============================================================
     * LISTAR USUÁRIOS DA EMPRESA
     * ============================================================
     */

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listarPorEmpresa(
            Pageable pageable
    ) {

        Long empresaId =
                securityUtils.getEmpresaId();

        return usuarioRepository
                .findByEmpresaIdOrderByNomeAsc(
                        empresaId,
                        pageable
                )
                .map(this::converterParaResponse);
    }


    /*
     * ============================================================
     * ATUALIZAR USUÁRIO
     * ============================================================
     */

    @Transactional
    public UsuarioResponse atualizar(
                    Long id,
            UsuarioAtualizacaoRequest request

    ) {

        Long empresaId =
                securityUtils.getEmpresaId();

        Usuario usuario =
                buscarUsuarioDaEmpresa(
                        id,
                        empresaId
                );

        validarEmailNaAtualizacao(
                request.getEmail(),
                usuario.getId()
        );

        usuario.setNome(
                request.getNome()
        );

        usuario.setEmail(
                request.getEmail()
        );

        Usuario usuarioAtualizado =
                usuarioRepository.save(usuario);

        auditoriaService.registrar(
                new AuditoriaRequest(
                        TipoAuditoria.ALTERACAO,
                        "usuario",
                        usuarioAtualizado.getId().toString(),
                        "Usuário '" +
                                usuarioAtualizado.getNome() +
                                "' atualizado."
                )
        );

        return converterParaResponse(
                usuarioAtualizado
        );
    }


    /*
     * ============================================================
     * ALTERAR PERFIL
     * ============================================================
     */

    @Transactional
    public UsuarioResponse alterarPerfil(
            Long id,
            Perfil perfil
    ) {

        Long empresaId =
                securityUtils.getEmpresaId();

        Usuario usuario =
                buscarUsuarioDaEmpresa(
                        id,
                        empresaId
                );

        validarPerfilNaAlteracao(
                usuario,
                perfil,
                empresaId
        );

        Perfil perfilAnterior =
                usuario.getPerfil();

        usuario.setPerfil(perfil);

        Usuario usuarioAtualizado =
                usuarioRepository.save(usuario);

        auditoriaService.registrar(
                new AuditoriaRequest(
                        TipoAuditoria.ALTERACAO,
                        "usuario",
                        usuarioAtualizado.getId().toString(),
                        "Perfil do usuário '" +
                                usuarioAtualizado.getNome() +
                                "' alterado de " +
                                perfilAnterior +
                                " para " +
                                usuarioAtualizado.getPerfil() +
                                "."
                )
        );

        return converterParaResponse(
                usuarioAtualizado
        );
    }

    /*
     * ============================================================
     * ATIVAR USUÁRIO
     * ============================================================
     */
    @Transactional
    public UsuarioResponse ativar(
            Long id
    ) {

        Long empresaId =
                securityUtils.getEmpresaId();

        Usuario usuario =
                buscarUsuarioDaEmpresa(
                        id,
                        empresaId
                );

        if (usuario.getStatus() == Status.ATIVO) {

            throw new IllegalStateException(
                    "O usuário já está ativo."
            );
        }

        usuario.setStatus(
                Status.ATIVO
        );

        Usuario usuarioAtualizado =
                usuarioRepository.save(usuario);

        auditoriaService.registrar(
                new AuditoriaRequest(
                        TipoAuditoria.ALTERACAO,
                        "usuario",
                        usuarioAtualizado.getId().toString(),
                        "Usuário '" +
                                usuarioAtualizado.getNome() +
                                "' foi ativado."
                )
        );

        return converterParaResponse(
                usuarioAtualizado
        );
    }


    /*
     * ============================================================
     * DESATIVAR USUÁRIO
     * ============================================================
     */

    @Transactional
    public UsuarioResponse desativar(
            Long id
    ) {

        Long empresaId =
                securityUtils.getEmpresaId();

        Usuario usuario =
                buscarUsuarioDaEmpresa(
                        id,
                        empresaId
                );

        if (usuario.getStatus() == Status.INATIVO) {

            throw new IllegalStateException(
                    "O usuário já está inativo."
            );
        }

        usuario.setStatus(
                Status.INATIVO
        );

        Usuario usuarioAtualizado =
                usuarioRepository.save(usuario);

        auditoriaService.registrar(
                new AuditoriaRequest(
                        TipoAuditoria.ALTERACAO,
                        "usuario",
                        usuarioAtualizado.getId().toString(),
                        "Usuário '" +
                                usuarioAtualizado.getNome() +
                                "' foi desativado."
                )
        );

        return converterParaResponse(
                usuarioAtualizado
        );
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> pesquisar(String termo, Pageable pageable) {
        Long empresaId = securityUtils.getEmpresaId();
        return usuarioRepository
                .pesquisarPorEmpresa(empresaId, termo, pageable)
                .map(this::converterParaResponse);
    }

    /*
     * ============================================================
     * ALTERAR A MINHA SENHA
     * ============================================================
     *
     * Apenas para o próprio utilizador autenticado.
     *
     * Não recebe id — a identidade vem do contexto de
     * segurança. Isto impede que alguém tente alterar
     * a senha de outro utilizador.
     */

    @Transactional
    public void alterarMinhaSenha(
            AlterarSenhaRequest request
    ) {

        Usuario usuario =
                securityUtils.getUsuarioAutenticado();

        /*
         * 1) A senha atual tem de corresponder.
         */
        if (!passwordEncoder.matches(
                request.getSenhaAtual(),
                usuario.getSenha()
        )) {

            throw new RegraNegocioException(
                    "A senha atual está incorreta."
            );
        }

        /*
         * 2) A nova senha não pode ser igual à atual.
         *
         * Se o utilizador está a alterar a senha,
         * é porque quer mudá-la mesmo.
         */
        if (passwordEncoder.matches(
                request.getNovaSenha(),
                usuario.getSenha()
        )) {

            throw new RegraNegocioException(
                    "A nova senha não pode ser igual à senha atual."
            );
        }

        /*
         * 3) Gravar a nova senha com hash.
         */
        usuario.setSenha(
                passwordEncoder.encode(
                        request.getNovaSenha()
                )
        );

        usuarioRepository.save(usuario);

        /*
         * 4) Auditoria.
         *
         * A senha em si nunca é registada — apenas o
         * evento.
         */
        auditoriaService.registrar(
                new AuditoriaRequest(
                        TipoAuditoria.ALTERACAO,
                        "usuario",
                        usuario.getId().toString(),
                        "Senha alterada pelo próprio utilizador."
                )
        );
    }


    /*
     * ============================================================
     * MÉTODOS INTERNOS
     * ============================================================
     */

    private Usuario buscarUsuarioDaEmpresa(
            Long id,
            Long empresaId
    ) {

        return usuarioRepository
                .findByIdAndEmpresaId(
                        id,
                        empresaId
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado."
                        )
                );
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


    private void validarEmail(
            String email
    ) {

        if (usuarioRepository.existsByEmail(email)) {

            throw new RegraNegocioException(
                    "Já existe um usuário com este email."
            );
        }
    }


    private void validarEmailNaAtualizacao(
            String email,
            Long usuarioId
    ) {

        usuarioRepository
                .findByEmail(email)
                .ifPresent(usuario -> {

                    if (!usuario.getId().equals(usuarioId)) {

                        throw new RegraNegocioException(
                                "Já existe um usuário com este email."
                        );
                    }
                });
    }


    private UsuarioResponse converterParaResponse(
            Usuario usuario
    ) {

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil(),
                usuario.getStatus(),
                usuario.getEmpresa().getId()
        );
    }


    private void validarPerfilNaCriacao(
            Perfil perfil,
            Long empresaId
    ) {

        if (perfil == Perfil.ADMIN &&
                usuarioRepository
                        .existsByEmpresaIdAndPerfil(
                                empresaId,
                                Perfil.ADMIN
                        )) {

            throw new IllegalStateException(
                    "Esta empresa já possui um administrador."
            );
        }
    }


    private void validarPerfilNaAlteracao(
            Usuario usuario,
            Perfil novoPerfil,
            Long empresaId
    ) {

        if (novoPerfil == Perfil.ADMIN &&
                usuario.getPerfil() != Perfil.ADMIN &&
                usuarioRepository
                        .existsByEmpresaIdAndPerfil(
                                empresaId,
                                Perfil.ADMIN
                        )) {

            throw new IllegalStateException(
                    "Esta empresa já possui um administrador."
            );
        }
    }
}