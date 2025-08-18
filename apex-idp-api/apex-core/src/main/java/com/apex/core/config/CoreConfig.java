package com.apex.core.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Core configuration for the Apex platform
 */
@Configuration
@ComponentScan(basePackages = "com.apex.core")
@EnableJpaAuditing
public class CoreConfig {
    
}
