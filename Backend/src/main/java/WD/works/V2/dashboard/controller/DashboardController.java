package WD.works.V2.dashboard.controller;

import WD.works.V2.dashboard.dto.DashboardResponse;
import WD.works.V2.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(
        name = "Dashboard",
        description = "Informações gerais da empresa"
)
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;


    @Operation(
            summary = "Consultar dashboard",
            description = """
                    Retorna os principais indicadores
                    da empresa do usuário autenticado.

                    Inclui informações sobre:
                    - produtos;
                    - stock;
                    - alertas;
                    - vendas;
                    - lucro.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Dashboard obtido com sucesso"
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
    @PreAuthorize("hasAuthority('DASHBOARD_VISUALIZAR')")
    public ResponseEntity<DashboardResponse> obterDashboard(
            @RequestParam(defaultValue = "7") int periodo
    ) {

        return ResponseEntity.ok(
                dashboardService.obterDashboard(periodo)
        );
    }
}

