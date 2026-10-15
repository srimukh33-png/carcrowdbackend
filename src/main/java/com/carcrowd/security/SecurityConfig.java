package com.carcrowd.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    Map<String, Object> errorBody = Map.of(
                        "status", HttpServletResponse.SC_UNAUTHORIZED,
                        "error", "Unauthorized",
                        "message", authException.getMessage() != null ? authException.getMessage() : "Full authentication is required to access this resource"
                    );
                    objectMapper.writeValue(response.getOutputStream(), errorBody);
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    Map<String, Object> errorBody = Map.of(
                        "status", HttpServletResponse.SC_FORBIDDEN,
                        "error", "Forbidden",
                        "message", "You do not have permission to access this resource"
                    );
                    objectMapper.writeValue(response.getOutputStream(), errorBody);
                })
            )
            .authorizeHttpRequests(auth -> auth
                // Public car catalog APIs
                .requestMatchers(HttpMethod.GET, "/api/brands/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cars/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cars/*/interests/count").permitAll()
                // Google customer auth public endpoints
                .requestMatchers(HttpMethod.POST, "/api/auth/google").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
                // Admin and Member authentication public endpoints
                .requestMatchers(HttpMethod.POST, "/api/admin/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/member/auth/login").permitAll()
                // Shared authentication check for staff
                .requestMatchers(HttpMethod.GET, "/api/admin/auth/me").hasAnyRole("ADMIN", "MEMBER")
                .requestMatchers(HttpMethod.POST, "/api/admin/auth/logout").hasAnyRole("ADMIN", "MEMBER")
                // Member protected endpoints (strictly ROLE_MEMBER)
                .requestMatchers("/api/member/**").hasRole("MEMBER")
                // Preflight OPTIONS requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Dev and static tools
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/error").permitAll()
                // Admin protected endpoints (strictly ROLE_ADMIN)
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // Customer profile, requests, and interest registration endpoints
                .requestMatchers("/api/auth/me").hasRole("CUSTOMER")
                .requestMatchers("/api/profile/**").hasRole("CUSTOMER")
                .requestMatchers("/api/my-requests/**").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.POST, "/api/cars/*/interests").hasRole("CUSTOMER")
                // Fallback: any other request authenticated
                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
