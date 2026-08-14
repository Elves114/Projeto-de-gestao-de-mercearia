package WD.works.V2.usuario.auth.controller;

import WD.works.V2.usuario.auth.dto.AuthRequest;
import WD.works.V2.usuario.auth.dto.AuthResponse;
import WD.works.V2.usuario.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(
        name = "Autenticação",
        description = "Endpoints relacionados à autenticação dos usuários"
)
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Autenticar usuário",
            description = """
                    Realiza o login do usuário através do email e senha.

                    Após uma autenticação bem-sucedida,
                    a API retorna um token JWT que deve ser utilizado
                    nas requisições que exigem autenticação.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Login realizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = AuthResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Dados de login inválidos",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Email ou senha incorretos",
                    content = @Content
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthRequest request
    ) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }
}