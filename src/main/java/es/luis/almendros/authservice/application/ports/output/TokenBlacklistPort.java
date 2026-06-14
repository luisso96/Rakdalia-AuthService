package es.luis.almendros.authservice.application.ports.output;

public interface TokenBlacklistPort {
    void blacklistToken(String token, long ttlSeconds);
    boolean isTokenBlacklisted(String token);
}
