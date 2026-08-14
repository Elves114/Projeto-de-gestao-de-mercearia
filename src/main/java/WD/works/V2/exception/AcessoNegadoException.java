package WD.works.V2.exception;

public class AcessoNegadoException
        extends RuntimeException {

    public AcessoNegadoException(
            String mensagem
    ) {
        super(mensagem);
    }
}