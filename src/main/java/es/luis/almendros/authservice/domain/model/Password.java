package es.luis.almendros.authservice.domain.model;

import es.luis.almendros.authservice.application.ports.output.PasswordEncoderPort;
import es.luis.almendros.authservice.domain.exceptions.InvalidCredentialsException;

public record Password(String hashedValue) {

    public Password {
        if (hashedValue == null || hashedValue.isBlank()) {
            throw new InvalidCredentialsException("Has is null or empty");
        }
    }

    public static Password of(String rawPassword, PasswordEncoderPort encoder) {
        if (rawPassword == null || rawPassword.length() < 8) {
            throw new InvalidCredentialsException("Password is too short");
        }
        return new Password(encoder.encode(rawPassword));
    }

    public boolean matches(String rawPassword, PasswordEncoderPort encoder) {
        return encoder.matches(rawPassword, this.hashedValue);
    }
}
