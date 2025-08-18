package com.apex.core.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

/**
 * Public Test Controller for testing API connectivity without authentication
 */
@RestController
@RequestMapping("/v1/public")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class PublicTestController {

    @GetMapping("/health")
    public ResponseEntity<?> publicHealth() {
        log.info("Public health check accessed");
        
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "message", "Public API is working",
            "timestamp", Instant.now().toString(),
            "service", "Apex IDP API"
        ));
    }

    @PostMapping("/echo")
    public ResponseEntity<?> echo(@RequestBody(required = false) Map<String, Object> body) {
        log.info("Public echo endpoint accessed with body: {}", body);
        
        return ResponseEntity.ok(Map.of(
            "message", "Echo endpoint working",
            "received", body != null ? body : "no body",
            "timestamp", Instant.now().toString()
        ));
    }

    @GetMapping("/test-token")
    public ResponseEntity<?> getTestToken() {
        log.info("Test token request - this endpoint should work without authentication");
        
        // Return a fake response that indicates the endpoint is working
        return ResponseEntity.ok(Map.of(
            "message", "This endpoint is public and working",
            "next_step", "Use /v1/auth/test-user to get a real JWT token",
            "timestamp", Instant.now().toString()
        ));
    }
}
