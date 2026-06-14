package es.luis.almendros.authservice.domain.exceptions;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends DomainException {

    private static final String ERROR_CODE = "USER_ALREADY_EXISTS";

    public UserAlreadyExistsException(String message) {
        super(message, ERROR_CODE, HttpStatus.CONFLICT.value());
    }
}
