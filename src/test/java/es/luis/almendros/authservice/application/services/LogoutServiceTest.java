package es.luis.almendros.authservice.application.services;

import es.luis.almendros.authservice.application.ports.input.LogoutUseCase;
import es.luis.almendros.authservice.application.ports.output.TokenBlacklistPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock
    private TokenBlacklistPort tokenBlacklistPort;

    @InjectMocks
    private LogoutService logoutService;

    @Test
    void shouldBlacklistBothTokens() {
        ReflectionTestUtils.setField(logoutService, "accessTokenExpirationMs", 3600000L);
        ReflectionTestUtils.setField(logoutService, "refreshTokenExpirationMs", 604800000L);
        String accessToken = "accessToken123";
        String refreshToken = "refreshToken456";
        logoutService.logout(new LogoutUseCase.LogoutCommand(accessToken, refreshToken));
        verify(tokenBlacklistPort, times(1)).blacklistToken(accessToken, 3600);
        verify(tokenBlacklistPort, times(1)).blacklistToken(refreshToken, 604800);
        verifyNoMoreInteractions(tokenBlacklistPort);
    }

    @Test
    void shouldBlacklistOnlyAccessTokenWhenRefreshTokenIsNull() {
        ReflectionTestUtils.setField(logoutService, "accessTokenExpirationMs", 3600000L);
        ReflectionTestUtils.setField(logoutService, "refreshTokenExpirationMs", 604800000L);
        String accessToken = "accessToken123";
        logoutService.logout(new LogoutUseCase.LogoutCommand(accessToken, null));
        verify(tokenBlacklistPort, times(1)).blacklistToken(accessToken, 3600);
        verifyNoMoreInteractions(tokenBlacklistPort);
    }

    @Test
    void shouldBlacklistOnlyRefreshTokenWhenAccessTokenIsNull() {
        ReflectionTestUtils.setField(logoutService, "accessTokenExpirationMs", 3600000L);
        ReflectionTestUtils.setField(logoutService, "refreshTokenExpirationMs", 604800000L);
        String refreshToken = "refreshToken456";
        logoutService.logout(new LogoutUseCase.LogoutCommand(null, refreshToken));
        verify(tokenBlacklistPort, times(1)).blacklistToken(refreshToken, 604800);
        verifyNoMoreInteractions(tokenBlacklistPort);
    }

    @Test
    void shouldDoNothingWhenBothTokensAreNull() {
        logoutService.logout(new LogoutUseCase.LogoutCommand(null, null));
        verifyNoInteractions(tokenBlacklistPort);
    }
}