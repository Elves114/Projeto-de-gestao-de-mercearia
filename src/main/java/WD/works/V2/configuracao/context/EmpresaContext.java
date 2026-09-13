package WD.works.V2.configuracao.context;

import WD.works.V2.empresa.entity.Empresa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmpresaContext {

    private final UsuarioContext usuarioContext;

    public Empresa getEmpresaAtual() {

        return usuarioContext
                .getUsuarioAtual()
                .getEmpresa();
    }

    public Long getEmpresaIdAtual() {

        return getEmpresaAtual().getId();
    }

    public void validarEmpresa(
            Long empresaId
    ) {

        Long empresaAtual =
                getEmpresaIdAtual();

        if (!empresaAtual.equals(empresaId)) {

            throw new SecurityException(
                    "Acesso negado à empresa."
            );
        }
    }
}