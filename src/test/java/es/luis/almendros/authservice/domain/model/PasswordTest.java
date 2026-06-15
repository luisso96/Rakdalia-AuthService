package es.luis.almendros.authservice.domain.model;

import es.luis.almendros.authservice.application.ports.output.PasswordEncoderPort;
import es.luis.almendros.authservice.domain.exceptions.InvalidCredentialsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordTest {

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Test
    void shouldCreatePasswordFromRaw() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedHash123");
        Password password = Password.of("password123", passwordEncoder);
        assertNotNull(password);
        assertEquals("encodedHash123", password.hashedValue());
    }

    @Test
    void shouldThrowExceptionForShortPassword() {
        assertThrows(InvalidCredentialsException.class, () -> Password.of("123", passwordEncoder));
    }

    @Test
    void shouldThrowExceptionForNullPassword() {
        assertThrows(InvalidCredentialsException.class, () -> Password.of(null, passwordEncoder));
    }

    @Test
    void shouldMatchPassword() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedHash123");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        Password password = Password.of("password123", passwordEncoder);
        assertTrue(password.matches("password123", passwordEncoder));
    }

    @Test
    void shouldNotMatchIncorrectPassword() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedHash123");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        Password password = Password.of("password123", passwordEncoder);
        assertFalse(password.matches("wrongpassword", passwordEncoder));
    }
}