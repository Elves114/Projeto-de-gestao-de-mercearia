package WD.works.V2.auditoria.dto;

import WD.works.V2.auditoria.gravidade.GravidadeAuditoria;
import WD.works.V2.auditoria.tipo.TipoAuditoria;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaResponse {

    private Long id;

    private TipoAuditoria tipo;

    private GravidadeAuditoria gravidade;

    private String tabela;

    private String registo;

    private String descricao;

    private LocalDateTime data;

    private Long empresaId;

    private Long usuarioId;

    private String usuarioNome;

    private String ip;

    private String metodo;

    private String endpoint;

    private String dadosAntigos;

    private String dadosNovos;

    private String payload;
}