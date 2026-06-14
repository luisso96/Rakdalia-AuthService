package es.luis.almendros.authservice.infrastructure.web.dtos;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn
) {
}
