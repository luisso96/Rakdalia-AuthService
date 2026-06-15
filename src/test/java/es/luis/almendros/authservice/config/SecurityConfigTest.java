package es.luis.almendros.authservice.config;

import es.luis.almendros.authservice.infrastructure.web.AuthController;
import es.luis.almendros.authservice.infrastructure.web.filters.TokenBlacklistFilter;
import es.luis.almendros.authservice.application.ports.input.RegisterUserUseCase;
import es.luis.almendros.authservice.application.ports.input.LoginUseCase;
import es.luis.almendros.authservice.application.ports.input.RefreshTokenUseCase;
import es.luis.almendros.authservice.application.ports.input.LogoutUseCase;
import es.luis.almendros.authservice.application.ports.input.GetUserProfileUseCase;
import es.luis.almendros.authservice.application.ports.input.ChangePasswordUseCase;
import es.luis.almendros.authservice.application.ports.output.JwtTokenPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenBlacklistFilter tokenBlacklistFilter;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    @MockitoBean
    private LoginUseCase loginUseCase;

    @MockitoBean
    private RefreshTokenUseCase refreshTokenUseCase;

    @MockitoBean
    private LogoutUseCase logoutUseCase;

    @MockitoBean
    private GetUserProfileUseCase getUserProfileUseCase;

    @MockitoBean
    private ChangePasswordUseCase changePasswordUseCase;

    @MockitoBean
    private JwtTokenPort jwtTokenPort;

    @Test
    void registerEndpointShouldBePublic() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content("{\"email\":\"test@test.com\",\"username\":\"test\",\"password\":\"password123\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void loginEndpointShouldBePublic() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"test@test.com\",\"password\":\"password123\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void refreshEndpointShouldBePublic() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .contentType("application/json")
                        .content("{\"refreshToken\":\"token\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void meEndpointShouldRequireAuth() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void changePasswordEndpointShouldRequireAuth() throws Exception {
        mockMvc.perform(post("/auth/change-password")
                        .contentType("application/json")
                        .content("{\"currentPassword\":\"old\",\"newPassword\":\"new\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void logoutEndpointShouldRequireAuth() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .contentType("application/json")
                        .content("{\"accessToken\":\"token\"}"))
                .andExpect(status().is4xxClientError());
    }
}