package es.luis.almendros.authservice.application.services;

import es.luis.almendros.authservice.application.ports.input.LoginUseCase;
import es.luis.almendros.authservice.application.ports.output.JwtTokenPort;
import es.luis.almendros.authservice.application.ports.output.PasswordEncoderPort;
import es.luis.almendros.authservice.application.ports.output.UserRepositoryPort;
import es.luis.almendros.authservice.domain.exceptions.InvalidCredentialsException;
import es.luis.almendros.authservice.domain.model.Email;
import es.luis.almendros.authservice.domain.model.Password;
import es.luis.almendros.authservice.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {
    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private JwtTokenPort jwtTokenPort;

    @InjectMocks
    private LoginService loginService;

    private LoginUseCase.LoginCommand validCommand;
    private User existingUser;

    @BeforeEach
    void setUp() {
        validCommand = new LoginUseCase.LoginCommand("test@ejemplo.com", "password123");

        existingUser = User.reconstruct(
                UUID.randomUUID(),
                Email.of("test@ejemplo.com"),
                "testuser",
                new Password("encodedHash"),
                Instant.now(),
                Instant.now(),
                true
        );
    }

    @Test
    void shouldLoginSuccessfully() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTokenPort.generateAccessToken(any(), anyString(), anyString())).thenReturn("accessToken123");
        when(jwtTokenPort.generateRefreshToken(any())).thenReturn("refreshToken123");

        var response = loginService.login(validCommand);

        assertNotNull(response);
        assertEquals("accessToken123", response.accessToken());
        assertEquals("refreshToken123", response.refreshToken());
        assertEquals(existingUser.getId().toString(), response.userId());
        assertEquals(existingUser.getUsername(), response.username());

        verify(userRepository).findByEmail(any(Email.class));
        verify(passwordEncoder).matches(anyString(), anyString());
        verify(jwtTokenPort).generateAccessToken(any(), anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,() -> loginService.login(validCommand));

        verify(jwtTokenPort, never()).generateAccessToken(any(), anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,() -> loginService.login(validCommand));

        verify(jwtTokenPort, never()).generateAccessToken(any(), anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenUserIsInactive() {
        var inactiveUser = User.reconstruct(
                existingUser.getId(),
                existingUser.getEmail(),
                existingUser.getUsername(),
                existingUser.getPassword(),
                existingUser.getCreatedAt(),
                existingUser.getUpdatedAt(),
                false
        );

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(inactiveUser));

        assertThrows(InvalidCredentialsException.class,() -> loginService.login(validCommand));

        verify(jwtTokenPort, never()).generateAccessToken(any(), anyString(), anyString());
    }
}