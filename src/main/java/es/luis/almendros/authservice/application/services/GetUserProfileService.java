package es.luis.almendros.authservice.application.services;

import es.luis.almendros.authservice.application.ports.input.GetUserProfileUseCase;
import es.luis.almendros.authservice.application.ports.output.UserRepositoryPort;
import es.luis.almendros.authservice.domain.exceptions.InvalidTokenException;
import org.springframework.stereotype.Service;

@Service
public class GetUserProfileService implements GetUserProfileUseCase {

    private final UserRepositoryPort userRepository;

    public GetUserProfileService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserProfileResponse getProfile(GetUserProfileCommand command) {
        var user = userRepository.findById(command.userId()).orElseThrow(() -> new InvalidTokenException("User not found"));

        return new UserProfileResponse(
                user.getId().toString(),
                user.getEmail().value(),
                user.getUsername(),
                user.getCreatedAt().toString(),
                user.isActive()
        );
    }
}
