package WD.works.V2.alertaStock.event;

import WD.works.V2.estoque.entity.Estoque;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockAlteradoEvent {
    private final Estoque estoque;
}