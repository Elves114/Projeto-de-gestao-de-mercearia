package WD.works.V2.usuario.auth.controller;

import WD.works.V2.usuario.auth.dto.*;
import WD.works.V2.usuario.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de autenticação")
public class AuthController {

    private final AuthService authService;

    /* ============================================================
       LOGIN
       ============================================================ */

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login OK",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(
                authService.login(request, httpRequest)
        );
    }

    /* ============================================================
       REFRESH
       ============================================================ */

    @PostMapping("/refresh")
    @Operation(
            summary = "Renovar access token",
            description = "Usa um refresh token válido para gerar um novo par de tokens."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token renovado",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Refresh token inválido ou expirado")
    })
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(
                authService.refresh(request.getRefreshToken(), httpRequest)
        );
    }

    /* ============================================================
       LOGOUT
       ============================================================ */

    @PostMapping("/logout")
    @Operation(
            summary = "Terminar sessão",
            description = "Invalida o refresh token. O access token continua válido até expirar."
    )
    @ApiResponse(responseCode = "204", description = "Sessão terminada")
    public ResponseEntity<Void> logout(
            @RequestBody(required = false) RefreshTokenRequest request
    ) {
        String token = request != null ? request.getRefreshToken() : null;
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }

    /* ============================================================
       ME
       ============================================================ */

    @GetMapping("/me")
    @Operation(summary = "Utilizador autenticado")
    public ResponseEntity<AuthMeResponse> usuarioAutenticado() {
        return ResponseEntity.ok(authService.usuarioAutenticado());
    }

    /* ============================================================
       CADASTRO
       ============================================================ */

    @PostMapping("/cadastro")
    @Operation(summary = "Cadastrar empresa + admin")
    @ApiResponse(responseCode = "201", description = "Conta criada",
            content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    public ResponseEntity<AuthResponse> cadastrar(
            @Valid @RequestBody CadastroRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.cadastrar(request));
    }

    /* ============================================================
       RECUPERAÇÃO DE SENHA
       ============================================================ */

    @PostMapping("/recuperar-senha")
    @Operation(summary = "Solicitar recuperação de senha")
    public ResponseEntity<Void> recuperarSenha(
            @Valid @RequestBody RecuperacaoSenhaRequest request
    ) {
        authService.solicitarRecuperacaoSenha(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/redefinir-senha")
    @Operation(summary = "Redefinir senha com token")
    public ResponseEntity<Void> redefinirSenha(
            @Valid @RequestBody RedefinirSenhaRequest request
    ) {
        authService.redefinirSenha(request);
        return ResponseEntity.ok().build();
    }
}