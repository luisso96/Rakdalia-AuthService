package es.luis.almendros.authservice.infrastructure.adapters.persistence.mappers;

import es.luis.almendros.authservice.domain.model.Email;
import es.luis.almendros.authservice.domain.model.Password;
import es.luis.almendros.authservice.domain.model.User;
import es.luis.almendros.authservice.infrastructure.adapters.persistence.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper mapper;
    private UUID userId;
    private Instant now;

    @BeforeEach
    void setUp() {
        mapper = new UserMapper();
        userId = UUID.randomUUID();
        now = Instant.now();
    }

    @Test
    void shouldMapDomainToEntity() {
        User user = User.reconstruct(
                userId,
                Email.of("test@ejemplo.com"),
                "testuser",
                new Password("encodedHash123"),
                now,
                now,
                true
        );

        UserEntity entity = mapper.toEntity(user);

        assertNotNull(entity);
        assertEquals(user.getId(), entity.getId());
        assertEquals(user.getEmail().value(), entity.getEmail());
        assertEquals(user.getUsername(), entity.getUsername());
        assertEquals(user.getPassword().hashedValue(), entity.getPasswordHash());
        assertEquals(user.getCreatedAt(), entity.getCreatedAt());
        assertEquals(user.getUpdatedAt(), entity.getUpdatedAt());
        assertEquals(user.isActive(), entity.isActive());
    }

    @Test
    void shouldMapEntityToDomain() {
        UserEntity entity = new UserEntity();
        entity.setId(userId);
        entity.setEmail("test@ejemplo.com");
        entity.setUsername("testuser");
        entity.setPasswordHash("encodedHash123");
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setActive(true);

        User user = mapper.toDomain(entity);

        assertNotNull(user);
        assertEquals(entity.getId(), user.getId());
        assertEquals(entity.getEmail(), user.getEmail().value());
        assertEquals(entity.getUsername(), user.getUsername());
        assertEquals(entity.getPasswordHash(), user.getPassword().hashedValue());
        assertEquals(entity.getCreatedAt(), user.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), user.getUpdatedAt());
        assertEquals(entity.isActive(), user.isActive());
    }

    @Test
    void shouldReturnNullWhenDomainIsNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertNull(mapper.toDomain(null));
    }
}