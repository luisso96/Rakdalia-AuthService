package es.luis.almendros.authservice.infrastructure.web.handlers;

import es.luis.almendros.authservice.application.services.MessageService;
import es.luis.almendros.authservice.domain.exceptions.InvalidCredentialsException;
import es.luis.almendros.authservice.domain.exceptions.InvalidTokenException;
import es.luis.almendros.authservice.domain.exceptions.UserAlreadyExistsException;
import es.luis.almendros.authservice.infrastructure.web.dtos.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestComponent
class GlobalExceptionHandlerTest {

    @Mock
    private MessageService messageService;
    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler(messageService);
        java.lang.reflect.Field field = null;
        try {
            field = GlobalExceptionHandler.class.getDeclaredField("activeProfile");
            field.setAccessible(true);
            field.set(handler, "dev");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldHandleUserAlreadyExistsException() {
        when(request.getRequestURI()).thenReturn("/auth/register");
        UserAlreadyExistsException ex = new UserAlreadyExistsException();

        ResponseEntity<ErrorResponse> response = handler.handleDomainException(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().status());
        assertEquals("/auth/register", response.getBody().path());
        assertEquals("USER_ALREADY_EXISTS", response.getBody().errorCode());
    }

    @Test
    void shouldHandleInvalidCredentialsException() {
        when(request.getRequestURI()).thenReturn("/auth/login");
        InvalidCredentialsException ex = new InvalidCredentialsException();

        ResponseEntity<ErrorResponse> response = handler.handleDomainException(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().status());
        assertEquals("INVALID_CREDENTIALS", response.getBody().errorCode());
    }

    @Test
    void shouldHandleInvalidTokenException() {
        when(request.getRequestURI()).thenReturn("/auth/refresh");
        InvalidTokenException ex = new InvalidTokenException();

        ResponseEntity<ErrorResponse> response = handler.handleDomainException(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().status());
        assertEquals("INVALID_TOKEN", response.getBody().errorCode());
    }

    @Test
    void shouldHandleValidationExceptions() {
        when(request.getRequestURI()).thenReturn("/auth/register");

        FieldError fieldError = new FieldError("registerRequest", "email", "El email es obligatorio");
        BindingResult bindingResult = org.mockito.Mockito.mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                org.mockito.Mockito.mock(org.springframework.core.MethodParameter.class),
                bindingResult
        );

        ResponseEntity<ErrorResponse> response = handler.handleValidationExceptions(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Validation Error", response.getBody().error());
        assertEquals("/auth/register", response.getBody().path());
    }

    @Test
    void shouldHandleGenericException() {
        when(request.getRequestURI()).thenReturn("/auth/test");
        Exception ex = new RuntimeException("Error inesperado");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().status());
        assertNotNull(response.getBody().traceId());
    }
}