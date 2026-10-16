package WD.works.V2.caixa.controller;

import WD.works.V2.caixa.dto.FechoCaixaResponse;
import WD.works.V2.caixa.service.CaixaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/caixa")
@RequiredArgsConstructor
@Tag(name = "Caixa", description = "Fecho de caixa e resumo diário")
@SecurityRequirement(name = "bearerAuth")
public class CaixaController {

    private final CaixaService caixaService;

    @Operation(
            summary = "Fecho de caixa do dia",
            description = "Retorna o resumo completo de vendas de um dia específico."
    )
    @GetMapping("/fecho")
    @PreAuthorize("hasAuthority('VENDA_VISUALIZAR')")
    public ResponseEntity<FechoCaixaResponse> fecho(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate data
    ) {
        return ResponseEntity.ok(caixaService.fechoDoDia(data));
    }
}