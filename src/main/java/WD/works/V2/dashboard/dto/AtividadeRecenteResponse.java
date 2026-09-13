package WD.works.V2.dashboard.dto;

import WD.works.V2.auditoria.gravidade.GravidadeAuditoria;
import WD.works.V2.auditoria.tipo.TipoAuditoria;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AtividadeRecenteResponse {

    private TipoAuditoria tipo;
    private GravidadeAuditoria gravidade;
    private String descricao;
    private LocalDateTime data;
    private String usuarioNome;
}