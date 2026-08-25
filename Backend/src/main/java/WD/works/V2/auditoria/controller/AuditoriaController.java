package WD.works.V2.auditoria.controller;

import WD.works.V2.auditoria.dto.AuditoriaResponse;
import WD.works.V2.auditoria.gravidade.GravidadeAuditoria;
import WD.works.V2.auditoria.service.AuditoriaService;
import WD.works.V2.auditoria.tipo.TipoAuditoria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auditorias")
@RequiredArgsConstructor
@Tag(
        name = "Auditoria",
        description = "Consulta do histórico de ações realizadas no sistema."
)
@SecurityRequirement(name = "bearerAuth")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;


    /*
     * ============================================================
     * LISTAR TODAS AS AUDITORIAS DA EMPRESA
     * ============================================================
     */

    @Operation(
            summary = "Listar todas as auditorias",
            description = """
                    Retorna todas as auditorias pertencentes à empresa
                    do usuário autenticado.

                    Apenas usuários com permissão para visualizar todas
                    as auditorias podem utilizar esta operação.
                    """
    )


            @ApiResponses({
                    @ApiResponse(
                            responseCode = "200",
                            description = "Auditorias da empresa listadas com sucesso."
                    ),

                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuário não autenticado.",
                            content = @Content
                    ),

                    @ApiResponse(
                            responseCode = "403",
                            description = "Usuário não possui permissão.",
                            content = @Content
                    )
            })
            @GetMapping
    public ResponseEntity<Page<AuditoriaResponse>> listarPorEmpresa(
            Pageable pageable
    ) {

        Page<AuditoriaResponse> auditorias =
                auditoriaService.listarPorEmpresa(pageable);

        return ResponseEntity.ok(auditorias);
    }

    /*
     * ============================================================
     * LISTAR MINHAS AUDITORIAS
     * ============================================================
     */

    @Operation(
            summary = "Listar minhas auditorias",
            description = """
                    Retorna somente as auditorias realizadas pelo
                    usuário autenticado dentro da sua empresa.

                    Qualquer usuário autenticado pode utilizar
                    esta operação.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Auditorias do usuário autenticado listadas com sucesso."
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado.",
                    content = @Content
            )
    })
    @GetMapping("/minhas")
    public ResponseEntity<Page<AuditoriaResponse>> minhasAuditorias(
            Pageable pageable
    ) {

        Page<AuditoriaResponse> auditorias =
                auditoriaService.minhasAuditorias(
                        pageable
                );

        return ResponseEntity.ok(auditorias);
    }

    /*
     * ============================================================
     * LISTAR AUDITORIAS DE UM USUÁRIO
     * ============================================================
     */

    @Operation(
            summary = "Listar auditorias de um usuário",
            description = """
                    Retorna as auditorias associadas a um usuário.

                    ADMIN e GERENTE podem consultar as auditorias
                    de qualquer usuário da empresa.

                    FUNCIONÁRIO recebe somente as próprias auditorias,
                    independentemente do usuarioId informado.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Auditorias do usuário listadas com sucesso."
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado.",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão.",
                    content = @Content
            )
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<AuditoriaResponse>> listarPorUsuario(

            @Parameter(
                    name = "usuarioId",
                    description = "ID do usuário cujas auditorias serão consultadas.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "1"
            )
            @PathVariable Long usuarioId,

            Pageable pageable

    ) {

        Page<AuditoriaResponse> auditorias =
                auditoriaService.listarPorUsuario(
                        usuarioId,
                        pageable
                );

        return ResponseEntity.ok(auditorias);
    }


    /*
     * ============================================================
     * LISTAR POR TIPO
     * ============================================================
     */

    @Operation(
            summary = "Listar auditorias por tipo",
            description = """
                    Retorna as auditorias da empresa filtradas pelo
                    tipo da operação realizada.

                    Apenas ADMIN e GERENTE podem utilizar esta operação.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Auditorias filtradas por tipo listadas com sucesso."
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado.",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão.",
                    content = @Content
            )
    })
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<Page<AuditoriaResponse>> listarPorTipo(

            @Parameter(
                    name = "tipo",
                    description = "Tipo de operação registrada na auditoria.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "VENDA"
            )
            @PathVariable TipoAuditoria tipo,

            Pageable pageable

    ) {

        Page<AuditoriaResponse> auditorias =
                auditoriaService.listarPorTipo(
                        tipo,
                        pageable
                );

        return ResponseEntity.ok(auditorias);
    }

    /*
     * ============================================================
     * LISTAR POR GRAVIDADE
     * ============================================================
     */

    @Operation(
            summary = "Listar auditorias por gravidade",
            description = """
                Retorna as auditorias da empresa filtradas
                pelo nível de gravidade da operação.

                Os níveis disponíveis são:

                INFO:
                Eventos informativos e operações normais.

                WARNING:
                Eventos que merecem atenção, como alterações
                importantes no sistema.

                CRITICAL:
                Eventos críticos que podem representar uma
                operação de alto impacto ou uma tentativa
                suspeita.

                Apenas ADMIN e GERENTE podem utilizar esta operação.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Auditorias filtradas por gravidade listadas com sucesso."
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Gravidade inválida ou parâmetro inválido.",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado.",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão para consultar auditorias.",
                    content = @Content
            )
    })
    @GetMapping("/gravidade/{gravidade}")
    public ResponseEntity<Page<AuditoriaResponse>> listarPorGravidade(

            @Parameter(
                    name = "gravidade",
                    description = """
                        Nível de gravidade utilizado para filtrar
                        as auditorias.

                        Valores possíveis:
                        INFO, WARNING ou CRITICAL.
                        """,
                    required = true,
                    in = ParameterIn.PATH,
                    example = "CRITICAL"
            )
            @PathVariable GravidadeAuditoria gravidade,

            Pageable pageable

    ) {

        Page<AuditoriaResponse> auditorias =
                auditoriaService.listarPorGravidade(
                        gravidade,
                        pageable
                );

        return ResponseEntity.ok(auditorias);
    }
}