package es.luis.almendros.authservice.config;

import es.luis.almendros.authservice.infrastructure.adapters.security.Argon2PasswordEncoderAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
public class PasswordEncoderConfig {

    @Bean
    @Primary
    @Profile("dev")
    public Argon2PasswordEncoderAdapter devPasswordEncoder() {
        return new Argon2PasswordEncoderAdapter();
    }

    @Bean
    @Profile("prod")
    public Argon2PasswordEncoderAdapter prodPasswordEncoder() {
        return new Argon2PasswordEncoderAdapter();
    }
}
