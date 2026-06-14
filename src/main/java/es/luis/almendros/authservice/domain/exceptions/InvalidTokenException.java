package es.luis.almendros.authservice.domain.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends DomainException {

  private static final String ERROR_CODE = "INVALID_TOKEN";

  public InvalidTokenException(String message) {
    super(message, ERROR_CODE, HttpStatus.UNAUTHORIZED.value());
  }
}
