package WD.works.V2.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(
            RecursoNaoEncontradoException.class
    )
    public ResponseEntity<Map<String, Object>> tratarRecursoNaoEncontrado(
            RecursoNaoEncontradoException ex,
            HttpServletRequest request
    ) {

        log.warn(
                "Recurso não encontrado: {} - {}",
                request.getRequestURI(),
                ex.getMessage()
        );

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

        log.warn(
                "Regra de negócio violada: {} - {}",
                request.getRequestURI(),
                ex.getMessage()
        );

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

        log.warn(
                "Acesso negado: {} - {}",
                request.getRequestURI(),
                ex.getMessage()
        );

        return criarResposta(
                HttpStatus.FORBIDDEN,
                ex.getMessage(),
                request.getRequestURI()
        );
    }


    @ExceptionHandler(
            AccessDeniedException.class
    )
    public ResponseEntity<Map<String, Object>> tratarAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {

        log.warn(
                "Acesso negado pelo Spring Security: {}",
                request.getRequestURI()
        );

        return criarResposta(
                HttpStatus.FORBIDDEN,
                "Você não tem permissão para realizar esta operação.",
                request.getRequestURI()
        );
    }


    @ExceptionHandler(
            AuthenticationException.class
    )
    public ResponseEntity<Map<String, Object>> tratarAuthentication(
            AuthenticationException ex,
            HttpServletRequest request
    ) {

        log.warn(
                "Falha de autenticação: {} - {}",
                request.getRequestURI(),
                ex.getMessage()
        );

        return criarResposta(
                HttpStatus.UNAUTHORIZED,
                "Autenticação necessária ou inválida.",
                request.getRequestURI()
        );
    }


    @ExceptionHandler(
            SecurityException.class
    )
    public ResponseEntity<Map<String, Object>> tratarSecurityException(
            SecurityException ex,
            HttpServletRequest request
    ) {

        log.warn(
                "Exceção de segurança: {} - {}",
                request.getRequestURI(),
                ex.getMessage()
        );

        return criarResposta(
                HttpStatus.FORBIDDEN,
                ex.getMessage(),
                request.getRequestURI()
        );
    }


    @ExceptionHandler(
            TokenRecuperacaoException.class
    )
    public ResponseEntity<Map<String, Object>> tratarTokenRecuperacao(
            TokenRecuperacaoException ex,
            HttpServletRequest request
    ) {

        log.warn(
                "Token de recuperação inválido: {} - {}",
                request.getRequestURI(),
                ex.getMessage()
        );

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI()
        );
    }


    @ExceptionHandler(
            IllegalStateException.class
    )
    public ResponseEntity<Map<String, Object>> tratarIllegalState(
            IllegalStateException ex,
            HttpServletRequest request
    ) {

        log.warn(
                "Estado inválido para operação: {} - {}",
                request.getRequestURI(),
                ex.getMessage()
        );

        return criarResposta(
                HttpStatus.CONFLICT,
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

        Map<String, String> erros =
                new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(erro ->
                        erros.put(
                                erro.getField(),
                                erro.getDefaultMessage()
                        )
                );

        Map<String, Object> resposta =
                new HashMap<>();

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


    @ExceptionHandler(
            Exception.class
    )
    public ResponseEntity<Map<String, Object>> tratarErroInterno(
            Exception ex,
            HttpServletRequest request
    ) {

        log.error(
                "Erro interno não tratado: {}",
                request.getRequestURI(),
                ex
        );

        return criarResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno no servidor.",
                request.getRequestURI()
        );
    }


    private ResponseEntity<Map<String, Object>> criarResposta(
            HttpStatus status,
            String mensagem,
            String caminho
    ) {

        Map<String, Object> resposta =
                new HashMap<>();

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

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> tratarConflitoEstoque(
            ObjectOptimisticLockingFailureException ex,
            HttpServletRequest request
    ) {

        log.warn(
                "Conflito de concorrência no estoque: {}",
                request.getRequestURI()
        );

        return criarResposta(
                HttpStatus.CONFLICT,
                "O estoque foi alterado por outra operação. Atualize os dados e tente novamente.",
                request.getRequestURI()
        );
    }
}

