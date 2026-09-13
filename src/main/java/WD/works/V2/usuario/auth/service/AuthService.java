package WD.works.V2.usuario.auth.service;

import WD.works.V2.auditoria.dto.AuditoriaRequest;
import WD.works.V2.auditoria.entity.Auditoria;
import WD.works.V2.auditoria.gravidade.GravidadeAuditoria;
import WD.works.V2.auditoria.repository.AuditoriaRepository;
import WD.works.V2.auditoria.service.AuditoriaService;
import WD.works.V2.auditoria.tipo.TipoAuditoria;
import WD.works.V2.configuracao.context.EmpresaContext;
import WD.works.V2.configuracao.context.UsuarioContext;
import WD.works.V2.configuracao.security.AutorizacaoService;
import WD.works.V2.configuracao.security.JwtService;
import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.empresa.repository.EmpresaRepository;
import WD.works.V2.exception.RegraNegocioException;
import WD.works.V2.usuario.auth.dto.*;
import WD.works.V2.usuario.auth.entity.TokenRecuperacaoSenha;
import WD.works.V2.usuario.auth.security.UsuarioDetails;
import WD.works.V2.empresa.dto.EmpresaRequest;
import WD.works.V2.empresa.dto.EmpresaResponse;
import WD.works.V2.empresa.service.EmpresaService;
import WD.works.V2.usuario.dto.UsuarioRequest;
import WD.works.V2.usuario.dto.UsuarioResponse;
import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.usuario.perfil.Perfil;
import WD.works.V2.usuario.repository.UsuarioRepository;
import WD.works.V2.usuario.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final LoginTentativaService loginTentativaService;
    private final TokenRecuperacaoSenhaService tokenRecuperacaoSenhaService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;
    private final EmpresaRepository empresaRepository;


    @Transactional
    public AuthResponse cadastrar(
            CadastroRequest request
    ) {

        EmpresaResponse empresa =
                empresaService.criarInicial(
                        new EmpresaRequest(
                                request.getNomeEmpresa(),
                                request.getNuit(),
                                request.getEmailEmpresa(),
                                request.getContacto(),
                                request.getEndereco()
                        )
                );

        UsuarioResponse admin =
                usuarioService.criarAdminInicial(
                        new UsuarioRequest(
                                request.getNomeAdministrador(),
                                request.getEmailAdministrador(),
                                request.getSenhaAdministrador(),
                                Perfil.ADMIN
                        ),
                        empresa.getId()
                );

        Usuario adminEntity = usuarioRepository.findById(admin.getId()).orElseThrow();
        Empresa empresaEntity = empresaRepository.findById(empresa.getId()).orElseThrow();

        auditoriaService.registrar(
                new AuditoriaRequest(TipoAuditoria.CRIACAO, "empresa",
                        empresa.getId().toString(), "Empresa criada: " + empresa.getNome()),
                adminEntity, empresaEntity, GravidadeAuditoria.INFO, null
        );

        return login(
                new AuthRequest(
                        request.getEmailAdministrador(),
                        request.getSenhaAdministrador()
                )
        );
    }

    public AuthResponse login(AuthRequest request) {

        Usuario usuario =
                usuarioRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() ->
                                new RegraNegocioException(
                                        "Email ou senha inválidos."
                                )
                        );

        /*
         * Verifica se existe um bloqueio temporário
         * causado por excesso de tentativas.
         */
        if (loginTentativaService.estaTemporariamenteBloqueado(usuario)) {

            throw new RegraNegocioException(
                    "Email ou senha inválidos."
            );
        }

        try {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getEmail(),
                                    request.getSenha()
                            )
                    );

            UsuarioDetails usuarioDetails =
                    (UsuarioDetails) authentication.getPrincipal();

            /*
             * Login correto:
             * limpamos as tentativas anteriores.
             */
            loginTentativaService.registrarSucesso(
                    usuario
            );

            String token =
                    jwtService.gerarToken(usuarioDetails);

            auditoriaService.registrar(
                    new AuditoriaRequest(
                            TipoAuditoria.LOGIN,
                            "usuario",
                            usuario.getId().toString(),
                            "Login efetuado com sucesso."
                    ),
                    usuario,
                    usuario.getEmpresa(),
                    GravidadeAuditoria.INFO,
                    null
            );


            return new AuthResponse(token);


        } catch (org.springframework.security.core.AuthenticationException ex) {

            /*
             * A senha estava incorreta ou a autenticação
             * falhou.
             */
            loginTentativaService.registrarFalha(
                    usuario
            );

            throw new RegraNegocioException(
                    "Email ou senha inválidos."
            );
        }
    }

    public AuthMeResponse usuarioAutenticado() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        UsuarioDetails usuarioDetails =
                (UsuarioDetails) authentication.getPrincipal();

        var usuario = usuarioDetails.getUsuario();

        return new AuthMeResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil(),
                usuario.getEmpresa().getId()
        );
    }

    public void solicitarRecuperacaoSenha(
            RecuperacaoSenhaRequest request
    ) {

        usuarioRepository.findByEmail(request.email())
                .ifPresent(usuario -> {

                    String token =
                            tokenRecuperacaoSenhaService.criarToken(usuario);

                    String link =
                            "http://localhost:5173/redefinir-senha?token="
                                    + token;

                    emailService.enviarRecuperacaoSenha(
                            usuario.getEmail(),
                            usuario.getNome(),
                            link
                    );
                });
    }
    public void redefinirSenha(RedefinirSenhaRequest request) {

        TokenRecuperacaoSenha token =
                tokenRecuperacaoSenhaService.validarToken(request.token());

        Usuario usuario = token.getUsuario();

        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));

        usuarioRepository.save(usuario);

        tokenRecuperacaoSenhaService.marcarComoUsado(token);
    }
}