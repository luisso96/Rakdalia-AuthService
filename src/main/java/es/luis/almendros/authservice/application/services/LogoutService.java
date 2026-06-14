package es.luis.almendros.authservice.application.services;

import es.luis.almendros.authservice.application.ports.input.LogoutUseCase;
import es.luis.almendros.authservice.application.ports.output.TokenBlacklistPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LogoutService implements LogoutUseCase {

    private final TokenBlacklistPort tokenBlacklistPort;

    @Value("${jwt.expiration}")
    private long accessTokenExpirationMs;
    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpirationMs;

    public LogoutService(TokenBlacklistPort tokenBlacklistPort) {
        this.tokenBlacklistPort = tokenBlacklistPort;
    }

    @Override
    public void logout(LogoutCommand command) {
        if (command.accessToken() != null && !command.accessToken().isBlank()) {
            long accessTtlSeconds = accessTokenExpirationMs / 1000;
            tokenBlacklistPort.blacklistToken(command.accessToken(), accessTtlSeconds);
        }

        if (command.refreshToken() != null && !command.refreshToken().isBlank()) {
            long refreshTtlSeconds = refreshTokenExpirationMs / 1000;
            tokenBlacklistPort.blacklistToken(command.refreshToken(), refreshTtlSeconds);
        }
    }
}
