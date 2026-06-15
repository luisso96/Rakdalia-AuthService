package es.luis.almendros.authservice.infrastructure.adapters.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisTokenBlacklistAdapterTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private RedisTokenBlacklistAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RedisTokenBlacklistAdapter(redisTemplate);
    }

    @Test
    void shouldBlacklistToken() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String token = "testToken123";
        long ttlSeconds = 3600;
        adapter.blacklistToken(token, ttlSeconds);
        verify(valueOperations).set(eq("blacklist:testToken123"), eq("revoked"), any(Duration.class));
    }

    @Test
    void shouldReturnTrueWhenTokenIsBlacklisted() {
        String token = "blacklistedToken";
        when(redisTemplate.hasKey("blacklist:" + token)).thenReturn(true);
        boolean result = adapter.isTokenBlacklisted(token);
        assertTrue(result);
        verify(redisTemplate).hasKey("blacklist:" + token);
        verify(redisTemplate, never()).opsForValue();
    }

    @Test
    void shouldReturnFalseWhenTokenIsNotBlacklisted() {
        String token = "validToken";
        when(redisTemplate.hasKey("blacklist:" + token)).thenReturn(false);
        boolean result = adapter.isTokenBlacklisted(token);
        assertFalse(result);
        verify(redisTemplate).hasKey("blacklist:" + token);
        verify(redisTemplate, never()).opsForValue();
    }
}