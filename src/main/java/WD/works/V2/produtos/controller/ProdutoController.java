package WD.works.V2.produtos.controller;

import WD.works.V2.produtos.dto.ProdutoRequest;
import WD.works.V2.produtos.dto.ProdutoResponse;
import WD.works.V2.produtos.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@Tag(
        name = "Produtos",
        description = "Operações de gerenciamento dos produtos"
)
@SecurityRequirement(name = "bearerAuth")
public class ProdutoController {

    private final ProdutoService produtoService;


    @Operation(
            summary = "Criar produto",
            description = "Cria um novo produto para a empresa informada."
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "201",
                    description = "Produto criado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ProdutoResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Dados do produto inválidos",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão para criar produtos",
                    content = @Content
            )
    })
    @PostMapping("/empresa/{empresaId}")
    @PreAuthorize("hasAuthority('PRODUTO_CRIAR')")
    public ResponseEntity<ProdutoResponse> criar(
            @PathVariable Long empresaId,
            @Valid @RequestBody ProdutoRequest request
    ) {

        ProdutoResponse response =
                produtoService.criar(request, empresaId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @Operation(
            summary = "Buscar produto por ID",
            description = "Retorna um produto específico pertencente à empresa informada."
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Produto encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ProdutoResponse.class
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
                    description = "Usuário não possui permissão para visualizar produtos",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado",
                    content = @Content
            )
    })
    @GetMapping("/{id}/empresa/{empresaId}")
    @PreAuthorize("hasAuthority('PRODUTO_VISUALIZAR')")
    public ResponseEntity<ProdutoResponse> buscarPorId(
            @PathVariable Long id,
            @PathVariable Long empresaId
    ) {

        return ResponseEntity.ok(
                produtoService.buscarPorId(id, empresaId)
        );
    }


    @Operation(
            summary = "Listar produtos da empresa",
            description = "Retorna todos os produtos pertencentes à empresa informada."
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Produtos encontrados",
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
                    description = "Usuário não possui permissão para visualizar produtos",
                    content = @Content
            )
    })
    @GetMapping
    @PreAuthorize("hasAuthority('PRODUTO_VISUALIZAR')")
    public ResponseEntity<Page<ProdutoResponse>> listarPorEmpresa(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                produtoService.listarPorEmpresa(pageable)
        );
    }

    @Operation(
            summary = "Pesquisar produtos",
            description = "Pesquisa produtos pelo nome dentro da empresa informada."
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Pesquisa realizada com sucesso",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Parâmetro de pesquisa inválido",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão para visualizar produtos",
                    content = @Content
            )
    })

    @GetMapping("/empresa/{empresaId}/pesquisar")
    @PreAuthorize("hasAuthority('PRODUTO_VISUALIZAR')")
    public ResponseEntity<Page<ProdutoResponse>> pesquisar(
            @PathVariable Long empresaId,
            @RequestParam String nome,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                produtoService.pesquisar(
                        nome,
                        empresaId,
                        pageable
                )
        );
    }

    @Operation(
            summary = "Atualizar produto",
            description = "Atualiza os dados de um produto pertencente à empresa informada."
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Produto atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ProdutoResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Dados do produto inválidos",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão para editar produtos",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado",
                    content = @Content
            )
    })
    @PutMapping("/{id}/empresa/{empresaId}")
    @PreAuthorize("hasAuthority('PRODUTO_EDITAR')")
    public ResponseEntity<ProdutoResponse> atualizar(
            @PathVariable Long id,
            @PathVariable Long empresaId,
            @Valid @RequestBody ProdutoRequest request
    ) {

        return ResponseEntity.ok(
                produtoService.atualizar(
                        id,
                        request,
                        empresaId
                )
        );
    }


    @Operation(
            summary = "Eliminar produto",
            description = "Remove um produto pertencente à empresa informada."
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "204",
                    description = "Produto eliminado com sucesso"
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão para eliminar produtos",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}/empresa/{empresaId}")
    @PreAuthorize("hasAuthority('PRODUTO_EXCLUIR')")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @PathVariable Long empresaId
    ) {

        produtoService.eliminar(id, empresaId);

        return ResponseEntity.noContent().build();
    }
}