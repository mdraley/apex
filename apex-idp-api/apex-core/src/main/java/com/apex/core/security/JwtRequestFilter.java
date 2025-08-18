package com.apex.core.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT Request Filter that processes JWT tokens on each request
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
        
        final String requestTokenHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwtToken = null;
        
        // JWT token is in the form "Bearer token"
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtService.getUsernameFromToken(jwtToken);
            } catch (Exception e) {
                log.warn("Unable to get JWT Token: {}", e.getMessage());
            }
        } else {
            log.debug("JWT Token does not begin with Bearer String");
        }
        
        // Validate token and set authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtService.validateToken(jwtToken, username)) {
                
                // Extract roles from token (you'll need to implement this based on your token structure)
                List<String> roles = extractRolesFromToken(jwtToken);
                List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
                
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("Successfully authenticated user: {}", username);
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private List<String> extractRolesFromToken(String token) {
        try {
            // Extract roles from JWT claims
            // This is a placeholder - implement based on your token structure
            @SuppressWarnings("unchecked")
            List<String> roles = jwtService.getClaimFromToken(token, 
                claims -> (List<String>) claims.get("roles"));
            return roles != null ? roles : List.of("USER");
        } catch (Exception e) {
            log.warn("Could not extract roles from token: {}", e.getMessage());
            return List.of("USER"); // Default role
        }
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        log.debug("JWT Filter checking path: {} for skipping", path);
        // Skip JWT processing for public endpoints
        boolean shouldSkip = path.startsWith("/v1/auth/") || 
               path.startsWith("/v1/public/") ||
               path.startsWith("/v1/health/") ||
               path.startsWith("/actuator/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs/") ||
               path.startsWith("/ws/");
        log.debug("Should skip JWT filter for path {}: {}", path, shouldSkip);
        return shouldSkip;
    }
}
