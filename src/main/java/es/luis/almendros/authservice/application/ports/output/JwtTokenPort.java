package es.luis.almendros.authservice.application.ports.output;

import java.util.UUID;

public interface JwtTokenPort {
    String generateAccessToken(UUID userId, String username, String email);
    String generateRefreshToken(UUID userId);
    UUID validateTokenAndGetUserId(String token);
    UUID extractUserIdFromToken(String token);
}
