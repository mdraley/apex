package com.apex.core.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Custom Actuator endpoint for testing auth bypass
 */
@Component
@Endpoint(id = "authtest")
@Slf4j
public class AuthTestActuatorEndpoint {

    @ReadOperation
    public Map<String, Object> test() {
        log.info("Auth test actuator endpoint accessed");
        return Map.of(
            "message", "Auth test endpoint working through actuator",
            "status", "success"
        );
    }
}
