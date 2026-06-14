package es.luis.almendros.authservice.application.services;

import es.luis.almendros.authservice.application.ports.input.ChangePasswordUseCase;
import es.luis.almendros.authservice.application.ports.output.PasswordEncoderPort;
import es.luis.almendros.authservice.application.ports.output.UserRepositoryPort;
import es.luis.almendros.authservice.domain.exceptions.InvalidCredentialsException;
import es.luis.almendros.authservice.domain.exceptions.WeakPasswordException;
import org.springframework.stereotype.Service;

@Service
public class ChangePasswordService implements ChangePasswordUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public ChangePasswordService(UserRepositoryPort userRepository, PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void changePassword(ChangePasswordCommand command) {
        var user = userRepository.findById(command.userId()).orElseThrow(() -> new InvalidCredentialsException("User not found"));
        boolean currentPasswordMatches = user.getPassword().matches(command.currentPassword(), passwordEncoder);

        if (!currentPasswordMatches) {
            throw new InvalidCredentialsException("Incorrect password");
        }

        if (command.currentPassword().equals(command.newPassword())) {
            throw new IllegalArgumentException("The new password must be different from the current one");
        }

        if (command.newPassword().length() < 8) {
            throw new WeakPasswordException("The new password must be at least 8 characters long");
        }

        user.changePassword(command.newPassword(), passwordEncoder);
        userRepository.save(user);
    }
}
