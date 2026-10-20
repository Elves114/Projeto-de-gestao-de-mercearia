package WD.works.V2.usuario.auth.service;

import WD.works.V2.auditoria.dto.AuditoriaRequest;
import WD.works.V2.auditoria.gravidade.GravidadeAuditoria;
import WD.works.V2.auditoria.service.AuditoriaService;
import WD.works.V2.auditoria.tipo.TipoAuditoria;
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
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final LoginTentativaService loginTentativaService;
    private final TokenRecuperacaoSenhaService tokenRecuperacaoSenhaService;
    private final RefreshTokenService refreshTokenService;
    private final ObjectProvider<EmailService> emailServiceProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;
    private final EmpresaRepository empresaRepository;

    /* ============================================================
       CADASTRO
       ============================================================ */

    @Transactional
    public AuthResponse cadastrar(CadastroRequest request) {

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

        Usuario adminEntity = usuarioRepository
                .findById(admin.getId())
                .orElseThrow();

        Empresa empresaEntity = empresaRepository
                .findById(empresa.getId())
                .orElseThrow();

        auditoriaService.registrar(
                new AuditoriaRequest(
                        TipoAuditoria.CRIACAO,
                        "empresa",
                        empresa.getId().toString(),
                        "Empresa criada: " + empresa.getNome()
                ),
                adminEntity,
                empresaEntity,
                GravidadeAuditoria.INFO,
                null
        );

        return login(
                new AuthRequest(
                        request.getEmailAdministrador(),
                        request.getSenhaAdministrador()
                ),
                null
        );
    }

    /* ============================================================
       LOGIN
       ============================================================ */

    @Transactional
    public AuthResponse login(AuthRequest request, HttpServletRequest httpRequest) {

        Usuario usuario = usuarioRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new RegraNegocioException(
                        "Email ou senha inválidos."
                ));

        if (loginTentativaService.estaTemporariamenteBloqueado(usuario)) {
            throw new RegraNegocioException("Email ou senha inválidos.");
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

            Usuario usuarioAutenticado = usuarioDetails.getUsuario();

            loginTentativaService.registrarSucesso(usuarioAutenticado);

            // Gera access + refresh
            return gerarRespostaAutenticacao(usuarioAutenticado, httpRequest);

        } catch (org.springframework.security.core.AuthenticationException ex) {
            loginTentativaService.registrarFalha(usuario);
            throw new RegraNegocioException("Email ou senha inválidos.");
        }
    }

    /* ============================================================
       REFRESH
       ============================================================ */

    @Transactional
    public AuthResponse refresh(String refreshToken, HttpServletRequest httpRequest) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new RegraNegocioException("Refresh token não informado.");
        }


        Usuario usuario = refreshTokenService.validarERevogar(refreshToken);

        // 3. Gera novo par
        AuthResponse response = gerarRespostaAutenticacao(usuario, httpRequest);

        // 4. Auditoria
        auditoriaService.registrar(
                new AuditoriaRequest(
                        TipoAuditoria.LOGIN,
                        "usuario",
                        usuario.getId().toString(),
                        "Access token renovado via refresh."
                ),
                usuario,
                usuario.getEmpresa(),
                GravidadeAuditoria.INFO,
                null
        );

        return response;
    }

    /* ============================================================
       LOGOUT
       ============================================================ */

    @Transactional
    public void logout(String refreshToken) {

        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenService.revogar(refreshToken);
        }
    }

    /* ============================================================
       USUÁRIO AUTENTICADO
       ============================================================ */

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

    /* ============================================================
       RECUPERAÇÃO DE SENHA
       ============================================================ */

    public void solicitarRecuperacaoSenha(RecuperacaoSenhaRequest request) {

        usuarioRepository.findByEmail(request.email())
                .ifPresent(usuario -> {

                    String token = tokenRecuperacaoSenhaService.criarToken(usuario);

                    String link = frontendUrl + "/redefinir-senha?token=" + token;

                    EmailService emailService = emailServiceProvider.getIfAvailable();

                    if (emailService != null) {
                        emailService.enviarRecuperacaoSenha(
                                usuario.getEmail(),
                                usuario.getNome(),
                                link
                        );
                    }
                });
    }

    public void redefinirSenha(RedefinirSenhaRequest request) {

        TokenRecuperacaoSenha token =
                tokenRecuperacaoSenhaService.validarToken(request.token());

        Usuario usuario = token.getUsuario();

        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));

        usuarioRepository.save(usuario);

        tokenRecuperacaoSenhaService.marcarComoUsado(token);

        // Segurança: revoga todos os refresh tokens ativos
        refreshTokenService.revogarTodos(usuario.getId());
    }

    /* ============================================================
       MÉTODOS INTERNOS
       ============================================================ */

    /**
     * Gera o par (access + refresh) e devolve a resposta.
     */
    private AuthResponse gerarRespostaAutenticacao(
            Usuario usuario,
            HttpServletRequest httpRequest
    ) {

        UsuarioDetails usuarioDetails =
                new UsuarioDetails(usuario);

        String accessToken =
                jwtService.gerarToken(usuarioDetails);

        String ip = extrairIp(httpRequest);
        String userAgent = httpRequest != null
                ? httpRequest.getHeader("User-Agent")
                : null;

        String refreshToken =
                refreshTokenService.criar(usuario, ip, userAgent);

        return new AuthResponse(
                accessToken,
                refreshToken,
                jwtService.getExpirationSeconds()
        );
    }

    private String extrairIp(HttpServletRequest request) {

        if (request == null) return null;

        String xff = request.getHeader("X-Forwarded-For");

        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}