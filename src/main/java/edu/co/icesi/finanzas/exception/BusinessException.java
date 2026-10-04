package edu.co.icesi.finanzas.exception;
import org.springframework.http.HttpStatus;
public class BusinessException extends RuntimeException {
    private final HttpStatus status;
    public BusinessException(HttpStatus status, String message) { super(message); this.status = status; }
    public HttpStatus getStatus() { return status; }
    public static BusinessException missing(String entity) { return new BusinessException(HttpStatus.NOT_FOUND, entity + " no encontrado"); }
    public static BusinessException conflict(String message) { return new BusinessException(HttpStatus.CONFLICT, message); }
    public static BusinessException invalid(String message) { return new BusinessException(HttpStatus.BAD_REQUEST, message); }
}
