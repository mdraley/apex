package com.apex.core.api;

import com.apex.core.security.JwtTokenProvider;
import com.apex.core.security.UserPrincipal;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Authentication Controller for user login and registration
 * Provides endpoints for JWT-based authentication
 */
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());
        
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    ));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(authentication);

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", jwt);
            response.put("refreshToken", refreshToken);
            response.put("tokenType", "Bearer");
            response.put("user", Map.of(
                "id", userPrincipal.getId(),
                "username", userPrincipal.getUsername(),
                "email", userPrincipal.getEmail(),
                "firstName", userPrincipal.getFirstName(),
                "lastName", userPrincipal.getLastName(),
                "role", userPrincipal.getRole(),
                "organizationId", userPrincipal.getOrganizationId()
            ));

            log.info("User {} authenticated successfully", loginRequest.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Authentication failed for user {}: {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid username or password"));
        }
    }

    @PostMapping("/test-user")
    public ResponseEntity<?> createTestUser() {
        log.info("Creating test user for development");
        
        // For development/testing purposes only
        UserPrincipal testUser = UserPrincipal.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@apex.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("Test")
                .lastName("User")
                .role("ADMIN")
                .organizationId(UUID.randomUUID())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        // Create authentication object manually for testing
        Authentication auth = new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities());
        String jwt = tokenProvider.generateToken(auth);
        String refreshToken = tokenProvider.generateRefreshToken(auth);

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", jwt);
        response.put("refreshToken", refreshToken);
        response.put("tokenType", "Bearer");
        response.put("user", Map.of(
            "id", testUser.getId(),
            "username", testUser.getUsername(),
            "email", testUser.getEmail(),
            "firstName", testUser.getFirstName(),
            "lastName", testUser.getLastName(),
            "role", testUser.getRole(),
            "organizationId", testUser.getOrganizationId()
        ));

        log.info("Test user created successfully with token");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        Map<String, Object> user = Map.of(
            "id", userPrincipal.getId(),
            "username", userPrincipal.getUsername(),
            "email", userPrincipal.getEmail(),
            "firstName", userPrincipal.getFirstName(),
            "lastName", userPrincipal.getLastName(),
            "role", userPrincipal.getRole(),
            "organizationId", userPrincipal.getOrganizationId()
        );

        return ResponseEntity.ok(user);
    }

    @Data
    public static class LoginRequest {
        @NotBlank
        @Size(min = 3, max = 50)
        private String username;

        @NotBlank
        @Size(min = 6, max = 40)
        private String password;
    }

    @Data
    public static class SignUpRequest {
        @NotBlank
        @Size(min = 3, max = 50)
        private String username;

        @NotBlank
        @Size(max = 60)
        @Email
        private String email;

        @NotBlank
        @Size(min = 6, max = 40)
        private String password;

        @NotBlank
        @Size(min = 1, max = 50)
        private String firstName;

        @NotBlank
        @Size(min = 1, max = 50)
        private String lastName;

        private String role = "USER";
    }
}
