package es.luis.almendros.authservice.application.services;

import es.luis.almendros.authservice.application.ports.input.RefreshTokenUseCase;
import es.luis.almendros.authservice.application.ports.output.JwtTokenPort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private JwtTokenPort jwtTokenPort;

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

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
    void shouldRefreshTokensSuccessfully() {
        String oldRefreshToken = "oldRefreshToken123";
        when(jwtTokenPort.extractUserIdFromToken(oldRefreshToken)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(jwtTokenPort.generateAccessToken(userId, existingUser.getUsername(), existingUser.getEmail().value()))
                .thenReturn("newAccessToken");
        when(jwtTokenPort.generateRefreshToken(userId)).thenReturn("newRefreshToken");
        var response = refreshTokenService.refresh(new RefreshTokenUseCase.RefreshTokenCommand(oldRefreshToken));
        assertNotNull(response);
        assertEquals("newAccessToken", response.accessToken());
        assertEquals("newRefreshToken", response.refreshToken());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(jwtTokenPort.extractUserIdFromToken(any())).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        RefreshTokenUseCase.RefreshTokenCommand command = new RefreshTokenUseCase.RefreshTokenCommand("token");
        assertThrows(InvalidTokenException.class, () -> refreshTokenService.refresh(command));
    }

    @Test
    void shouldThrowExceptionWhenUserIsInactive() {
        var inactiveUser = User.reconstruct(
                userId,
                existingUser.getEmail(),
                existingUser.getUsername(),
                existingUser.getPassword(),
                existingUser.getCreatedAt(),
                existingUser.getUpdatedAt(),
                false
        );
        when(jwtTokenPort.extractUserIdFromToken(any())).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(inactiveUser));
        var command = new RefreshTokenUseCase.RefreshTokenCommand("token");
        assertThrows(InvalidTokenException.class, () -> refreshTokenService.refresh(command));
    }
}