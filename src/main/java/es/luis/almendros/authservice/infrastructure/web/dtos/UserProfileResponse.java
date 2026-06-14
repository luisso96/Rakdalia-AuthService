package es.luis.almendros.authservice.infrastructure.web.dtos;

public record UserProfileResponse(
        String userId,
        String email,
        String username,
        String createdAt,
        boolean active
) {
}
