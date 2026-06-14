package es.luis.almendros.authservice.domain.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends DomainException {

    private static final String ERROR_CODE = "INVALID_CREDENTIALS";

    public InvalidCredentialsException(String message) {
        super(message, ERROR_CODE, HttpStatus.UNAUTHORIZED.value());
    }
}
