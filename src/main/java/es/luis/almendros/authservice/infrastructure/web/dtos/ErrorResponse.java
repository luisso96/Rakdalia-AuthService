package es.luis.almendros.authservice.infrastructure.web.dtos;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        String errorCode,
        String traceId
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(Instant.now(), status, error, message, path, null, null);
    }

    public static ErrorResponse withCode(int status, String error, String message, String path, String errorCode) {
        return new ErrorResponse(Instant.now(), status, error, message, path, errorCode, null);
    }

    public static ErrorResponse withTrace(int status, String error, String message, String path, String traceId) {
        return new ErrorResponse(Instant.now(), status, error, message, path, null, traceId);
    }
}
