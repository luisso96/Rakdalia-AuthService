package es.luis.almendros.authservice.infrastructure.adapters.security;

import static org.junit.jupiter.api.Assertions.*;

import es.luis.almendros.authservice.domain.exceptions.InvalidTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;


class JwtTokenAdapterTest {

    private JwtTokenAdapter jwtTokenAdapter;
    private final String secret = "abc123def456ghi789jkl012mno345pqr678"; // 32 chars
    private final long accessExpiration = 3600000; // 1 hour
    private final long refreshExpiration = 604800000; // 7 days

    @BeforeEach
    void setUp() {
        jwtTokenAdapter = new JwtTokenAdapter(secret, accessExpiration, refreshExpiration);
    }

    @Test
    void shouldGenerateAccessToken() {
        UUID userId = UUID.randomUUID();
        String username = "testuser";
        String email = "test@ejemplo.com";

        String token = jwtTokenAdapter.generateAccessToken(userId, username, email);
        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void shouldGenerateRefreshToken() {
        UUID userId = UUID.randomUUID();
        String token = jwtTokenAdapter.generateRefreshToken(userId);
        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void shouldValidateAndGetUserIdFromAccessToken() {
        UUID userId = UUID.randomUUID();
        String username = "testuser";
        String email = "test@ejemplo.com";
        String token = jwtTokenAdapter.generateAccessToken(userId, username, email);
        UUID extractedUserId = jwtTokenAdapter.validateTokenAndGetUserId(token);
        assertEquals(userId, extractedUserId);
    }

    @Test
    void shouldThrowExceptionWhenRefreshTokenUsedAsAccessToken() {
        UUID userId = UUID.randomUUID();
        String refreshToken = jwtTokenAdapter.generateRefreshToken(userId);
        assertThrows(InvalidTokenException.class, () -> jwtTokenAdapter.validateTokenAndGetUserId(refreshToken));
    }

    @Test
    void shouldExtractUserIdFromToken() {
        UUID userId = UUID.randomUUID();
        String token = jwtTokenAdapter.generateAccessToken(userId, "user", "email@test.com");
        UUID extractedUserId = jwtTokenAdapter.extractUserIdFromToken(token);
        assertEquals(userId, extractedUserId);
    }

    @Test
    void shouldThrowExceptionForInvalidToken() {
        String invalidToken = "invalid.token.string";
        assertThrows(InvalidTokenException.class, () -> jwtTokenAdapter.validateTokenAndGetUserId(invalidToken));
    }

    @Test
    void shouldThrowExceptionForMalformedToken() {
        String malformedToken = "eyJhbGciOiJIUzUxMiJ9.malformed";
        assertThrows(InvalidTokenException.class, () -> jwtTokenAdapter.validateTokenAndGetUserId(malformedToken));
    }
}