package es.luis.almendros.authservice.infrastructure.web.filters;

import es.luis.almendros.authservice.application.ports.output.TokenBlacklistPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistFilterTest {

    @Mock
    private TokenBlacklistPort tokenBlacklistPort;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private PrintWriter printWriter;

    @InjectMocks
    private TokenBlacklistFilter filter;

    @Test
    void shouldAllowRequestWhenNoToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        filter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
        verify(tokenBlacklistPort, never()).isTokenBlacklisted(anyString());
    }

    @Test
    void shouldAllowRequestWhenTokenNotInBlacklist() throws Exception {
        String token = "validToken123";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenBlacklistPort.isTokenBlacklisted(token)).thenReturn(false);
        filter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldRejectRequestWhenTokenInBlacklist() throws Exception {
        String token = "revokedToken123";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenBlacklistPort.isTokenBlacklisted(token)).thenReturn(true);
        when(response.getWriter()).thenReturn(printWriter);
        filter.doFilterInternal(request, response, filterChain);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        verify(printWriter).write(anyString());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void shouldHandleInvalidBearerFormat() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat");
        filter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
        verify(tokenBlacklistPort, never()).isTokenBlacklisted(anyString());
    }
}