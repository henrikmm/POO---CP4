package br.com.fiap.streamfiap.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConteudoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleConteudoNaoEncontrado(ConteudoNaoEncontradoException e) {
        return respostaErro(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(CreditosInsuficientesException.class)
    public ResponseEntity<Map<String, String>> handleCreditosInsuficientes(CreditosInsuficientesException e) {
        return respostaErro(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
    }

    @ExceptionHandler(ConteudoIndisponivelException.class)
    public ResponseEntity<Map<String, String>> handleConteudoIndisponivel(ConteudoIndisponivelException e) {
        return respostaErro(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(DadosInvalidosException.class)
    public ResponseEntity<Map<String, String>> handleDadosInvalidos(DadosInvalidosException e) {
        return respostaErro(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ClassificacaoIndicativaException.class)
    public ResponseEntity<Map<String, String>> handleClassificacaoIndicativa(ClassificacaoIndicativaException e) {
        return respostaErro(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleUsuarioNaoEncontrado(UsuarioNaoEncontradoException e) {
        return respostaErro(HttpStatus.NOT_FOUND, e.getMessage());
    }

    private ResponseEntity<Map<String, String>> respostaErro(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("erro", mensagem));
    }
}
