package es.luis.almendros.authservice.application.ports.input;

import java.util.UUID;

public interface GetUserProfileUseCase {
    UserProfileResponse getProfile(GetUserProfileCommand command);

    record GetUserProfileCommand(UUID userId) {}

    record UserProfileResponse(
            String userId,
            String email,
            String username,
            String createdAt,
            boolean active
    ) {}
}
