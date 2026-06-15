package es.luis.almendros.authservice.application.services;

import es.luis.almendros.authservice.application.ports.input.GetUserProfileUseCase;
import es.luis.almendros.authservice.application.ports.output.UserRepositoryPort;
import es.luis.almendros.authservice.domain.exceptions.InvalidTokenException;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserProfileServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private GetUserProfileService getUserProfileService;

    private UUID userId;
    private User existingUser;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        existingUser = User.reconstruct(
                userId,
                Email.of("test@ejemplo.com"),
                "testuser",
                new Password("encodedHash"),
                Instant.now(),
                Instant.now(),
                true
        );
    }

    @Test
    void shouldGetUserProfileSuccessfully() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        var response = getUserProfileService.getProfile(new GetUserProfileUseCase.GetUserProfileCommand(userId));
        assertNotNull(response);
        assertEquals(userId.toString(), response.userId());
        assertEquals("test@ejemplo.com", response.email());
        assertEquals("testuser", response.username());
        assertTrue(response.active());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        var command = new GetUserProfileUseCase.GetUserProfileCommand(userId);
        assertThrows(InvalidTokenException.class, () -> getUserProfileService.getProfile(command));
    }
}