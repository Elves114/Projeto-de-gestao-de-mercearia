package WD.works.V2.auditoria.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaDetalhes {

    private String dadosAntigos;

    private String dadosNovos;

    private String payload;
}