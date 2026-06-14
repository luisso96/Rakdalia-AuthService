package es.luis.almendros.authservice.application.ports.input;


public interface LoginUseCase {

    LoginResponse login(LoginCommand command);

    record LoginCommand(String email, String password) {
    }

    record LoginResponse(
            String accessToken,
            String refreshToken,
            String userId,
            String username,
            String email,
            long expiresIn) {
    }
}
