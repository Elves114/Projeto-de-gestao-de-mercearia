package WD.works.V2.dashboard.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UltimaVendaResponse {

    private Long id;
    private LocalDateTime dataVenda;
    private BigDecimal total;
    private String usuarioNome;
}