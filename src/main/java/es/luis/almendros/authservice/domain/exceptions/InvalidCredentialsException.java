package es.luis.almendros.authservice.domain.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("exception.invalid.credentials", "INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED.value());
    }
}
