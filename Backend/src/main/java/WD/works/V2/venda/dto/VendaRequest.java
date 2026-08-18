package WD.works.V2.venda.dto;

import WD.works.V2.venda.itensVenda.dto.ItemVendaRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendaRequest {

    @NotEmpty(message = "A venda deve possuir pelo menos um item")
    @Valid
    private List<ItemVendaRequest> itens;
}