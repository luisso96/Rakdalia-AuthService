package es.luis.almendros.authservice.application.ports.output;

import es.luis.almendros.authservice.domain.model.Email;
import es.luis.almendros.authservice.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(Email email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(Email email);
    boolean existsByUsername(String username);
}
