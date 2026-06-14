package es.luis.almendros.authservice.infrastructure.web;

import es.luis.almendros.authservice.application.ports.input.*;
import es.luis.almendros.authservice.application.ports.input.RegisterUserUseCase.RegisterUserCommand;
import es.luis.almendros.authservice.application.ports.input.RegisterUserUseCase.RegisterUserResponse;
import es.luis.almendros.authservice.application.ports.input.GetUserProfileUseCase.UserProfileResponse;
import es.luis.almendros.authservice.application.ports.input.GetUserProfileUseCase.GetUserProfileCommand;
import es.luis.almendros.authservice.application.ports.output.JwtTokenPort;
import es.luis.almendros.authservice.infrastructure.web.dtos.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserService;
    private final LoginUseCase loginService;
    private final RefreshTokenUseCase refreshTokenService;
    private final LogoutUseCase logoutService;
    private final JwtTokenPort jwtTokenPort;
    private final GetUserProfileUseCase getUserProfileService;
    private final ChangePasswordUseCase changePasswordService;


    public AuthController(RegisterUserUseCase useCase, LoginUseCase loginService, RefreshTokenUseCase refreshTokenService,
                          LogoutUseCase logoutService, JwtTokenPort jwtTokenPort, GetUserProfileUseCase getUserProfileService,
                          ChangePasswordUseCase changePasswordService) {
        this.registerUserService = useCase;
        this.loginService = loginService;
        this.refreshTokenService = refreshTokenService;
        this.logoutService = logoutService;
        this.jwtTokenPort = jwtTokenPort;
        this.getUserProfileService = getUserProfileService;
        this.changePasswordService = changePasswordService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {

        RegisterUserCommand command = new RegisterUserCommand(
                request.email(),
                request.username(),
                request.password()
        );

        RegisterUserResponse response = registerUserService.register(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(RegisterResponse.of(
                response.userId(),
                response.email(),
                response.username())
        );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        LoginUseCase.LoginCommand command = new LoginUseCase.LoginCommand(
                request.email(),
                request.password()
        );

        LoginUseCase.LoginResponse response = loginService.login(command);

        return ResponseEntity.ok(new LoginResponse(
                response.accessToken(),
                response.refreshToken(),
                response.userId(),
                response.username(),
                response.email(),
                response.expiresIn()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {

        RefreshTokenUseCase.RefreshTokenCommand command = new RefreshTokenUseCase.RefreshTokenCommand(
                request.refreshToken()
        );

        RefreshTokenUseCase.RefreshTokenResponse response = refreshTokenService.refresh(command);

        return ResponseEntity.ok(new RefreshTokenResponse(
                response.accessToken(),
                response.refreshToken(),
                response.expiresIn()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {

        logoutService.logout(new LogoutUseCase.LogoutCommand(null, request.refreshToken()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile(@RequestHeader("Authorization") String authorization) {

        String token = authorization.substring(7);
        UUID userId = jwtTokenPort.validateTokenAndGetUserId(token);
        UserProfileResponse response = getUserProfileService.getProfile(new GetUserProfileCommand(userId));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestHeader("Authorization") String authorization,
                                               @Valid @RequestBody ChangePasswordRequest request) {

        String token = authorization.substring(7);
        UUID userId = jwtTokenPort.validateTokenAndGetUserId(token);

        changePasswordService.changePassword(new ChangePasswordUseCase.ChangePasswordCommand(
                userId,
                request.currentPassword(),
                request.newPassword()
        ));

        return ResponseEntity.noContent().build();
    }
}
