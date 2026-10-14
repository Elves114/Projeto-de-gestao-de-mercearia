package WD.works.V2.venda.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CancelarVendaRequest {

    @NotBlank(message = "O motivo é obrigatório")
    @Size(max = 500, message = "O motivo não pode ultrapassar 500 caracteres")
    private String motivo;
}