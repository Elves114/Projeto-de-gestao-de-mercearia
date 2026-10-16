package WD.works.V2.relatorios.controller;

import WD.works.V2.relatorios.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
@Tag(name = "Relatórios", description = "Exportação de dados em CSV")
@SecurityRequirement(name = "bearerAuth")
public class RelatorioController {

    private final RelatorioService relatorioService;

    @Operation(summary = "Exportar vendas em CSV")
    @GetMapping(value = "/vendas.csv", produces = "text/csv; charset=UTF-8")
    @PreAuthorize("hasAuthority('VENDA_VISUALIZAR')")
    public ResponseEntity<byte[]> vendasCsv(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fim
    ) {
        byte[] csv = relatorioService.gerarCsvVendas(inicio, fim);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"vendas.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv);
    }

    @Operation(summary = "Exportar stock em CSV")
    @GetMapping(value = "/stock.csv", produces = "text/csv; charset=UTF-8")
    @PreAuthorize("hasAuthority('ESTOQUE_VISUALIZAR')")
    public ResponseEntity<byte[]> stockCsv() {
        byte[] csv = relatorioService.gerarCsvStock();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"stock.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv);
    }

    @Operation(summary = "Exportar movimentos de stock em CSV")
    @GetMapping(value = "/movimentos.csv", produces = "text/csv; charset=UTF-8")
    @PreAuthorize("hasAuthority('ESTOQUE_VISUALIZAR')")
    public ResponseEntity<byte[]> movimentosCsv(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fim
    ) {
        byte[] csv = relatorioService.gerarCsvMovimentos(inicio, fim);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"movimentos.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv);
    }

    @Operation(summary = "Exportar auditoria em CSV")
    @GetMapping(value = "/auditoria.csv", produces = "text/csv; charset=UTF-8")
    @PreAuthorize("hasAuthority('AUDITORIA_VISUALIZAR')")
    public ResponseEntity<byte[]> auditoriaCsv(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fim
    ) {
        byte[] csv = relatorioService.gerarCsvAuditoria(inicio, fim);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"auditoria.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv);
    }
}