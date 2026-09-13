package WD.works.V2.auditoria.dto;

import WD.works.V2.auditoria.tipo.TipoAuditoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaRequest {

    @NotNull(message = "O tipo da auditoria é obrigatório")
    private TipoAuditoria tipo;

    @NotBlank(message = "A tabela é obrigatória")
    @Size(
            max = 100,
            message = "A tabela não pode ultrapassar 100 caracteres"
    )
    private String tabela;

    @NotBlank(message = "O registro é obrigatório")
    @Size(
            max = 100,
            message = "O registro não pode ultrapassar 100 caracteres"
    )
    private String registo;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(
            max = 1000,
            message = "A descrição não pode ultrapassar 1000 caracteres"
    )
    private String descricao;
}