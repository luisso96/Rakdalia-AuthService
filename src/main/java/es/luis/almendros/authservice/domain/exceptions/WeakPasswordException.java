package es.luis.almendros.authservice.domain.exceptions;

import org.springframework.http.HttpStatus;

public class WeakPasswordException extends DomainException {
  private static final String ERROR_CODE = "WEAK_PASSWORD";

  public WeakPasswordException(String message) {
    super(message, ERROR_CODE, HttpStatus.BAD_REQUEST.value());
  }
}
