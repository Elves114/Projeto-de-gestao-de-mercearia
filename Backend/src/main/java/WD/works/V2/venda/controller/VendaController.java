package WD.works.V2.venda.controller;

import WD.works.V2.venda.dto.VendaRequest;
import WD.works.V2.venda.dto.VendaResponse;
import WD.works.V2.venda.service.VendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/vendas")
@RequiredArgsConstructor
@Tag(
        name = "Vendas",
        description = "Operações relacionadas às vendas da empresa"
)
public class VendaController {

    private final VendaService vendaService;


    /*
     * ============================================================
     * CRIAR VENDA
     * ============================================================
     */

    @Operation(
            summary = "Criar uma venda",
            description = """
                    Registra uma nova venda para a empresa
                    do usuário autenticado.

                    A venda recebe automaticamente:
                    - usuário responsável;
                    - empresa;
                    - data da venda;
                    - total;
                    - lucro total.

                    O estoque dos produtos vendidos também é
                    atualizado automaticamente e um movimento
                    de saída de estoque é registrado.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "201",
                    description = "Venda criada com sucesso",
                    content = @Content(
                            schema = @Schema(
                                    implementation = VendaResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Dados da venda inválidos"
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário sem permissão"
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Produto ou estoque não encontrado"
            )
    })
    @PreAuthorize("hasAuthority('VENDA_CRIAR')")
    @PostMapping
    public ResponseEntity<VendaResponse> criar(

            @Valid
            @RequestBody
            VendaRequest request

    ) {

        VendaResponse response =
                vendaService.criar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    /*
     * ============================================================
     * BUSCAR VENDA POR ID
     * ============================================================
     */

    @Operation(
            summary = "Buscar venda por ID",
            description = """
                    Retorna os dados de uma venda específica.

                    A venda é procurada apenas dentro da empresa
                    associada ao usuário autenticado.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Venda encontrada",
                    content = @Content(
                            schema = @Schema(
                                    implementation = VendaResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário sem permissão"
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Venda não encontrada"
            )
    })
    @PreAuthorize("hasAuthority('VENDA_VISUALIZAR')")
    @GetMapping("/{id}")
    public ResponseEntity<VendaResponse> buscarPorId(

            @Parameter(
                    description = "ID da venda",
                    example = "1"
            )
            @PathVariable Long id

    ) {

        VendaResponse response =
                vendaService.buscarPorId(id);

        return ResponseEntity.ok(response);
    }


    /*
     * ============================================================
     * LISTAR VENDAS DA EMPRESA
     * ============================================================
     */

    @Operation(
            summary = "Listar vendas da empresa",
            description = """
                    Retorna todas as vendas pertencentes à empresa
                    do usuário autenticado.

                    As vendas são apresentadas da mais recente
                    para a mais antiga.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Vendas encontradas"
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário sem permissão"
            )
    })
    @GetMapping
    @PreAuthorize("hasAuthority('VENDA_VISUALIZAR')")
    public ResponseEntity<Page<VendaResponse>> listarPorEmpresa(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                vendaService.listarPorEmpresa(pageable)
        );
    }
}