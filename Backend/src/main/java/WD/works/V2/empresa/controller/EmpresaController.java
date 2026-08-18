package WD.works.V2.empresa.controller;

import WD.works.V2.empresa.dto.EmpresaRequest;
import WD.works.V2.empresa.dto.EmpresaResponse;
import WD.works.V2.empresa.service.EmpresaService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
@Tag(
        name = "Empresas",
        description = "Operações de gestão e consulta das empresas."
)
@SecurityRequirement(name = "bearerAuth")
public class EmpresaController {

    private final EmpresaService empresaService;


    /**
     * ============================================================
     * CRIAR EMPRESA
     * ============================================================
     */

    @PostMapping
    @PreAuthorize("hasAuthority('EMPRESA_EDITAR')")
    @Operation(
            summary = "Criar empresa",
            description = "Cria uma nova empresa no sistema."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Empresa criada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = EmpresaResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados da empresa inválidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão para editar empresas"
            )
    })
    public ResponseEntity<EmpresaResponse> criar(
            @Valid @RequestBody EmpresaRequest request
    ) {

        EmpresaResponse response =
                empresaService.criar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * ============================================================
     * ATUALIZAR EMPRESA
     * ============================================================
     */

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPRESA_EDITAR')")
    @Operation(
            summary = "Atualizar empresa",
            description = "Atualiza os dados de uma empresa existente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Empresa atualizada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = EmpresaResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados da empresa inválidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão para editar empresas"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empresa não encontrada"
            )
    })
    public ResponseEntity<EmpresaResponse> atualizar(

            @Parameter(
                    description = "ID da empresa",
                    example = "1",
                    required = true
            )
            @PathVariable Long id,

            @Valid @RequestBody EmpresaRequest request
    ) {

        return ResponseEntity.ok(
                empresaService.atualizar(
                        id,
                        request
                )
        );
    }


    /**
     * ============================================================
     * ATIVAR EMPRESA
     * ============================================================
     */


    @GetMapping("/minha")
    @PreAuthorize("hasAuthority('EMPRESA_VISUALIZAR')")
    @Operation(
            summary = "Buscar minha empresa",
            description = """
                    Retorna os dados da empresa à qual
                    o usuário autenticado pertence.
                    
                    A empresa é identificada automaticamente
                    através do usuário autenticado.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Empresa encontrada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = EmpresaResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado."
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão."
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Empresa não encontrada."
            )
    })
    public ResponseEntity<EmpresaResponse> minhaEmpresa() {

        return ResponseEntity.ok(
                empresaService.minhaEmpresa()
        );
    }

    @PutMapping("/minha")
    @PreAuthorize("hasAuthority('EMPRESA_EDITAR')")
    @Operation(
            summary = "Atualizar minha empresa",
            description = """
                    Atualiza os dados da empresa à qual
                    o usuário autenticado pertence.
                    
                    A empresa é identificada automaticamente
                    através do usuário autenticado.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Empresa atualizada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = EmpresaResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Dados da empresa inválidos."
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado."
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão."
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Empresa não encontrada."
            )
    })
    public ResponseEntity<EmpresaResponse> atualizarMinhaEmpresa(

            @Valid @RequestBody EmpresaRequest request

    ) {

        return ResponseEntity.ok(
                empresaService.atualizarMinhaEmpresa(request)
        );
    }

    @PatchMapping("/minha/ativar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Ativar minha empresa",
            description = """
                    Ativa a empresa à qual o usuário autenticado pertence.
                    
                    Esta operação só pode ser realizada pelo ADMIN
                    da empresa.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Empresa ativada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = EmpresaResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado."
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Apenas o ADMIN pode realizar esta operação."
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Empresa não encontrada."
            )
    })
    public ResponseEntity<EmpresaResponse> ativarMinhaEmpresa() {

        return ResponseEntity.ok(
                empresaService.ativarMinhaEmpresa()
        );
    }

    @PatchMapping("/minha/desativar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Desativar minha empresa",
            description = """
                    Desativa a empresa à qual o usuário autenticado pertence.
                    
                    Esta operação só pode ser realizada pelo ADMIN
                    da empresa.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Empresa desativada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = EmpresaResponse.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado."
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Apenas o ADMIN pode realizar esta operação."
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Empresa não encontrada."
            )
    })
    public ResponseEntity<EmpresaResponse> desativarMinhaEmpresa() {

        return ResponseEntity.ok(
                empresaService.desativarMinhaEmpresa()
        );
    }
}