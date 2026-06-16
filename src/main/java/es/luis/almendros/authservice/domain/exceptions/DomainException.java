package es.luis.almendros.authservice.domain.exceptions;

import lombok.Getter;

@Getter
public abstract class DomainException extends RuntimeException {

    private final String errorCode;
    private final int httpStatus;
    private final String messageKey;
    private final Object[] args;

    protected DomainException(String messageKey, String errorCode, int httpStatus, Object... args) {
        super(messageKey);
        this.messageKey = messageKey;
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.args = args;
    }

}
