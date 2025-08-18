package com.apex.core.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Test User Details Service for development and testing purposes
 * In production, this would connect to a real user repository/database
 */
@Service
@Slf4j
public class TestUserDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final Map<String, UserPrincipal> testUsers = new HashMap<>();

    public TestUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        initializeTestUsers();
    }

    private void initializeTestUsers() {
        // Create test users for development
        testUsers.put("testuser", UserPrincipal.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .username("testuser")
                .email("test@apex.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("Test")
                .lastName("User")
                .role("ADMIN")
                .organizationId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build());

        testUsers.put("admin", UserPrincipal.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440002"))
                .username("admin")
                .email("admin@apex.com")
                .password(passwordEncoder.encode("admin123"))
                .firstName("Admin")
                .lastName("User")
                .role("ADMIN")
                .organizationId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build());

        testUsers.put("clerk", UserPrincipal.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440003"))
                .username("clerk")
                .email("clerk@apex.com")
                .password(passwordEncoder.encode("clerk123"))
                .firstName("AP")
                .lastName("Clerk")
                .role("AP_CLERK")
                .organizationId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build());

        testUsers.put("vendor", UserPrincipal.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440004"))
                .username("vendor")
                .email("vendor@apex.com")
                .password(passwordEncoder.encode("vendor123"))
                .firstName("Vendor")
                .lastName("User")
                .role("VENDOR")
                .organizationId(UUID.fromString("550e8400-e29b-41d4-a716-446655440002"))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build());

        log.info("Initialized {} test users for development", testUsers.size());
        testUsers.keySet().forEach(username -> 
            log.info("Test user available: {} (role: {})", username, testUsers.get(username).getRole()));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        
        UserPrincipal user = testUsers.get(username);
        if (user == null) {
            log.warn("User not found: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }
        
        log.debug("User found: {} with role: {}", username, user.getRole());
        return user;
    }

    /**
     * Get all test users (for development purposes)
     */
    public Map<String, UserPrincipal> getAllTestUsers() {
        return new HashMap<>(testUsers);
    }
}
