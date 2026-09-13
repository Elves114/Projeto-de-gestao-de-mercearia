package WD.works.V2.exception;

public class RecursoNaoEncontradoException
        extends RuntimeException {

    public RecursoNaoEncontradoException(
            String mensagem
    ) {
        super(mensagem);
    }
}