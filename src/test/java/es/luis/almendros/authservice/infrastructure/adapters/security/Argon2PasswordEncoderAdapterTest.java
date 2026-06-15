package es.luis.almendros.authservice.infrastructure.adapters.security;

import es.luis.almendros.authservice.domain.exceptions.InvalidCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Argon2PasswordEncoderAdapterTest {

    private Argon2PasswordEncoderAdapter encoder;

    @BeforeEach
    void setUp() {
        encoder = new Argon2PasswordEncoderAdapter();
    }

    @Test
    void shouldEncodePassword() {
        String rawPassword = "password123";
        String encoded = encoder.encode(rawPassword);
        assertNotNull(encoded);
        assertNotEquals(rawPassword, encoded);
        assertTrue(encoded.startsWith("$argon2"));
    }

    @Test
    void shouldMatchCorrectPassword() {
        String rawPassword = "password123";
        String encoded = encoder.encode(rawPassword);
        boolean matches = encoder.matches(rawPassword, encoded);
        assertTrue(matches);
    }

    @Test
    void shouldNotMatchIncorrectPassword() {
        String rawPassword = "password123";
        String encoded = encoder.encode(rawPassword);
        boolean matches = encoder.matches("wrongPassword", encoded);
        assertFalse(matches);
    }

    @Test
    void shouldThrowExceptionForNullPassword() {
        assertThrows(InvalidCredentialsException.class, () -> encoder.encode(null));
        assertThrows(InvalidCredentialsException.class, () -> encoder.matches(null, "hash"));
    }

    @Test
    void shouldThrowExceptionForEmptyPassword() {
        assertThrows(InvalidCredentialsException.class, () -> encoder.encode(""));
        assertThrows(InvalidCredentialsException.class, () -> encoder.matches("", "hash"));
    }

    @Test
    void shouldReturnFalseForNullHash() {
        assertFalse(encoder.matches("password", null));
        assertFalse(encoder.matches("password", ""));
    }
}