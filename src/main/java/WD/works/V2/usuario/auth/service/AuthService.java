package WD.works.V2.usuario.auth.service;

import WD.works.V2.configuracao.security.JwtService;
import WD.works.V2.usuario.auth.dto.AuthRequest;
import WD.works.V2.usuario.auth.dto.AuthResponse;
import WD.works.V2.usuario.auth.security.UsuarioDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

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
}