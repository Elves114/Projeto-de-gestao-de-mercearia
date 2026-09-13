package WD.works.V2.empresa.dto;

import WD.works.V2.empresa.status.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaResponse {

    private Long id;
    private String nome;
    private String nuit;
    private String email;
    private String contacto;
    private String endereco;
    private Status status;
    private LocalDateTime dataCriacao;
}