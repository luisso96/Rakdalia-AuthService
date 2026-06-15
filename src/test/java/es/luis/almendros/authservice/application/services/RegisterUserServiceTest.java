package es.luis.almendros.authservice.application.services;

import es.luis.almendros.authservice.application.ports.input.RegisterUserUseCase;
import es.luis.almendros.authservice.application.ports.output.PasswordEncoderPort;
import es.luis.almendros.authservice.application.ports.output.UserRepositoryPort;
import es.luis.almendros.authservice.domain.exceptions.UserAlreadyExistsException;
import es.luis.almendros.authservice.domain.model.Email;
import es.luis.almendros.authservice.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private RegisterUserService registerUserService;

    private RegisterUserUseCase.RegisterUserCommand validCommand;

    @BeforeEach
    void setUp() {
        validCommand = new RegisterUserUseCase.RegisterUserCommand(
                "test@ejemplo.com",
                "testuser",
                "password123"
        );
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        // Given
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        User savedUser = User.register(
                validCommand.email(),
                validCommand.username(),
                validCommand.password(),
                passwordEncoder
        );

        var userId = UUID.randomUUID();
        var userToReturn = User.reconstruct(
                userId,
                Email.of(validCommand.email()),
                validCommand.username(),
                savedUser.getPassword(),
                savedUser.getCreatedAt(),
                savedUser.getUpdatedAt(),
                true
        );

        when(userRepository.save(any(User.class))).thenReturn(userToReturn);

        var response = registerUserService.register(validCommand);

        assertNotNull(response);
        assertEquals(userId.toString(), response.userId());
        assertEquals(validCommand.email(), response.email());
        assertEquals(validCommand.username(), response.username());

        verify(userRepository).existsByEmail(any(Email.class));
        verify(userRepository).existsByUsername(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> registerUserService.register(validCommand));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> registerUserService.register(validCommand));

        verify(userRepository, never()).save(any(User.class));
    }
}