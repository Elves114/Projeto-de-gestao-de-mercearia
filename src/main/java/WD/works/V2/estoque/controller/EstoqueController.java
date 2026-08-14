package WD.works.V2.estoque.controller;

import WD.works.V2.estoque.dto.EstoqueResponse;
import WD.works.V2.estoque.service.EstoqueService;
import WD.works.V2.usuario.auth.security.UsuarioDetails;
import WD.works.V2.usuario.entity.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estoques")
@RequiredArgsConstructor
@Tag(
        name = "Estoque",
        description = "Operações de consulta do estoque da empresa"
)
@SecurityRequirement(name = "bearerAuth")
public class EstoqueController {

    private final EstoqueService estoqueService;


    @Operation(
            summary = "Listar estoque",
            description = """
                    Retorna todos os registros de estoque
                    pertencentes à empresa do usuário autenticado.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Estoque listado com sucesso",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão para visualizar o estoque",
                    content = @Content
            )
    })
    @GetMapping
    @PreAuthorize("hasAuthority('ESTOQUE_VISUALIZAR')")

    public ResponseEntity<Page<EstoqueResponse>> listar(
            Authentication authentication,
            Pageable pageable
    ) {

        Usuario usuario =
                obterUsuario(authentication);

        Long empresaId =
                usuario.getEmpresa().getId();

        return ResponseEntity.ok(
                estoqueService.listarPorEmpresa(
                        empresaId,
                        pageable
                )
        );
    }


    @Operation(
            summary = "Buscar estoque por ID",
            description = """
                    Retorna um registro específico de estoque
                    pertencente à empresa do usuário autenticado.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Estoque encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = EstoqueResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão para visualizar o estoque",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Estoque não encontrado",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ESTOQUE_VISUALIZAR')")
    public ResponseEntity<EstoqueResponse> buscarPorId(
            @PathVariable Long id,
            Authentication authentication
    ) {

        Usuario usuario =
                obterUsuario(authentication);

        Long empresaId =
                usuario.getEmpresa().getId();

        return ResponseEntity.ok(
                estoqueService.buscarPorId(
                        id,
                        empresaId
                )
        );
    }


    @Operation(
            summary = "Buscar estoque por produto",
            description = """
                    Retorna o estoque associado a um produto
                    pertencente à empresa do usuário autenticado.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Estoque do produto encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = EstoqueResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão para visualizar o estoque",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Estoque do produto não encontrado",
                    content = @Content
            )
    })
    @GetMapping("/produto/{produtoId}")
    @PreAuthorize("hasAuthority('ESTOQUE_VISUALIZAR')")
    public ResponseEntity<EstoqueResponse> buscarPorProduto(
            @PathVariable Long produtoId,
            Authentication authentication
    ) {

        Usuario usuario =
                obterUsuario(authentication);

        Long empresaId =
                usuario.getEmpresa().getId();

        return ResponseEntity.ok(
                estoqueService.buscarPorProduto(
                        produtoId,
                        empresaId
                )
        );
    }


    /*
     * ============================================================
     * MÉTODO INTERNO
     * ============================================================
     */

    private Usuario obterUsuario(
            Authentication authentication
    ) {

        UsuarioDetails usuarioDetails =
                (UsuarioDetails)
                        authentication.getPrincipal();

        return usuarioDetails.getUsuario();
    }
}