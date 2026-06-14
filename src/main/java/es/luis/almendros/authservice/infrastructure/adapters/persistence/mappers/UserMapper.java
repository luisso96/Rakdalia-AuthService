package es.luis.almendros.authservice.infrastructure.adapters.persistence.mappers;

import es.luis.almendros.authservice.domain.model.Email;
import es.luis.almendros.authservice.domain.model.Password;
import es.luis.almendros.authservice.domain.model.User;
import es.luis.almendros.authservice.infrastructure.adapters.persistence.entities.UserEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class UserMapper {

    public UserEntity toEntity(User domain) {
        if (domain == null) return null;

        return new UserEntity(
                domain.getId(),
                domain.getEmail().value(),
                domain.getUsername(),
                domain.getPassword().hashedValue(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.isActive()
        );
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;

        return reconstructUser(
                entity.getId(),
                entity.getEmail(),
                entity.getUsername(),
                entity.getPasswordHash(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.isActive()
        );
    }

    private User reconstructUser(UUID id, String email, String username, String password, Instant createdAt, Instant updatedAt, boolean active) {
        return User.reconstruct(id, Email.of(email), username, new Password(password), createdAt, updatedAt, active);
    }
}
