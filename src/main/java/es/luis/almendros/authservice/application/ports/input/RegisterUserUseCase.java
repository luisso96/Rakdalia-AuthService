package es.luis.almendros.authservice.application.ports.input;

public interface RegisterUserUseCase {
    RegisterUserResponse register(RegisterUserCommand command);

    record RegisterUserCommand(String email, String username, String password) {}
    record RegisterUserResponse(String userId, String email, String username) {}
}
