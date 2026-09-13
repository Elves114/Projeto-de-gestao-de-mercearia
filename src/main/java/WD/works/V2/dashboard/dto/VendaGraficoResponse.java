package WD.works.V2.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class VendaGraficoResponse {

    private LocalDate data;

    private BigDecimal valor;
}