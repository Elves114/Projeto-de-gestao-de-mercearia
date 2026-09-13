package WD.works.V2.usuario.auth.controller;

import WD.works.V2.usuario.auth.dto.AuthMeResponse;
import WD.works.V2.usuario.auth.dto.AuthRequest;
import WD.works.V2.usuario.auth.dto.AuthResponse;
import WD.works.V2.usuario.auth.service.AuthService;
import WD.works.V2.usuario.auth.dto.CadastroRequest;
import WD.works.V2.usuario.auth.dto.RecuperacaoSenhaRequest;
import WD.works.V2.usuario.auth.dto.RedefinirSenhaRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @GetMapping("/me")
    @Operation(
            summary = "Obter usuário autenticado",
            description = "Retorna os dados do usuário atualmente autenticado."
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Dados do usuário autenticado."
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado."
            )
    })
    public ResponseEntity<AuthMeResponse> usuarioAutenticado() {

        return ResponseEntity.ok(
                authService.usuarioAutenticado()
        );
    }

    @PostMapping("/cadastro")
    @Operation(
            summary = "Cadastrar empresa",
            description = """
                    Cria uma nova empresa juntamente com o
                    seu administrador inicial.
                    
                    O administrador inicial é criado automaticamente
                    com o perfil ADMIN.
                    
                    Este endpoint é público e não exige autenticação.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "201",
                    description = "Empresa e administrador criados com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = AuthResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Dados de cadastro inválidos.",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "409",
                    description = "Empresa ou usuário já existente.",
                    content = @Content
            )
    })
    public ResponseEntity<AuthResponse> cadastrar(
            @Valid @RequestBody CadastroRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        authService.cadastrar(request)
                );
    }

    @PostMapping("/recuperar-senha")
    @Operation(
            summary = "Solicitar recuperação de senha",
            description = """
                    Inicia o processo de recuperação de senha
                    através do email do usuário.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Solicitação processada com sucesso."
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Email inválido.",
                    content = @Content
            )
    })
    public ResponseEntity<Void> recuperarSenha(
            @Valid @RequestBody RecuperacaoSenhaRequest request
    ) {

        authService.solicitarRecuperacaoSenha(request);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/redefinir-senha")
    @Operation(
            summary = "Redefinir senha",
            description = """
                    Redefine a senha do usuário utilizando
                    um token de recuperação válido.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Senha redefinida com sucesso."
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Token inválido, expirado ou já utilizado.",
                    content = @Content
            )
    })
    public ResponseEntity<Void> redefinirSenha(
            @Valid @RequestBody RedefinirSenhaRequest request
    ) {

        authService.redefinirSenha(request);

        return ResponseEntity.ok().build();
    }
}