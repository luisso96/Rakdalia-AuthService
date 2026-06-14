package es.luis.almendros.authservice.config;

import es.luis.almendros.authservice.infrastructure.web.filters.TokenBlacklistFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final TokenBlacklistFilter tokenBlacklistFilter;

    public SecurityConfig(TokenBlacklistFilter tokenBlacklistFilter) {
        this.tokenBlacklistFilter = tokenBlacklistFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/register", "/auth/login").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/auth/register", "/auth/login", "/auth/refresh").permitAll()
                .anyRequest().authenticated()
            ).addFilterBefore(tokenBlacklistFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
