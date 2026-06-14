package es.luis.almendros.authservice.application.services;

import es.luis.almendros.authservice.application.ports.input.RefreshTokenUseCase;
import es.luis.almendros.authservice.application.ports.output.JwtTokenPort;
import es.luis.almendros.authservice.application.ports.output.UserRepositoryPort;
import es.luis.almendros.authservice.domain.exceptions.InvalidTokenException;
import es.luis.almendros.authservice.domain.model.User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RefreshTokenService implements RefreshTokenUseCase {

    private final JwtTokenPort jwtTokenPort;
    private final UserRepositoryPort userRepository;

    public RefreshTokenService(JwtTokenPort jwtTokenPort, UserRepositoryPort userRepository) {
        this.jwtTokenPort = jwtTokenPort;
        this.userRepository = userRepository;
    }

    @Override
    public RefreshTokenResponse refresh(RefreshTokenCommand command) {

        UUID userId = jwtTokenPort.extractUserIdFromToken(command.refreshToken());
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidTokenException("User not found"));

        if (!user.isActive()) {
            throw new InvalidTokenException("User deactivated");
        }

        String newAccessToken = jwtTokenPort.generateAccessToken(
                user.getId(),
                user.getUsername(),
                user.getEmail().value()
        );
        String newRefreshToken = jwtTokenPort.generateRefreshToken(user.getId());

        return new RefreshTokenResponse(
                newAccessToken,
                newRefreshToken,
                3600000
        );
    }
}
