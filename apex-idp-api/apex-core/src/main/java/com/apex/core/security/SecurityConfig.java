package com.apex.core.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Comprehensive Security Configuration for Apex IDP
 * Implements JWT-based authentication with role-based access control
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtRequestFilter jwtRequestFilter;
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - note: context path /api is handled by Tomcat
                .requestMatchers("/v1/auth/**").permitAll()
                .requestMatchers("/v1/public/**").permitAll()
                .requestMatchers("/v1/health/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/ws/**").permitAll()  // WebSocket endpoints
                
                // Admin only endpoints
                .requestMatchers(HttpMethod.POST, "/v1/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/v1/users/**").hasRole("ADMIN")
                .requestMatchers("/v1/admin/**").hasRole("ADMIN")
                
                // Vendor management - vendors and admins
                .requestMatchers("/v1/vendors/**").hasAnyRole("ADMIN", "VENDOR_MANAGER")
                .requestMatchers("/v1/financial/**").hasAnyRole("ADMIN", "FINANCE_MANAGER")
                .requestMatchers("/v1/contracts/**").hasAnyRole("ADMIN", "CONTRACT_MANAGER")
                
                // Document processing - all authenticated users
                .requestMatchers("/v1/documents/**").hasAnyRole("ADMIN", "USER", "VENDOR_MANAGER", "FINANCE_MANAGER", "CONTRACT_MANAGER")
                
                // Integration endpoints - admin and system users
                .requestMatchers("/v1/integration/**").hasAnyRole("ADMIN", "SYSTEM")
                
                // All other endpoints require authentication
                .anyRequest().authenticated()
            );
        
        // Add JWT filter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
