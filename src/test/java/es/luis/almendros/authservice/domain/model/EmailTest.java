package es.luis.almendros.authservice.domain.model;

import es.luis.almendros.authservice.domain.exceptions.InvalidEmailException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void shouldCreateValidEmail() {
        String validEmail = "usuario@ejemplo.com";
        Email email = Email.of(validEmail);

        assertEquals("usuario@ejemplo.com", email.value());
    }

    @Test
    void shouldNormalizeEmailToLowercase() {
        Email email = Email.of("Usuario@Ejemplo.com");

        assertEquals("usuario@ejemplo.com", email.value());
    }

    @Test
    void shouldTrimEmail() {
        Email email = Email.of("  usuario@ejemplo.com  ");

        assertEquals("usuario@ejemplo.com", email.value());
    }

    @Test
    void shouldThrowExceptionForNullEmail() {
        assertThrows(InvalidEmailException.class, () -> Email.of(null));
    }

    @Test
    void shouldThrowExceptionForEmptyEmail() {
        assertThrows(InvalidEmailException.class, () -> Email.of(""));
        assertThrows(InvalidEmailException.class, () -> Email.of("   "));
    }

    @Test
    void shouldThrowExceptionForInvalidFormat() {
        assertThrows(InvalidEmailException.class, () -> Email.of("usuario@"));
        assertThrows(InvalidEmailException.class, () -> Email.of("@ejemplo.com"));
        assertThrows(InvalidEmailException.class, () -> Email.of("usuario.ejemplo.com"));
        assertThrows(InvalidEmailException.class, () -> Email.of("usuario@ejemplo@com"));
    }
}