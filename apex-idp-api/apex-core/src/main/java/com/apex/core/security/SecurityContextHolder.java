package com.apex.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

/**
 * Utility class for accessing security context information.
 * Provides convenient methods to get current user information.
 */
public class SecurityContextHolder {
    
    /**
     * Get the current authenticated user's ID.
     * @return the user ID or "system" if no authentication is present
     */
    public static String getCurrentUserId() {
        SecurityContext context = org.springframework.security.core.context.SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "system";
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) principal;
            return userPrincipal.getId().toString();
        }
        
        // Fallback to authentication name
        return authentication.getName() != null ? authentication.getName() : "system";
    }
    
    /**
     * Get the current authenticated user's email.
     * @return the user email or null if not available
     */
    public static String getCurrentUserEmail() {
        SecurityContext context = org.springframework.security.core.context.SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) principal;
            return userPrincipal.getEmail();
        }
        
        return null;
    }
    
    /**
     * Check if the current user has a specific role.
     * @param role the role to check
     * @return true if the user has the role, false otherwise
     */
    public static boolean hasRole(String role) {
        SecurityContext context = org.springframework.security.core.context.SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }
    
    /**
     * Get the current user principal.
     * @return the user principal or null if not authenticated
     */
    public static UserPrincipal getCurrentUser() {
        SecurityContext context = org.springframework.security.core.context.SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal) {
            return (UserPrincipal) principal;
        }
        
        return null;
    }
}
