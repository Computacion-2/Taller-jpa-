package edu.co.icesi.finanzas.exception;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ProblemDetail> business(BusinessException ex) { return problem(ex.getStatus(), ex.getMessage()); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ProblemDetail> validation(MethodArgumentNotValidException ex) {
        return problem(HttpStatus.BAD_REQUEST, ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage()).sorted().distinct().reduce((a,b) -> a + "; " + b).orElse("Datos invalidos"));
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
        ConstraintViolationException.class, IllegalArgumentException.class})
    ResponseEntity<ProblemDetail> malformed(Exception ex) { return problem(HttpStatus.BAD_REQUEST, "Solicitud invalida: revise tipos, campos y valores permitidos"); }
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ProblemDetail> integrity(DataIntegrityViolationException ex) {
        return problem(HttpStatus.CONFLICT, "La operacion viola una restriccion o el registro conserva datos asociados");
    }
    private ResponseEntity<ProblemDetail> problem(HttpStatus status, String detail) {
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, detail));
    }
}
