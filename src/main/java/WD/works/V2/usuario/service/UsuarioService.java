package WD.works.V2.usuario.service;

import WD.works.V2.configuracao.security.SecurityUtils;
import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.empresa.repository.EmpresaRepository;
import WD.works.V2.exception.RecursoNaoEncontradoException;
import WD.works.V2.usuario.dto.UsuarioRequest;
import WD.works.V2.usuario.dto.UsuarioResponse;
import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.usuario.repository.UsuarioRepository;
import WD.works.V2.usuario.perfil.Perfil;
import WD.works.V2.usuario.status.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;

    @Transactional
    public UsuarioResponse criar(
            UsuarioRequest request,
            Long empresaId
    ) {
        validarEmpresaDoUsuario(empresaId);

        validarEmail(request.getEmail());

        Empresa empresa = buscarEmpresa(empresaId);

        validarPerfilNaCriacao(
                request.getPerfil(),
                empresaId
        );

        Usuario usuario = new Usuario();

        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());

        usuario.setSenha(
                passwordEncoder.encode(request.getSenha())
        );

        usuario.setPerfil(request.getPerfil());
        usuario.setStatus(Status.ATIVO);
        usuario.setEmpresa(empresa);

        Usuario usuarioSalvo =
                usuarioRepository.save(usuario);

        return converterParaResponse(usuarioSalvo);
    }

    @Transactional
    public UsuarioResponse criarAdminInicial(
            UsuarioRequest request,
            Long empresaId
    ) {

        Empresa empresa = buscarEmpresa(empresaId);

        // O endpoint inicial só pode criar ADMIN
        if (request.getPerfil() != Perfil.ADMIN) {
            throw new IllegalArgumentException(
                    "O usuário inicial da empresa deve ser ADMIN."
            );
        }

        // Uma empresa só pode ter um ADMIN
        if (usuarioRepository.existsByEmpresaIdAndPerfil(
                empresaId,
                Perfil.ADMIN
        )) {
            throw new IllegalStateException(
                    "Esta empresa já possui um administrador."
            );
        }

        validarEmail(request.getEmail());

        Usuario usuario = new Usuario();

        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());

        usuario.setSenha(
                passwordEncoder.encode(request.getSenha())
        );

        usuario.setPerfil(Perfil.ADMIN);
        usuario.setStatus(Status.ATIVO);
        usuario.setEmpresa(empresa);

        Usuario usuarioSalvo =
                usuarioRepository.save(usuario);

        return converterParaResponse(usuarioSalvo);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(
            Long id,
            Long empresaId
    ) {

        validarEmpresaDoUsuario(empresaId);

        Usuario usuario =
                buscarUsuarioDaEmpresa(
                        id,
                        empresaId
                );

        return converterParaResponse(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorEmail(
            String email,
            Long empresaId
    ) {

        validarEmpresaDoUsuario(empresaId);

        Usuario usuario =
                usuarioRepository
                        .findByEmailAndEmpresaId(
                                email,
                                empresaId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Usuário não encontrado."
                                )
                        );

        return converterParaResponse(usuario);
    }
    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listarPorEmpresa(
            Long empresaId,
            Pageable pageable
    ) {

        validarEmpresaDoUsuario(empresaId);

        return usuarioRepository
                .findByEmpresaIdOrderByNomeAsc(
                        empresaId,
                        pageable
                )
                .map(this::converterParaResponse);
    }

    @Transactional
    public UsuarioResponse atualizar(
            Long id,
            UsuarioRequest request,
            Long empresaId
    ) {

        validarEmpresaDoUsuario(empresaId);

        Usuario usuario =
                buscarUsuarioDaEmpresa(
                        id,
                        empresaId
                );

        validarEmailNaAtualizacao(
                request.getEmail(),
                usuario.getId()
        );

        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());

        Usuario usuarioAtualizado =
                usuarioRepository.save(usuario);

        return converterParaResponse(usuarioAtualizado);
    }

    public UsuarioResponse alterarPerfil(
            Long id,
            Perfil perfil,
            Long empresaId
    ) {

        validarEmpresaDoUsuario(empresaId);

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

        usuario.setPerfil(perfil);

        return converterParaResponse(
                usuarioRepository.save(usuario)
        );
    }

    public UsuarioResponse ativar(
            Long id,
            Long empresaId
    ) {

        validarEmpresaDoUsuario(empresaId);

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

        usuario.setStatus(Status.ATIVO);

        return converterParaResponse(
                usuarioRepository.save(usuario)
        );
    }

    public UsuarioResponse desativar(
            Long id,
            Long empresaId
    ) {

        validarEmpresaDoUsuario(empresaId);

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

        usuario.setStatus(Status.INATIVO);

        return converterParaResponse(
                usuarioRepository.save(usuario)
        );
    }

    /*
     * ============================================================
     * Métodos internos
     * ============================================================
     */

    private Usuario buscarUsuarioDaEmpresa(
            Long id,
            Long empresaId
    ) {

        return usuarioRepository
                .findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuário não encontrado."
                        )
                );
    }

    private Empresa buscarEmpresa(Long empresaId) {

        return empresaRepository.findById(empresaId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Empresa não encontrada."
                        )
                );
    }

    private void validarEmail(String email) {

        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Já existe um usuário com este email."
            );
        }
    }

    private void validarEmailNaAtualizacao(
            String email,
            Long usuarioId
    ) {

        usuarioRepository.findByEmail(email)
                .ifPresent(usuario -> {

                    if (!usuario.getId().equals(usuarioId)) {
                        throw new IllegalArgumentException(
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
                usuario.getEmpresa().getId()
        );
    }

    private void validarPerfilNaCriacao(
            Perfil perfil,
            Long empresaId
    ) {

        if (perfil == Perfil.ADMIN &&
                usuarioRepository.existsByEmpresaIdAndPerfil(
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
                usuarioRepository.existsByEmpresaIdAndPerfil(
                        empresaId,
                        Perfil.ADMIN
                )) {

            throw new IllegalStateException(
                    "Esta empresa já possui um administrador."
            );
        }
    }

    private void validarEmpresaDoUsuario(
            Long empresaId
    ) {

        Long empresaAutenticadaId =
                securityUtils.getEmpresaId();

        if (!empresaAutenticadaId.equals(empresaId)) {

            throw new org.springframework.security.access.AccessDeniedException(
                    "Você não possui acesso a esta empresa."
            );
        }
    }
}