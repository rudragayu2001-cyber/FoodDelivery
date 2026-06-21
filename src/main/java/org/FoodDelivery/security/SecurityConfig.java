package org.FoodDelivery.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Stateless HTTP-Basic security with role-based authorization.
 *
 * <p>Advanced auth (OAuth/SSO/MFA) is intentionally out of scope. Coarse URL rules live here;
 * finer per-operation checks use {@code @PreAuthorize} on the service/controller methods.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public: registration, health, H2 console, error
                        .requestMatchers(HttpMethod.POST, "/api/auth/register/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info", "/h2-console/**", "/error")
                        .permitAll()
                        // API docs / Swagger UI are open so the docs are browsable
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                        .permitAll()
                        // Browsing the catalogue is open to any authenticated user
                        .requestMatchers(HttpMethod.GET, "/api/browse/**").authenticated()
                        .anyRequest().authenticated())
                .httpBasic(basic -> {})
                // H2 console renders inside a frame
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
