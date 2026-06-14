package es.luis.almendros.authservice.infrastructure.web.dtos;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String userId,
        String username,
        String email,
        long expiresIn) {
}
