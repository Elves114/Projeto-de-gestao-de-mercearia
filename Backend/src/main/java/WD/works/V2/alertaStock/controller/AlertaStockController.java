package WD.works.V2.alertaStock.controller;

import WD.works.V2.alertaStock.dto.AlertaStockResponse;
import WD.works.V2.alertaStock.service.AlertaStockService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alertas-stock")
@RequiredArgsConstructor
@Tag(
        name = "Alertas de Stock",
        description = "Operações relacionadas aos alertas de stock"
)
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('ALERTA_STOCK_VISUALIZAR')")
public class AlertaStockController {

    private final AlertaStockService alertaStockService;

    @Operation(
            summary = "Listar alertas de stock",
            description = """
                    Retorna todos os alertas de stock
                    pertencentes à empresa do usuário autenticado.

                    Os alertas são apresentados do mais recente
                    para o mais antigo.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Alertas encontrados",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = AlertaStockResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<Page<AlertaStockResponse>> listar(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                alertaStockService.listar(pageable)
        );
    }

    @Operation(
            summary = "Listar alertas de stock ativos",
            description = """
                    Retorna apenas os alertas de stock
                    que ainda estão ativos na empresa
                    do usuário autenticado.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Alertas ativos encontrados",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = AlertaStockResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content
            )
    })
    @GetMapping("/ativos")
    public ResponseEntity<Page<AlertaStockResponse>> listarAtivos(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                alertaStockService.listarAtivos(pageable)
        );
    }

    @Operation(
            summary = "Buscar alerta de stock por ID",
            description = """
                    Retorna um alerta específico pertencente
                    à empresa do usuário autenticado.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Alerta encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = AlertaStockResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Alerta não encontrado",
                    content = @Content
            )
    })

    @GetMapping("/{id}")
    public ResponseEntity<AlertaStockResponse> buscarPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                alertaStockService.buscarPorId(id)
        );
    }
}