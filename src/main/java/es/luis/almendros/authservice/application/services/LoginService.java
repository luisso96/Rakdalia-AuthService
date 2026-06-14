package es.luis.almendros.authservice.application.services;

import es.luis.almendros.authservice.application.ports.input.LoginUseCase;
import es.luis.almendros.authservice.application.ports.output.JwtTokenPort;
import es.luis.almendros.authservice.application.ports.output.PasswordEncoderPort;
import es.luis.almendros.authservice.application.ports.output.UserRepositoryPort;
import es.luis.almendros.authservice.domain.exceptions.InvalidCredentialsException;
import es.luis.almendros.authservice.domain.model.Email;
import es.luis.almendros.authservice.domain.model.User;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements LoginUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtTokenPort jwtTokenPort;

    public LoginService(UserRepositoryPort userRepository, PasswordEncoderPort passwordEncoder, JwtTokenPort jwtTokenPort) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenPort = jwtTokenPort;
    }

    @Override
    public LoginResponse login(LoginCommand command) {
        User user = userRepository.findByEmail(Email.of(command.email())).orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!user.isActive()) {
            throw new InvalidCredentialsException("User deactivated");
        }

        if (!user.getPassword().matches(command.password(), passwordEncoder)) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String accessToken = jwtTokenPort.generateAccessToken(
                user.getId(),
                user.getUsername(),
                user.getEmail().value()
        );
        String refreshToken = jwtTokenPort.generateRefreshToken(user.getId());

        return new LoginResponse(
                accessToken,
                refreshToken,
                user.getId().toString(),
                user.getUsername(),
                user.getEmail().value(),
                3600000
        );

    }
}
