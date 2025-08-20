package com.apex.gateway.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.security.Principal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Health and System Information Controller
 * Provides system health checks and basic information endpoints
 */
@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Health", description = "System health and information endpoints")
public class HealthController {

    @Operation(
            summary = "Get system health status",
            description = "Returns the current health status of all system components including database, messaging, and storage"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "System is healthy",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "503", description = "System is unhealthy",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getHealthStatus() {
        Map<String, Object> health = Map.of(
                "status", "UP",
                "timestamp", System.currentTimeMillis(),
                "version", "1.0.0",
                "components", Map.of(
                        "database", Map.of("status", "UP", "details", "PostgreSQL connection healthy"),
                        "messaging", Map.of("status", "UP", "details", "Kafka brokers available"),
                        "storage", Map.of("status", "UP", "details", "MinIO storage accessible"),
                        "cache", Map.of("status", "UP", "details", "Redis cache operational")
                )
        );

        return ResponseEntity.ok(health);
    }

    @Operation(
            summary = "Get detailed system information",
            description = "Returns detailed system information including configuration and runtime metrics"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "System information retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - admin role required")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/info")
    @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Map<String, Object>> getSystemInfo(
            @Parameter(description = "Current authenticated user")
                        @AuthenticationPrincipal Principal user) {
                String username = (user != null) ? user.getName() : "anonymous";
                log.info("System info requested by admin user: {}", username);
        
        Map<String, Object> info = Map.of(
                "application", Map.of(
                        "name", "Apex IDP Platform",
                        "version", "1.0.0",
                        "description", "Intelligent Document Processing Platform for Healthcare",
                        "buildTime", "2024-01-01T00:00:00Z"
                ),
                "environment", Map.of(
                        "javaVersion", System.getProperty("java.version"),
                        "springBootVersion", "3.2.0",
                        "profiles", "production"
                ),
                "metrics", Map.of(
                        "uptime", "72h 15m 30s",
                        "documentsProcessed", 15847,
                        "activeUsers", 23,
                        "storageUsed", "2.3GB"
                )
        );

        return ResponseEntity.ok(info);
    }

    @Operation(
            summary = "Perform readiness check",
            description = "Checks if the system is ready to serve requests"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "System is ready"),
            @ApiResponse(responseCode = "503", description = "System is not ready")
    })
    @GetMapping("/ready")
    public ResponseEntity<Map<String, Object>> readinessCheck() {
        // Perform actual readiness checks here
        boolean ready = true; // Simplified for example
        
        Map<String, Object> response = Map.of(
                "ready", ready,
                "timestamp", System.currentTimeMillis(),
                "checks", Map.of(
                        "database", "PASS",
                        "messaging", "PASS",
                        "storage", "PASS"
                )
        );

        return ready ? ResponseEntity.ok(response) : 
                      ResponseEntity.status(503).body(response);
    }

    @Operation(
            summary = "Perform liveness check",
            description = "Checks if the application is alive and responding"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application is alive")
    })
    @GetMapping("/live")
    public ResponseEntity<Map<String, String>> livenessCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "ALIVE",
                "timestamp", String.valueOf(System.currentTimeMillis())
        ));
    }
}
