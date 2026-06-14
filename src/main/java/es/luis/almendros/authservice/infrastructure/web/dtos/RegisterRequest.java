package es.luis.almendros.authservice.infrastructure.web.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,

        @NotBlank(message = "Username is required") @Size(min = 3, max = 50, message = "Invalid User") String username,

        @NotBlank(message = "Password is required.") @Size(min = 8, message = "Invalid Password") String password) { }
