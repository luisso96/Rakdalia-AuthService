package es.luis.almendros.authservice.domain.model;

import es.luis.almendros.authservice.application.ports.output.PasswordEncoderPort;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class User {
    private final UUID id;
    private final Email email;
    private final String username;
    private Password password;
    private final Instant createdAt;
    private Instant updatedAt;
    private boolean active;

    private User(UUID id, Email email, String username, Password password,Instant createdAt, Instant updatedAt, boolean active) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.active = active;
    }

    public static User register(String email, String username, String rawPassword, PasswordEncoderPort encoder) {
        return new User(
                UUID.randomUUID(),
                Email.of(email),
                username,
                Password.of(rawPassword, encoder),
                Instant.now(),
                Instant.now(),
                true
        );
    }

    public static User reconstruct(UUID id, Email email, String username, Password password, Instant createdAt, Instant updatedAt, boolean active){
        return new User(id, email, username, password, createdAt, updatedAt, active);
    }

    public void changePassword(String newPassword, PasswordEncoderPort encoder) {
        this.password = Password.of(newPassword, encoder);
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        if (this.active) {
            this.active = false;
            this.updatedAt = Instant.now();
        }
    }

    public void activate() {
        if (!this.active) {
            this.active = true;
            this.updatedAt = Instant.now();
        }
    }
}
