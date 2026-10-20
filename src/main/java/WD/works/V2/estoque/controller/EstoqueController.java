package WD.works.V2.estoque.controller;

import WD.works.V2.configuracao.context.EmpresaContext;
import WD.works.V2.estoque.dto.EstoqueQuantidadeMinimaRequest;
import WD.works.V2.estoque.dto.EstoqueResponse;
import WD.works.V2.estoque.service.EstoqueService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "Listar estoque")
    @GetMapping
    @PreAuthorize("hasAuthority('ESTOQUE_VISUALIZAR')")
    public ResponseEntity<Page<EstoqueResponse>> listar(Pageable pageable) {
        return ResponseEntity.ok(estoqueService.listarPorEmpresa(pageable));
    }

    @Operation(summary = "Buscar estoque por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ESTOQUE_VISUALIZAR')")
    public ResponseEntity<EstoqueResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(estoqueService.buscarPorId(id));
    }

    @Operation(summary = "Buscar estoque por produto")
    @GetMapping("/produto/{produtoId}")
    @PreAuthorize("hasAuthority('ESTOQUE_VISUALIZAR')")
    public ResponseEntity<EstoqueResponse> buscarPorProduto(@PathVariable Long produtoId) {
        return ResponseEntity.ok(estoqueService.buscarPorProduto(produtoId));
    }

    @Operation(summary = "Alterar quantidade mínima")
    @PatchMapping("/produto/{produtoId}/quantidade-minima")
    @PreAuthorize("hasAuthority('ESTOQUE_AJUSTAR')")
    public ResponseEntity<EstoqueResponse> alterarQuantidadeMinima(
            @PathVariable Long produtoId,
            @Valid @RequestBody EstoqueQuantidadeMinimaRequest request
    ) {
        return ResponseEntity.ok(
                estoqueService.alterarQuantidadeMinima(
                        produtoId,
                        request.getQuantidadeMinima()
                )
        );
    }
}