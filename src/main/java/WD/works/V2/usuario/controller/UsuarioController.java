package WD.works.V2.usuario.controller;

import WD.works.V2.usuario.dto.UsuarioRequest;
import WD.works.V2.usuario.dto.UsuarioResponse;
import WD.works.V2.usuario.perfil.Perfil;
import WD.works.V2.usuario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(
        name = "Usuários",
        description = "Operações de gerenciamento dos usuários das empresas."
)
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;


    /*
     * ============================================================
     * CRIAR USUÁRIO
     * ============================================================
     */

    @Operation(
            summary = "Criar usuário",
            description = """
                    Cria um novo usuário associado a uma empresa.

                    É necessário possuir a permissão USUARIO_CRIAR.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário criado com sucesso.",
                    content = @Content(
                            schema = @Schema(
                                    implementation = UsuarioResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Dados do usuário inválidos.",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado.",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão.",
                    content = @Content
            )
    })
    @PreAuthorize("hasAuthority('USUARIO_CRIAR')")
    @PostMapping("/empresa/{empresaId}")
    public ResponseEntity<UsuarioResponse> criar(

            @Parameter(
                    name = "empresaId",
                    description = "ID da empresa à qual o usuário será associado.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "1"
            )
            @PathVariable Long empresaId,

            @Valid
            @RequestBody UsuarioRequest request

    ) {

        UsuarioResponse response =
                usuarioService.criar(
                        request,
                        empresaId
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    /*
     * ============================================================
     * BUSCAR POR ID
     * ============================================================
     */

    @Operation(
            summary = "Buscar usuário por ID",
            description = """
                    Retorna os dados de um usuário específico
                    pertencente à empresa informada.

                    É necessário possuir a permissão
                    USUARIO_VISUALIZAR.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário encontrado.",
                    content = @Content(
                            schema = @Schema(
                                    implementation = UsuarioResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado.",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão.",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado.",
                    content = @Content
            )
    })
    @PreAuthorize("hasAuthority('USUARIO_VISUALIZAR')")
    @GetMapping("/{id}/empresa/{empresaId}")
    public ResponseEntity<UsuarioResponse> buscarPorId(

            @Parameter(
                    name = "id",
                    description = "ID do usuário.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "1"
            )
            @PathVariable Long id,

            @Parameter(
                    name = "empresaId",
                    description = "ID da empresa do usuário.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "1"
            )
            @PathVariable Long empresaId

    ) {

        return ResponseEntity.ok(
                usuarioService.buscarPorId(
                        id,
                        empresaId
                )
        );
    }


    /*
     * ============================================================
     * BUSCAR POR EMAIL
     * ============================================================
     */

    @Operation(
            summary = "Buscar usuário por email",
            description = """
                    Retorna um usuário através do seu email
                    dentro da empresa informada.

                    É necessário possuir a permissão
                    USUARIO_VISUALIZAR.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário encontrado.",
                    content = @Content(
                            schema = @Schema(
                                    implementation = UsuarioResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado.",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão.",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado.",
                    content = @Content
            )
    })
    @PreAuthorize("hasAuthority('USUARIO_VISUALIZAR')")
    @GetMapping("/empresa/{empresaId}/email/{email}")
    public ResponseEntity<UsuarioResponse> buscarPorEmail(

            @Parameter(
                    name = "empresaId",
                    description = "ID da empresa.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "1"
            )
            @PathVariable Long empresaId,

            @Parameter(
                    name = "email",
                    description = "Email do usuário.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "usuario@email.com"
            )
            @PathVariable String email

    ) {

        return ResponseEntity.ok(
                usuarioService.buscarPorEmail(
                        email,
                        empresaId
                )
        );
    }


    /*
     * ============================================================
     * LISTAR USUÁRIOS
     * ============================================================
     */

    @Operation(
            summary = "Listar usuários da empresa",
            description = """
                    Retorna todos os usuários pertencentes à empresa
                    informada.

                    É necessário possuir a permissão
                    USUARIO_VISUALIZAR.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuários retornada com sucesso.",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = UsuarioResponse.class
                                    )
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado.",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão.",
                    content = @Content
            )
    })
    @GetMapping("/empresa/{empresaId}")
    @PreAuthorize("hasAuthority('USUARIO_VISUALIZAR')")
    public ResponseEntity<Page<UsuarioResponse>> listarPorEmpresa(

            @Parameter(
                    name = "empresaId",
                    description = "ID da empresa.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "1"
            )
            @PathVariable Long empresaId,

            Pageable pageable

    ) {

        return ResponseEntity.ok(
                usuarioService.listarPorEmpresa(
                        empresaId,
                        pageable
                )
        );
    }


    /*
     * ============================================================
     * CRIAR ADMINISTRADOR INICIAL
     * ============================================================
     */

    @Operation(
            summary = "Criar administrador inicial",
            description = """
                    Cria o administrador inicial de uma empresa.

                    Este endpoint é utilizado no processo de
                    inicialização da empresa.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "201",
                    description = "Administrador inicial criado com sucesso.",
                    content = @Content(
                            schema = @Schema(
                                    implementation = UsuarioResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou administrador já existente.",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Empresa não encontrada.",
                    content = @Content
            )
    })
    @PostMapping("/empresa/{empresaId}/admin-inicial")
    public ResponseEntity<UsuarioResponse> criarAdminInicial(

            @Parameter(
                    name = "empresaId",
                    description = "ID da empresa que receberá o administrador inicial.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "1"
            )
            @PathVariable Long empresaId,

            @Valid
            @RequestBody UsuarioRequest request

    ) {

        UsuarioResponse response =
                usuarioService.criarAdminInicial(
                        request,
                        empresaId
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    /*
     * ============================================================
     * ATUALIZAR USUÁRIO
     * ============================================================
     */

    @Operation(
            summary = "Atualizar usuário",
            description = """
                    Atualiza os dados de um usuário pertencente
                    à empresa informada.

                    É necessário possuir a permissão
                    USUARIO_EDITAR.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário atualizado com sucesso.",
                    content = @Content(
                            schema = @Schema(
                                    implementation = UsuarioResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos.",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado.",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão.",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado.",
                    content = @Content
            )
    })
    @PreAuthorize("hasAuthority('USUARIO_EDITAR')")
    @PutMapping("/{id}/empresa/{empresaId}")
    public ResponseEntity<UsuarioResponse> atualizar(

            @Parameter(
                    name = "id",
                    description = "ID do usuário.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "1"
            )
            @PathVariable Long id,

            @Parameter(
                    name = "empresaId",
                    description = "ID da empresa.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "1"
            )
            @PathVariable Long empresaId,

            @Valid
            @RequestBody UsuarioRequest request

    ) {

        return ResponseEntity.ok(
                usuarioService.atualizar(
                        id,
                        request,
                        empresaId
                )
        );
    }


    /*
     * ============================================================
     * ALTERAR PERFIL
     * ============================================================
     */

    @Operation(
            summary = "Alterar perfil do usuário",
            description = """
                    Altera o perfil de acesso de um usuário.

                    O perfil determina as permissões que o usuário
                    possui no sistema.

                    É necessário possuir a permissão
                    USUARIO_EDITAR.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Perfil alterado com sucesso.",
                    content = @Content(
                            schema = @Schema(
                                    implementation = UsuarioResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Perfil inválido.",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado.",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão.",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado.",
                    content = @Content
            )
    })
    @PreAuthorize("hasAuthority('USUARIO_EDITAR')")
    @PatchMapping("/{id}/empresa/{empresaId}/perfil")
    public ResponseEntity<UsuarioResponse> alterarPerfil(

            @Parameter(
                    name = "id",
                    description = "ID do usuário.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "1"
            )
            @PathVariable Long id,

            @Parameter(
                    name = "empresaId",
                    description = "ID da empresa.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "1"
            )
            @PathVariable Long empresaId,

            @Parameter(
                    name = "perfil",
                    description = "Novo perfil do usuário.",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "FUNCIONARIO"
            )
            @RequestParam Perfil perfil

    ) {

        return ResponseEntity.ok(
                usuarioService.alterarPerfil(
                        id,
                        perfil,
                        empresaId
                )
        );
    }


    /*
     * ============================================================
     * ATIVAR USUÁRIO
     * ============================================================
     */

    @Operation(
            summary = "Ativar usuário",
            description = """
                    Ativa um usuário da empresa.

                    É necessário possuir a permissão
                    USUARIO_BLOQUEAR.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário ativado com sucesso.",
                    content = @Content(
                            schema = @Schema(
                                    implementation = UsuarioResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado.",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão.",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado.",
                    content = @Content
            )
    })
    @PreAuthorize("hasAuthority('USUARIO_BLOQUEAR')")
    @PatchMapping("/{id}/empresa/{empresaId}/ativar")
    public ResponseEntity<UsuarioResponse> ativar(

            @Parameter(
                    name = "id",
                    description = "ID do usuário.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "1"
            )
            @PathVariable Long id,

            @Parameter(
                    name = "empresaId",
                    description = "ID da empresa.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "1"
            )
            @PathVariable Long empresaId

    ) {

        return ResponseEntity.ok(
                usuarioService.ativar(
                        id,
                        empresaId
                )
        );
    }


    /*
     * ============================================================
     * DESATIVAR USUÁRIO
     * ============================================================
     */

    @Operation(
            summary = "Desativar usuário",
            description = """
                    Desativa um usuário da empresa.

                    É necessário possuir a permissão
                    USUARIO_BLOQUEAR.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário desativado com sucesso.",
                    content = @Content(
                            schema = @Schema(
                                    implementation = UsuarioResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado.",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão.",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado.",
                    content = @Content
            )
    })
    @PreAuthorize("hasAuthority('USUARIO_BLOQUEAR')")
    @PatchMapping("/{id}/empresa/{empresaId}/desativar")
    public ResponseEntity<UsuarioResponse> desativar(

            @Parameter(
                    name = "id",
                    description = "ID do usuário.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "1"
            )
            @PathVariable Long id,

            @Parameter(
                    name = "empresaId",
                    description = "ID da empresa.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "1"
            )
            @PathVariable Long empresaId

    ) {

        return ResponseEntity.ok(
                usuarioService.desativar(
                        id,
                        empresaId
                )
        );
    }
}