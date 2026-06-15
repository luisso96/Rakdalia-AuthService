package es.luis.almendros.authservice.application.services;

import es.luis.almendros.authservice.application.ports.input.ChangePasswordUseCase;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangePasswordServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private ChangePasswordService changePasswordService;

    private User existingUser;
    private ChangePasswordUseCase.ChangePasswordCommand validCommand;

    @BeforeEach
    void setUp() {
        existingUser = User.reconstruct(
                UUID.randomUUID(),
                Email.of("test@ejemplo.com"),
                "testuser",
                new Password("oldEncodedHash"),
                Instant.now(),
                Instant.now(),
                true
        );

        validCommand = new ChangePasswordUseCase.ChangePasswordCommand(
                existingUser.getId(),
                "oldPassword123",
                "newPassword456"
        );
    }

    @Test
    void shouldChangePasswordSuccessfully() {
        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("oldPassword123", "oldEncodedHash")).thenReturn(true);
        when(passwordEncoder.encode("newPassword456")).thenReturn("newEncodedHash");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        assertDoesNotThrow(() -> changePasswordService.changePassword(validCommand));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenCurrentPasswordIsWrong() {
        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("oldPassword123", "oldEncodedHash")).thenReturn(false);
        assertThrows(InvalidCredentialsException.class, () -> changePasswordService.changePassword(validCommand));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenNewPasswordIsSameAsCurrent() {
        var samePasswordCommand = new ChangePasswordUseCase.ChangePasswordCommand(
                existingUser.getId(),
                "password123",
                "password123"
        );
        assertThrows(InvalidCredentialsException.class, () -> changePasswordService.changePassword(samePasswordCommand));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.empty());
        assertThrows(InvalidCredentialsException.class, () -> changePasswordService.changePassword(validCommand));
    }

}