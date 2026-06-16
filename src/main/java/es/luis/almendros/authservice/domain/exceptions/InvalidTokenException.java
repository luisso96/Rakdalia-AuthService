package es.luis.almendros.authservice.domain.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends DomainException {

  public InvalidTokenException() {
    super("exception.invalid.token", "INVALID_TOKEN", HttpStatus.UNAUTHORIZED.value());
  }
}
