package es.luis.almendros.authservice.infrastructure.adapters.persistence;

import es.luis.almendros.authservice.domain.model.Email;
import es.luis.almendros.authservice.domain.model.Password;
import es.luis.almendros.authservice.domain.model.User;
import es.luis.almendros.authservice.infrastructure.adapters.persistence.entities.UserEntity;
import es.luis.almendros.authservice.infrastructure.adapters.persistence.mappers.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaUserRepositoryAdapterTest {

    @Mock
    private JpaUserRepository jpaRepository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private JpaUserRepositoryAdapter adapter;

    private UUID userId;
    private User domainUser;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        Instant now = Instant.now();

        domainUser = User.reconstruct(
                userId,
                Email.of("test@ejemplo.com"),
                "testuser",
                new Password("encodedHash"),
                now,
                now,
                true
        );

        userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail("test@ejemplo.com");
        userEntity.setUsername("testuser");
        userEntity.setPasswordHash("encodedHash");
        userEntity.setCreatedAt(now);
        userEntity.setUpdatedAt(now);
        userEntity.setActive(true);
    }

    @Test
    void shouldSaveUser() {
        when(mapper.toEntity(domainUser)).thenReturn(userEntity);
        when(jpaRepository.save(userEntity)).thenReturn(userEntity);
        when(mapper.toDomain(userEntity)).thenReturn(domainUser);
        User result = adapter.save(domainUser);
        assertNotNull(result);
        assertEquals(domainUser.getId(), result.getId());
        assertEquals(domainUser.getEmail().value(), result.getEmail().value());
        verify(jpaRepository).save(userEntity);
    }

    @Test
    void shouldFindById() {
        when(jpaRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(mapper.toDomain(userEntity)).thenReturn(domainUser);
        Optional<User> result = adapter.findById(userId);
        assertTrue(result.isPresent());
        assertEquals(domainUser.getId(), result.get().getId());
    }

    @Test
    void shouldReturnEmptyWhenFindByIdNotFound() {
        when(jpaRepository.findById(userId)).thenReturn(Optional.empty());
        Optional<User> result = adapter.findById(userId);
        assertFalse(result.isPresent());
    }

    @Test
    void shouldFindByEmail() {
        String email = "test@ejemplo.com";
        when(jpaRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(mapper.toDomain(userEntity)).thenReturn(domainUser);
        Optional<User> result = adapter.findByEmail(Email.of(email));
        assertTrue(result.isPresent());
        assertEquals(domainUser.getEmail().value(), result.get().getEmail().value());
    }

    @Test
    void shouldFindByUsername() {
        String username = "testuser";
        when(jpaRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
        when(mapper.toDomain(userEntity)).thenReturn(domainUser);
        Optional<User> result = adapter.findByUsername(username);
        assertTrue(result.isPresent());
        assertEquals(domainUser.getUsername(), result.get().getUsername());
    }

    @Test
    void shouldCheckExistsByEmail() {
        String email = "test@ejemplo.com";
        when(jpaRepository.existsByEmail(email)).thenReturn(true);
        boolean exists = adapter.existsByEmail(Email.of(email));
        assertTrue(exists);
    }

    @Test
    void shouldCheckExistsByUsername() {
        String username = "testuser";
        when(jpaRepository.existsByUsername(username)).thenReturn(true);
        boolean exists = adapter.existsByUsername(username);
        assertTrue(exists);
    }
}