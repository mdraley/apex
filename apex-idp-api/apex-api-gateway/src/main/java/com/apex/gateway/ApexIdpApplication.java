package com.apex.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import com.apex.gateway.config.ApexProperties;

/**
 * Main application class for Apex IDP Platform
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.apex.core",
    "com.apex.vendor",
    "com.apex.document",
    "com.apex.gateway"
})
@EnableConfigurationProperties(ApexProperties.class)
@EntityScan(basePackages = {
    "com.apex.vendor.domain",
    "com.apex.document.domain",
    // Include read-side projection entities
    "com.apex.document.infrastructure.projection"
})
@EnableJpaRepositories(basePackages = {
    "com.apex.vendor.infrastructure",
    "com.apex.document.infrastructure",
    // Ensure repository interfaces located in domain packages are discovered
    "com.apex.vendor.domain",
    "com.apex.document.domain"
})
public class ApexIdpApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ApexIdpApplication.class, args);
    }
}
