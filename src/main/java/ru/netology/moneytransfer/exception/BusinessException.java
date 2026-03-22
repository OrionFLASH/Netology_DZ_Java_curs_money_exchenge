package ru.netology.moneytransfer.exception;

import org.springframework.http.HttpStatus;

/**
 * Прикладное исключение с HTTP-кодом и числовым идентификатором ошибки для клиента.
 */
public class BusinessException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final int errorId;

    public BusinessException(String message, HttpStatus httpStatus, int errorId) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorId = errorId;
    }

    public static BusinessException badRequest(String message, int errorId) {
        return new BusinessException(message, HttpStatus.BAD_REQUEST, errorId);
    }

    public static BusinessException serverError(String message, int errorId) {
        return new BusinessException(message, HttpStatus.INTERNAL_SERVER_ERROR, errorId);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public int getErrorId() {
        return errorId;
    }
}
