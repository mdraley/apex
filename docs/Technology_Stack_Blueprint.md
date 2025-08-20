# Technology Stack Blueprint

Configuration
- Project Type: Auto-detect (full-stack)
- Depth Level: Comprehensive
- Include Versions: true
- Include Licenses: true
- Include Diagrams: true
- Include Usage Patterns: true
- Include Conventions: true
- Output Format: Markdown
- Categorization: Layer

## Versions
- Spring Boot 3.4.5
- Spring Cloud 2024.0.2
- springdoc-openapi 2.8.9
- MapStruct 1.5.5.Final
- Lombok 1.18.32
- React ^18.3.1
- Vite ^5.4.1
- TypeScript ^5.5.4

## Endpoints
- Health: /api/actuator/health
- Swagger UI: /api/swagger-ui/index.html
- OpenAPI: /api/v3/api-docs

## Modules
- Apex Core (jar)
- Apex Vendor Management (jar)
- Apex Document Processing (jar)
- Apex Financial Operations (jar)
- Apex Contract Management (jar)
- Apex Integration (jar)
- Apex API Gateway (jar)

## Dependencies by Module (top 10)
### Apex Core
- org.springframework.boot:spring-boot-starter-web
- org.springframework.boot:spring-boot-starter-data-jpa
- org.springframework.boot:spring-boot-starter-security
- org.springframework.boot:spring-boot-starter-validation
- org.springframework.boot:spring-boot-starter-websocket
- org.springframework.boot:spring-boot-starter-actuator
- org.springframework.kafka:spring-kafka
- org.postgresql:postgresql (runtime)
- org.flywaydb:flyway-core
- io.jsonwebtoken:jjwt-api

### Apex Vendor Management
- com.apex:apex-core:${project.version}
- org.springframework.boot:spring-boot-starter-web
- org.springframework.boot:spring-boot-starter-data-jpa
- org.springframework.boot:spring-boot-starter-validation
- org.springdoc:springdoc-openapi-starter-webmvc-ui
- org.projectlombok:lombok
- org.postgresql:postgresql (runtime)
- org.springframework.boot:spring-boot-starter-test (test)
- org.testcontainers:postgresql (test)
- org.mapstruct:mapstruct:${mapstruct.version}

### Apex Document Processing
- com.apex:apex-core:${project.version}
- org.springframework.boot:spring-boot-starter-web
- org.springframework.boot:spring-boot-starter-data-jpa
- org.springframework.boot:spring-boot-starter-websocket
- org.apache.tika:tika-core:2.9.1
- org.apache.tika:tika-parsers-standard-package:2.9.1
- io.minio:minio:8.5.7
- org.springdoc:springdoc-openapi-starter-webmvc-ui
- org.projectlombok:lombok
- org.springframework.boot:spring-boot-starter-test (test)

### Apex Financial Operations
- com.apex:apex-core:${project.version}
- org.springframework.boot:spring-boot-starter-web
- org.springframework.boot:spring-boot-starter-data-jpa
- org.springframework.boot:spring-boot-starter-validation
- org.springframework.boot:spring-boot-starter-security
- org.projectlombok:lombok

### Apex Contract Management
- com.apex:apex-core:${project.version}
- org.springframework.boot:spring-boot-starter-web
- org.springframework.boot:spring-boot-starter-data-jpa
- org.springframework.boot:spring-boot-starter-validation
- org.springframework.boot:spring-boot-starter-security
- org.projectlombok:lombok

### Apex Integration
- com.apex:apex-core:${project.version}
- org.springframework.boot:spring-boot-starter-web
- org.springframework.boot:spring-boot-starter-webflux
- org.springframework.boot:spring-boot-starter-data-jpa
- org.springframework.boot:spring-boot-starter-validation
- org.springframework.boot:spring-boot-starter-security
- org.springframework.integration:spring-integration-core
- org.springframework.integration:spring-integration-http
- org.projectlombok:lombok

### Apex API Gateway
- com.apex:apex-core:${project.version}
- com.apex:apex-vendor-management:${project.version}
- com.apex:apex-document-processing:${project.version}
- org.springframework.boot:spring-boot-starter-web
- org.springframework.boot:spring-boot-starter-data-jpa
- org.springframework.boot:spring-boot-starter-security
- org.springframework.boot:spring-boot-starter-websocket
- org.springframework.boot:spring-boot-starter-actuator
- org.postgresql:postgresql (runtime)
- com.h2database:h2 (runtime)
