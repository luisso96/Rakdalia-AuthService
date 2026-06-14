package es.luis.almendros.authservice.infrastructure.web.dtos;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
        @NotBlank(message = "Access token is required") String accessToken,
        String refreshToken
) {
}
