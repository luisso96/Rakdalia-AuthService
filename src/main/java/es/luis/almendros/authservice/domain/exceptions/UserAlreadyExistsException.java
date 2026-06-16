package es.luis.almendros.authservice.domain.exceptions;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends DomainException {

    public UserAlreadyExistsException() {
        super("exception.user.already.exists", "USER_ALREADY_EXISTS", HttpStatus.CONFLICT.value());
    }
}
