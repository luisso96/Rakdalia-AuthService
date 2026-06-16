package es.luis.almendros.authservice.domain.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidEmailException extends DomainException {

  public InvalidEmailException() {
    super("exception.email.invalid", "INVALID_EMAIL", HttpStatus.BAD_REQUEST.value());
  }
}