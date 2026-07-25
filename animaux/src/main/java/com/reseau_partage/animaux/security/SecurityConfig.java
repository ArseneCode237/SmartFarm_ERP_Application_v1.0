package com.reseau_partage.animaux.security;

import com.reseau_partage.animaux.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, e) -> {
                            response.setStatus(401);
                            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"timestamp\":" + java.time.LocalDateTime.now() +
                                            ",\"status\":401,\"error\":\"Unauthorized\"" +
                                            ",\"message\":\"Authentification requise : votre session est absente, invalide ou expiree.\"" +
                                            ",\"path\":\"" + request.getRequestURI() + "\"}"
                            );
                        })
                        .accessDeniedHandler((request, response, e) -> {
                            response.setStatus(403);
                            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"timestamp\":" + java.time.LocalDateTime.now() +
                                            ",\"status\":403,\"error\":\"Forbidden\"" +
                                            ",\"message\":\"Vous n'etes pas autorise a effectuer cette action.\"" +
                                            ",\"path\":\"" + request.getRequestURI() + "\"}"
                            );
                        }))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "http://192.168.1.*:*",
                "http://0.0.0.0:*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token", "*"));
        configuration.setExposedHeaders(List.of("x-auth-token", "Authorization"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
