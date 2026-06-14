package es.luis.almendros.authservice.domain.exceptions;

import lombok.Getter;

@Getter
public class TechnicalException extends RuntimeException {

    private final String errorCode;

    public TechnicalException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "TECHNICAL_ERROR";
    }

    public TechnicalException(String message) {
        super(message);
        this.errorCode = "TECHNICAL_ERROR";
    }

}
