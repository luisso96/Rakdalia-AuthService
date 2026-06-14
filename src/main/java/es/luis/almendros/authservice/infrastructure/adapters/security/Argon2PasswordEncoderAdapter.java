package es.luis.almendros.authservice.infrastructure.adapters.security;

import es.luis.almendros.authservice.application.ports.output.PasswordEncoderPort;
import es.luis.almendros.authservice.domain.exceptions.InvalidCredentialsException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Argon2PasswordEncoderAdapter implements PasswordEncoderPort {

    private final PasswordEncoder passwordEncoder;

    public Argon2PasswordEncoderAdapter() {
        this.passwordEncoder = new Argon2PasswordEncoder(
                32,
                64,
                1,
                19 * 1024,
                2
        );
    }

    @Override
    public String encode(String rawPassword) {
        validateRawPassword(rawPassword);
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        validateRawPassword(rawPassword);
        if (hashedPassword == null || hashedPassword.isBlank()) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    private void validateRawPassword(String rawPassword) {
        if (rawPassword == null) {
            throw new InvalidCredentialsException("Password is null");
        }
        if (rawPassword.isBlank()) {
            throw new InvalidCredentialsException("Password is blank");
        }
    }
}
