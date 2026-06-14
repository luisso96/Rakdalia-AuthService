package es.luis.almendros.authservice.infrastructure.web.dtos;

public record RegisterResponse(
        String userId,
        String email,
        String username,
        String message
) {
    public static RegisterResponse of(String userId, String email, String username) {
        return new RegisterResponse(userId, email, username, "User successfully registered");
    }
}
