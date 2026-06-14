package es.luis.almendros.authservice.application.ports.input;

public interface LogoutUseCase {
    void logout(LogoutCommand command);

    record LogoutCommand(String accessToken, String refreshToken) {}
}
