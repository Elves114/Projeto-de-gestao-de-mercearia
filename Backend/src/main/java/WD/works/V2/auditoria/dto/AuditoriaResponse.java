package WD.works.V2.auditoria.dto;

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

    private String tabela;

    private String registo;

    private String descricao;

    private LocalDateTime data;

    private Long empresaId;

    private Long usuarioId;

    private String usuarioNome;
}