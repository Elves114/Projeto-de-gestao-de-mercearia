package WD.works.V2.categorias.controller;

import WD.works.V2.categorias.dto.CategoriaProdutoRequest;
import WD.works.V2.categorias.dto.CategoriaProdutoResponse;
import WD.works.V2.categorias.repository.CategoriaProdutoRepository;
import WD.works.V2.categorias.service.CategoriaProdutoService;
import WD.works.V2.configuracao.context.EmpresaContext;
import WD.works.V2.empresa.repository.EmpresaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Tag(
        name = "Categorias",
        description = "Operações de gerenciamento das categorias de produtos"
)
@SecurityRequirement(name = "bearerAuth")
public class CategoriaProdutoController {

    private final CategoriaProdutoService categoriaService;
    private final CategoriaProdutoRepository categoriaProdutoRepository;
    private final EmpresaRepository empresaRepository;
    private final EmpresaContext empresaContext;

    @Operation(
            summary = "Criar categoria",
            description = "Cria uma nova categoria de produtos para uma empresa."
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "201",
                    description = "Categoria criada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = CategoriaProdutoResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Dados da categoria inválidos",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão para criar categorias",
                    content = @Content
            )
    })
    @PostMapping()
    @PreAuthorize("hasAuthority('CATEGORIA_CRIAR')")
    public ResponseEntity<CategoriaProdutoResponse> criar(
            @Valid @RequestBody CategoriaProdutoRequest request
    ) {

        CategoriaProdutoResponse response =
                categoriaService.criar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @Operation(
            summary = "Buscar categoria por ID",
            description = "Retorna uma categoria específica pertencente à empresa informada."
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Categoria encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = CategoriaProdutoResponse.class
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
                    description = "Usuário não possui permissão para visualizar categorias",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Categoria não encontrada",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CATEGORIA_VISUALIZAR')")
    public ResponseEntity<CategoriaProdutoResponse> buscarPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                categoriaService.buscarPorId(id)
        );
    }
    @Operation(
            summary = "Listar e pesquisar categorias",
            description = """
                Retorna as categorias da empresa do usuário autenticado
                de forma paginada.

                O parâmetro 'nome' é opcional.
                Quando informado, retorna apenas categorias
                cujo nome contenha o texto pesquisado.
                """
    )
    @Parameter(
            description = "Texto para pesquisar no nome da categoria",
            example = "bebidas",
            required = false
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Categorias encontradas",
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
                    description = "Usuário não possui permissão para visualizar categorias",
                    content = @Content
            )
    })
    @GetMapping
    @PreAuthorize("hasAuthority('CATEGORIA_VISUALIZAR')")
    public ResponseEntity<Page<CategoriaProdutoResponse>> listarPorEmpresa(

            @RequestParam(required = false)
            String nome,

            Pageable pageable
    ) {

        return ResponseEntity.ok(
                categoriaService.listarPorEmpresa(
                        nome,
                        pageable
                )
        );
    }

    @Operation(
            summary = "Atualizar categoria",
            description = "Atualiza os dados de uma categoria pertencente à empresa informada."
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Categoria atualizada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = CategoriaProdutoResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Dados da categoria inválidos",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão para editar categorias",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Categoria não encontrada",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CATEGORIA_EDITAR')")

    public ResponseEntity<CategoriaProdutoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaProdutoRequest request
    ) {

        return ResponseEntity.ok(
                categoriaService.atualizar(
                        id,
                        request
                )
        );
    }


    @Operation(
            summary = "Eliminar categoria",
            description = "Remove uma categoria pertencente à empresa informada."
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "204",
                    description = "Categoria eliminada com sucesso"
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão para eliminar categorias",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Categoria não encontrada",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CATEGORIA_EXCLUIR')")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {

        categoriaService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}