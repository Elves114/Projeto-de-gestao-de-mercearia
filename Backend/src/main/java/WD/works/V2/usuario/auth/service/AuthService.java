package WD.works.V2.usuario.auth.service;

import WD.works.V2.configuracao.security.JwtService;
import WD.works.V2.usuario.auth.dto.AuthMeResponse;
import WD.works.V2.usuario.auth.dto.AuthRequest;
import WD.works.V2.usuario.auth.dto.AuthResponse;
import WD.works.V2.usuario.auth.dto.CadastroRequest;
import WD.works.V2.usuario.auth.security.UsuarioDetails;
import WD.works.V2.empresa.dto.EmpresaRequest;
import WD.works.V2.empresa.dto.EmpresaResponse;
import WD.works.V2.empresa.service.EmpresaService;
import WD.works.V2.usuario.dto.UsuarioRequest;
import WD.works.V2.usuario.dto.UsuarioResponse;
import WD.works.V2.usuario.perfil.Perfil;
import WD.works.V2.usuario.service.UsuarioService;
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

        return login(
                new AuthRequest(
                        request.getEmailAdministrador(),
                        request.getSenhaAdministrador()
                )
        );
    }

    public AuthResponse login(AuthRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getSenha()
                        )
                );

        UsuarioDetails usuarioDetails =
                (UsuarioDetails) authentication.getPrincipal();

        String token =
                jwtService.gerarToken(usuarioDetails);

        return new AuthResponse(token);
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
}