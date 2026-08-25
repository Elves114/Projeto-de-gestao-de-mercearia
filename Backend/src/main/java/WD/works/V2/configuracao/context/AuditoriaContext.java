package WD.works.V2.configuracao.context;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@Getter
public class AuditoriaContext {

    private String ip;
    private String metodo;
    private String endpoint;

    public void definir(
            String ip,
            String metodo,
            String endpoint
    ) {
        this.ip = ip;
        this.metodo = metodo;
        this.endpoint = endpoint;
    }
}