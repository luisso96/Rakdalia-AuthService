package es.luis.almendros.authservice.domain.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidEmailException extends DomainException {

  private static final String ERROR_CODE = "INVALID_EMAIL";

  public InvalidEmailException(String message) {
    super(message, ERROR_CODE, HttpStatus.BAD_REQUEST.value());
  }
}