package WD.works.V2.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            RecursoNaoEncontradoException.class
    )
    public ResponseEntity<Map<String, Object>> tratarRecursoNaoEncontrado(
            RecursoNaoEncontradoException ex,
            HttpServletRequest request
    ) {

        return criarResposta(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );
    }


    @ExceptionHandler(
            RegraNegocioException.class
    )
    public ResponseEntity<Map<String, Object>> tratarRegraNegocio(
            RegraNegocioException ex,
            HttpServletRequest request
    ) {

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI()
        );
    }


    @ExceptionHandler(
            AcessoNegadoException.class
    )
    public ResponseEntity<Map<String, Object>> tratarAcessoNegado(
            AcessoNegadoException ex,
            HttpServletRequest request
    ) {

        return criarResposta(
                HttpStatus.FORBIDDEN,
                ex.getMessage(),
                request.getRequestURI()
        );
    }


    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<Map<String, Object>> tratarValidacao(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        Map<String, String> erros = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(erro ->
                        erros.put(
                                erro.getField(),
                                erro.getDefaultMessage()
                        )
                );

        Map<String, Object> resposta = new HashMap<>();

        resposta.put(
                "timestamp",
                LocalDateTime.now()
        );

        resposta.put(
                "status",
                HttpStatus.BAD_REQUEST.value()
        );

        resposta.put(
                "erro",
                "Dados inválidos"
        );

        resposta.put(
                "mensagem",
                "Existem erros de validação."
        );

        resposta.put(
                "caminho",
                request.getRequestURI()
        );

        resposta.put(
                "erros",
                erros
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(resposta);
    }


    private ResponseEntity<Map<String, Object>> criarResposta(
            HttpStatus status,
            String mensagem,
            String caminho
    ) {

        Map<String, Object> resposta = new HashMap<>();

        resposta.put(
                "timestamp",
                LocalDateTime.now()
        );

        resposta.put(
                "status",
                status.value()
        );

        resposta.put(
                "erro",
                status.getReasonPhrase()
        );

        resposta.put(
                "mensagem",
                mensagem
        );

        resposta.put(
                "caminho",
                caminho
        );

        return ResponseEntity
                .status(status)
                .body(resposta);
    }
}