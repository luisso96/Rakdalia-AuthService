package es.luis.almendros.authservice.infrastructure.web;

import es.luis.almendros.authservice.application.ports.input.*;
import es.luis.almendros.authservice.application.ports.input.RegisterUserUseCase.RegisterUserCommand;
import es.luis.almendros.authservice.application.ports.input.RegisterUserUseCase.RegisterUserResponse;
import es.luis.almendros.authservice.application.ports.input.GetUserProfileUseCase.UserProfileResponse;
import es.luis.almendros.authservice.application.ports.input.GetUserProfileUseCase.GetUserProfileCommand;
import es.luis.almendros.authservice.application.ports.output.JwtTokenPort;
import es.luis.almendros.authservice.infrastructure.web.dtos.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
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
    @Operation(summary = "Register new user", description = "Create a new account in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully created",
                    content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data (malformed email, short password)",
                    content = @Content(examples = @ExampleObject(value = "{\"timestamp\":\"2024-01-15T10:30:00Z\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Email cannot be empty.\"}"))),
            @ApiResponse(responseCode = "409", description = "Conflict - Email or username already exists",
                    content = @Content(examples = @ExampleObject(value = "{\"timestamp\":\"2024-01-15T10:30:00Z\",\"status\":409,\"error\":\"Conflict\",\"message\":\"There is already a user with that email address.\"}")))
    })
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
    @Operation(summary = "Login", description = "Authenticates the user and returns JWT tokens (access + refresh)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful - Returns JWT tokens",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data (empty email or password)"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials or inactive user",
                    content = @Content(examples = @ExampleObject(value = "{\"timestamp\":\"2024-01-15T10:30:00Z\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Invalid credentials\"}")))
    })
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
    @Operation(summary = "Renew tokens", description = "Use the refresh token to get new access and refresh tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tokens successfully renewed",
                    content = @Content(schema = @Schema(implementation = RefreshTokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Refresh token not provided"),
            @ApiResponse(responseCode = "401", description = "Refresh token invalid or expired")
    })
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
    @Operation(summary = "Log out", description = "Revoke the tokens (add them to the Redis blacklist)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Logout successful - No content"),
            @ApiResponse(responseCode = "400", description = "Access token not provided"),
            @ApiResponse(responseCode = "401", description = "Invalid or revoked token")
    })
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {

        logoutService.logout(new LogoutUseCase.LogoutCommand(null, request.refreshToken()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Get profile", description = "Returns the authenticated user's information")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile successfully obtained",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid, expired or revoked token")
    })
    public ResponseEntity<UserProfileResponse> getProfile(@RequestHeader("Authorization") String authorization) {

        String token = authorization.substring(7);
        UUID userId = jwtTokenPort.validateTokenAndGetUserId(token);
        UserProfileResponse response = getUserProfileService.getProfile(new GetUserProfileCommand(userId));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Update the authenticated user's password")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Password successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data (new password too short or the same as the current one)"),
            @ApiResponse(responseCode = "401", description = "Invalid token or incorrect current password")
    })
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
