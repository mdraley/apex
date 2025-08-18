package com.apex.gateway.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3.0 configuration for Apex IDP Platform
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Apex IDP Platform API")
                        .description("""
                                Comprehensive Intelligent Document Processing Platform API
                                
                                ## Features
                                - **Document Processing**: Upload, process, and extract data from documents
                                - **Vendor Management**: Manage vendor relationships and onboarding
                                - **Financial Operations**: Handle invoices, payments, and financial workflows
                                - **Contract Management**: Lifecycle management of contracts and agreements
                                - **Integration**: ERP and external system integrations
                                - **Real-time Notifications**: WebSocket-based real-time updates
                                
                                ## Authentication
                                This API uses JWT Bearer token authentication. Include the token in the Authorization header:
                                ```
                                Authorization: Bearer <your-jwt-token>
                                ```
                                
                                ## Rate Limiting
                                API requests are rate limited to prevent abuse. Current limits:
                                - Authenticated users: 1000 requests per hour
                                - Document uploads: 100 requests per hour
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Apex IDP Support")
                                .email("support@apex-idp.com")
                                .url("https://apex-idp.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development server"),
                        new Server().url("https://api.apex-idp.com").description("Production server")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Bearer token authentication")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
