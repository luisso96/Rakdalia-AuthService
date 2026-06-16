package es.luis.almendros.authservice.domain.exceptions;

import org.springframework.http.HttpStatus;

public class WeakPasswordException extends DomainException {

  public WeakPasswordException() {
    super("exception.password.too.short", "WEAK_PASSWORD", HttpStatus.BAD_REQUEST.value());
  }
}
