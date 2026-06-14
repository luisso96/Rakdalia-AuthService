package es.luis.almendros.authservice.application.ports.input;

public interface RefreshTokenUseCase {

    RefreshTokenResponse refresh(RefreshTokenCommand command);

    record RefreshTokenCommand(String refreshToken) {
    }

    record RefreshTokenResponse(
            String accessToken,
            String refreshToken,
            long expiresIn
    ) {
    }
}
