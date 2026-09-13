package WD.works.V2.movimentoStock.controller;

import WD.works.V2.movimentoStock.dto.MovimentoStockRequest;
import WD.works.V2.movimentoStock.dto.MovimentoStockResponse;
import WD.works.V2.movimentoStock.service.MovimentoStockService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movimentos-stock")
@RequiredArgsConstructor
@Tag(
        name = "Movimentos de Stock",
        description = "Operações relacionadas às movimentações de stock"
)
@SecurityRequirement(name = "bearerAuth")
public class MovimentoStockController {

    private final MovimentoStockService movimentoStockService;

    @Operation(
            summary = "Criar movimento de stock",
            description = """
                    Registra uma movimentação de stock para um produto.
                    
                    As ações disponíveis são:
                    - ENTRADA: aumenta a quantidade em stock.
                    - SAIDA: reduz a quantidade em stock.
                    - DEVOLUCAO: aumenta a quantidade em stock.
                    - AJUSTE: define diretamente a quantidade existente.
                    
                    O usuário e a empresa são obtidos
                    através do usuário autenticado.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "201",
                    description = "Movimento de stock criado com sucesso",
                    content = @Content(
                            schema = @Schema(
                                    implementation = MovimentoStockResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou stock insuficiente"
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Produto ou stock não encontrado"
            )
    })
    @PostMapping
    @PreAuthorize("""
            hasAuthority('ESTOQUE_MOVIMENTAR')
            or hasAuthority('ESTOQUE_AJUSTAR')
            """)
    public ResponseEntity<MovimentoStockResponse> criar(
            @Valid
            @RequestBody
            MovimentoStockRequest request
    ) {

        MovimentoStockResponse response =
                movimentoStockService.criar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Listar movimentos de stock",
            description = """
                Retorna os movimentos de stock
                pertencentes à empresa do usuário autenticado.

                É possível pesquisar pelo nome do produto.

                Os movimentos são apresentados
                do mais recente para o mais antigo.
                """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Movimentos encontrados"
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            )
    })
    @GetMapping
    public ResponseEntity<Page<MovimentoStockResponse>> listar(

            @Parameter(
                    description = "Nome ou parte do nome do produto",
                    example = "Arroz"
            )
            @RequestParam(required = false)
            String produto,

            Pageable pageable
    ) {

        return ResponseEntity.ok(
                movimentoStockService.listar(
                        produto,
                        pageable
                )
        );
    }

    @Operation(
            summary = "Listar movimentos de um produto",
            description = """
                    Retorna o histórico de movimentações de stock
                    de um determinado produto.
                    
                    O produto deve pertencer à empresa
                    do usuário autenticado.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Movimentos encontrados"
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado"
            )
    })
    @GetMapping("/produto/{produtoId}")
    @PreAuthorize("hasAuthority('ESTOQUE_VISUALIZAR')")
    public ResponseEntity<Page<MovimentoStockResponse>> listarPorProduto(

            @Parameter(
                    description = "ID do produto",
                    example = "1",
                    required = true
            )
            @PathVariable Long produtoId,

            Pageable pageable
    ) {

        return ResponseEntity.ok(
                movimentoStockService.listarPorProduto(
                        produtoId,
                        pageable
                )
        );
    }
}