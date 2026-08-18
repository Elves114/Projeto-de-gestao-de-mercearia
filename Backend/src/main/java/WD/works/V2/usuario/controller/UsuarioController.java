package WD.works.V2.usuario.controller;

import WD.works.V2.usuario.dto.UsuarioRequest;
import WD.works.V2.usuario.dto.UsuarioResponse;
import WD.works.V2.usuario.perfil.Perfil;
import WD.works.V2.usuario.service.UsuarioService;
import WD.works.V2.usuario.dto.UsuarioAtualizacaoRequest;
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
    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(

            @Valid
            @RequestBody UsuarioRequest request

    ) {

        UsuarioResponse response =
                usuarioService.criar(request);

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
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(

            @PathVariable Long id

    ) {

        return ResponseEntity.ok(
                usuarioService.buscarPorId(id)
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
    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResponse> buscarPorEmail(

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
                usuarioService.buscarPorEmail(email)
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
    @GetMapping
    @PreAuthorize("hasAuthority('USUARIO_VISUALIZAR')")
    public ResponseEntity<Page<UsuarioResponse>> listarPorEmpresa(


            Pageable pageable

    ) {

        return ResponseEntity.ok(
                usuarioService.listarPorEmpresa(pageable)
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
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizar(

            @Parameter(
                    name = "id",
                    description = "ID do usuário.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "1"
            )
            @PathVariable Long id,

            @Valid
            @RequestBody UsuarioAtualizacaoRequest request

    ) {

        return ResponseEntity.ok(
                usuarioService.atualizar(
                        id,
                        request
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
    @PatchMapping("/{id}/perfil")
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
                        perfil
                )
        );
    }

    @Operation(summary = "Pesquisar usuários",
            description = " Pesquisa usuários da empresa do usuário autenticado através do nome ou email. A pesquisa não permite acessar usuários pertencentes a outras empresas. É necessário possuir a permissão USUARIO_VISUALIZAR. ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pesquisa realizada com sucesso."
                    , content = @Content(
                    schema = @Schema(implementation = UsuarioResponse.class)))
            , @ApiResponse(responseCode = "401", description = "Usuário não autenticado.", content = @Content), @ApiResponse(responseCode = "403", description = "Usuário não possui permissão.", content = @Content)})
    @PreAuthorize("hasAuthority('USUARIO_VISUALIZAR')")
    @GetMapping("/pesquisar")
    public ResponseEntity<Page<UsuarioResponse>> pesquisar(
            @Parameter(name = "termo", description = "Nome ou email do usuário.", required = true, in = ParameterIn.QUERY, example = "joao") @RequestParam String termo, Pageable pageable) {
        return ResponseEntity.ok(usuarioService.pesquisar(termo, pageable));
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
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<UsuarioResponse> ativar(

            @Parameter(
                    name = "id",
                    description = "ID do usuário.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "1"
            )
            @PathVariable Long id

    ) {

        return ResponseEntity.ok(
                usuarioService.ativar(id)
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
                    Desativa um usuário da empresa do usuário autenticado.
                    
                    A empresa é obtida automaticamente através
                    do usuário autenticado.
                    
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
    @PatchMapping("/{id}/desativar")
    public ResponseEntity<UsuarioResponse> desativar(

            @Parameter(
                    name = "id",
                    description = "ID do usuário.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "1"
            )
            @PathVariable Long id

    ) {

        return ResponseEntity.ok(
                usuarioService.desativar(id)
        );
    }
}