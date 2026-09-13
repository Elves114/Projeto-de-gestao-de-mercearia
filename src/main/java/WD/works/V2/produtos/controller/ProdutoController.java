package WD.works.V2.produtos.controller;

import WD.works.V2.produtos.dto.ProdutoRequest;
import WD.works.V2.produtos.dto.ProdutoResponse;
import WD.works.V2.produtos.service.ProdutoService;
import WD.works.V2.produtos.status.StatusProduto;
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

import java.math.BigDecimal;

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
            description = "Cria um novo produto para a empresa do usuário autenticado."
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
    @PostMapping
    @PreAuthorize("hasAuthority('PRODUTO_CRIAR')")
    public ResponseEntity<ProdutoResponse> criar(
            @Valid @RequestBody ProdutoRequest request
    ) {

        ProdutoResponse response =
                produtoService.criar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @Operation(
            summary = "Buscar produto por ID",
            description = "Retorna um produto pertencente à empresa do usuário autenticado."
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
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUTO_VISUALIZAR')")
    public ResponseEntity<ProdutoResponse> buscarPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                produtoService.buscarPorId(id)
        );
    }


    @Operation(
            summary = "Pesquisar produtos",
            description = "Pesquisa produtos pelo nome dentro da empresa do usuário autenticado."
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
    @GetMapping
    @PreAuthorize("hasAuthority('PRODUTO_VISUALIZAR')")
    public ResponseEntity<Page<ProdutoResponse>> listarPorEmpresa(

            @RequestParam(required = false)
            String nome,

            @RequestParam(required = false)
            StatusProduto status,

            @RequestParam(required = false)
            Long categoriaId,

            @RequestParam(required = false)
            BigDecimal precoMin,

            @RequestParam(required = false)
            BigDecimal precoMax,

            @RequestParam(required = false)
            Integer quantidadeMin,

            @RequestParam(required = false)
            Integer quantidadeMax,

            Pageable pageable
    ) {

        return ResponseEntity.ok(
                produtoService.pesquisarComFiltros(
                        nome,
                        status,
                        categoriaId,
                        precoMin,
                        precoMax,
                        quantidadeMin,
                        quantidadeMax,
                        pageable
                )
        );
    }


    @Operation(
            summary = "Atualizar produto",
            description = "Atualiza um produto pertencente à empresa do usuário autenticado."
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
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUTO_EDITAR')")
    public ResponseEntity<ProdutoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoRequest request
    ) {

        return ResponseEntity.ok(
                produtoService.atualizar(
                        id,
                        request
                )
        );
    }


    @Operation(
            summary = "Desativar produto",
            description = "Desativa um produto pertencente à empresa do usuário autenticado. " +
                    "O produto não é eliminado da base de dados."
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "204",
                    description = "Produto desativado com sucesso"
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Produto já está inativo",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão para desativar produtos",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado",
                    content = @Content
            )
    })
    @PatchMapping("/{id}/desativar")
    @PreAuthorize("hasAuthority('PRODUTO_DESATIVAR')")
    public ResponseEntity<Void> desativar(
            @PathVariable Long id
    ) {
        produtoService.desativar(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Ativar produto",
            description = "Ativa um produto pertencente à empresa do usuário autenticado."
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "204",
                    description = "Produto ativado com sucesso"
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão para ativar produtos",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado",
                    content = @Content
            )
    })
    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasAuthority('PRODUTO_ATIVAR')")
    public ResponseEntity<Void> ativar(
            @PathVariable Long id
    ) {

        produtoService.ativar(id);

        return ResponseEntity.noContent().build();
    }
}