package com.apex.core.websocket;

import com.apex.core.security.JwtTokenProvider;
import com.apex.core.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * WebSocket authentication interceptor for JWT-based authentication
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider tokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                
                if (tokenProvider.validateToken(token)) {
                    try {
                        Claims claims = tokenProvider.getAllClaimsFromToken(token);
                        
                        UUID userId = UUID.fromString(claims.getSubject());
                        String username = claims.get("username", String.class);
                        String email = claims.get("email", String.class);
                        String firstName = claims.get("firstName", String.class);
                        String lastName = claims.get("lastName", String.class);
                        String organizationIdStr = claims.get("organizationId", String.class);
                        UUID organizationId = organizationIdStr != null ? UUID.fromString(organizationIdStr) : null;
                        
                        @SuppressWarnings("unchecked")
                        List<String> roles = (List<String>) claims.get("roles");
                        
                        UserPrincipal userPrincipal = UserPrincipal.builder()
                                .id(userId)
                                .username(username)
                                .email(email)
                                .firstName(firstName)
                                .lastName(lastName)
                                .organizationId(organizationId)
                                .enabled(true)
                                .accountNonExpired(true)
                                .accountNonLocked(true)
                                .credentialsNonExpired(true)
                                .build();

                        List<SimpleGrantedAuthority> authorities = roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());

                        UsernamePasswordAuthenticationToken auth = 
                                new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);
                        
                        accessor.setUser(auth);
                        
                        log.debug("WebSocket authentication successful for user: {}", username);
                    } catch (Exception e) {
                        log.error("Failed to authenticate WebSocket connection: {}", e.getMessage());
                    }
                } else {
                    log.warn("Invalid JWT token in WebSocket connection");
                }
            } else {
                log.warn("No Authorization header found in WebSocket connection");
            }
        }

        return message;
    }
}
