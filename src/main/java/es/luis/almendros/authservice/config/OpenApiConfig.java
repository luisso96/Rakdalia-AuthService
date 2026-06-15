package es.luis.almendros.authservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Auth Service API",
                version = "v1.0.0",
                description = "Authentication API"
        ),
        servers = {
                @Server(url = "http://localhost:8081", description = "Local server")
        }
)
public class OpenApiConfig {
}