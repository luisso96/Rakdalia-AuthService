package es.luis.almendros.authservice.infrastructure.web.dtos;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token is required") String refreshToken
) {
}
