package com.apex.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

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
@EntityScan(basePackages = {
    "com.apex.vendor.domain",
    "com.apex.document.domain"
})
@EnableJpaRepositories(basePackages = {
    "com.apex.vendor.infrastructure",
    "com.apex.document.infrastructure"
})
public class ApexIdpApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ApexIdpApplication.class, args);
    }
}
