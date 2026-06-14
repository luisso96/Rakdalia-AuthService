package es.luis.almendros.authservice.application.ports.input;

import java.util.UUID;

public interface ChangePasswordUseCase {
    void changePassword(ChangePasswordCommand command);

    record ChangePasswordCommand(
            UUID userId,
            String currentPassword,
            String newPassword
    ) {}

}
