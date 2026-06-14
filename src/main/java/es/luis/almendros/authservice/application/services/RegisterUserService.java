package es.luis.almendros.authservice.application.services;

import es.luis.almendros.authservice.application.ports.input.RegisterUserUseCase;
import es.luis.almendros.authservice.application.ports.output.PasswordEncoderPort;
import es.luis.almendros.authservice.application.ports.output.UserRepositoryPort;
import es.luis.almendros.authservice.domain.exceptions.UserAlreadyExistsException;
import es.luis.almendros.authservice.domain.model.Email;
import es.luis.almendros.authservice.domain.model.User;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public RegisterUserService(UserRepositoryPort userRepository, PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegisterUserResponse register(RegisterUserCommand command) {

        if (userRepository.existsByEmail(Email.of(command.email()))){
            throw new UserAlreadyExistsException("This email is already registered.");
        }

        if (userRepository.existsByUsername(command.username())){
            throw new UserAlreadyExistsException("A user with that name already exists.");
        }

        User newUser = User.register(
                command.email(),
                command.username(),
                command.password(),
                passwordEncoder
        );

        User savedUser = userRepository.save(newUser);

        return new RegisterUserResponse(
                savedUser.getId().toString(),
                savedUser.getEmail().value(),
                savedUser.getUsername()
                );
    }
}
