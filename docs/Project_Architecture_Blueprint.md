# Project Architecture Blueprint

Generated: 2025-08-19

## Overview
Apex IDP is a modular monolith (DDD-style) on Spring Boot 3.4.5 with a React 18/Vite frontend. Backend modules include core, vendor-management, document-processing, financial-operations, contract-management, integration, and the API gateway. Infrastructure includes PostgreSQL, Redis, Kafka, and MinIO; H2 and in-memory mocks are used in dev.

## System Context (C4)
```mermaid
C4Context
  title System Context diagram for Apex IDP Platform
  Person(user, "End User", "Clerks, Vendors, Admins using the system")
  System(frontend, "Apex IDP Frontend", "React 18 + Vite + Ant Design 5 SPA")
  System(api, "Apex API Gateway", "Spring Boot 3.4.5 modular monolith")
  SystemDb(db, "PostgreSQL", "Primary relational database")
  SystemDb(cache, "Redis", "Caching / sessions")
  SystemQueue(kafka, "Kafka", "Event streaming bus")
  System_Ext(minio, "MinIO", "Object storage for documents")

  Rel(user, frontend, "Uses", "HTTPS")
  Rel(frontend, api, "Calls REST APIs & WebSocket", "JSON/HTTPS + WS")
  Rel(api, db, "JPA/Hibernate", "JDBC")
  Rel(api, cache, "Caches tokens/sessions")
  Rel(api, minio, "Stores/reads files", "S3 API")
  Rel(api, kafka, "Publishes domain events", "async, JSON")
```

## Containers (C4)
```mermaid
C4Container
title Container diagram for Apex IDP

Person(user, "End User")
System_Boundary(app, "Apex IDP Platform") {
  Container(frontend, "Frontend SPA", "React 18/Vite/AntD", "UI & routing")
  Container(api, "API Gateway", "Spring Boot 3.4.5", "REST APIs, WebSocket, Security")
  ContainerDb(db, "PostgreSQL", "Relational DB")
  ContainerDb(cache, "Redis", "Cache/Sessions")
  ContainerQueue(kafka, "Kafka", "Event bus")
  Container_Ext(minio, "MinIO", "Object storage")
}

Rel(user, frontend, "Uses", "HTTPS")
Rel(frontend, api, "Calls APIs", "JSON/HTTPS")
Rel(api, db, "JPA/Hibernate", "JDBC")
Rel(api, cache, "Caches tokens/sessions")
Rel(api, kafka, "Publishes domain events")
Rel(api, minio, "Stores documents", "S3 API")
```

## Components (C4) – API Gateway focus
```mermaid
C4Component
title Component view for API Gateway

Container(api, "API Gateway", "Spring Boot")
Component(sec, "SecurityConfig", "Spring Security", "JWT auth & public paths")
Component(openapi, "OpenApiConfig", "springdoc", "OpenAPI/Swagger config")
Component(docCtl, "DocumentController", "REST Controller", "Upload/validate/query")
Component(docSvcQ, "DocumentQueryService", "Service", "Read/query side")
Component(docSvcC, "DocumentCommandService", "Service", "Write/processing side")
Component(docRepo, "DocumentProjectionRepository", "Spring Data JPA", "Read model repo")
Component(ws, "WebSocketNotificationService", "WS", "Real-time notifications")

Rel(docCtl, docSvcQ, "Queries")
Rel(docCtl, docSvcC, "Commands")
Rel(docSvcQ, docRepo, "Reads")
Rel(docSvcC, ws, "Notifies")
Rel(api, sec, "Secures requests")
Rel(api, openapi, "Exposes docs")
```

## Module components (C4) — by bounded context

### Document Processing
```mermaid
C4Component
title Document Processing components

Container(doc, "Document Processing", "Spring Boot")
Component(docCtl, "DocumentController", "REST Controller", "Upload, validate, search, stats")
Component(docCmd, "DocumentCommandService", "Service", "Write/processing commands")
Component(docQry, "DocumentQueryService", "Service", "Read/projections/metrics")
Component(docRepo, "DocumentProjectionRepository", "JPA Repository", "CQRS read model")
Component(ws, "WebSocketNotificationService", "WebSocket", "Notify UI on updates")

Rel(docCtl, docCmd, "commands")
Rel(docCtl, docQry, "queries")
Rel(docQry, docRepo, "reads")
Rel(docCmd, ws, "notifies")
```

### Vendor Management
```mermaid
C4Component
title Vendor Management components

Container(vend, "Vendor Management", "Spring Boot")
Component(vCtl, "VendorController", "REST Controller", "CRUD, status, search, stats")
Component(vSvc, "VendorService", "Service", "Business rules & workflows")
Component(vRepo, "VendorRepository", "JPA Repository", "Aggregate persistence")
Component(vMap, "VendorMapper", "MapStruct", "DTO ⇄ Domain mapping")

Rel(vCtl, vSvc, "invokes")
Rel(vSvc, vRepo, "persists")
Rel(vSvc, vMap, "maps")
```

### Financial Operations
```mermaid
C4Component
title Financial Operations components

Container(fin, "Financial Operations", "Spring Boot")
Component(fCtl, "FinanceController", "REST Controller")
Component(fSvc, "FinanceService", "Service")
Component(fRepo, "FinanceRepository", "JPA Repository")

Rel(fCtl, fSvc, "invokes")
Rel(fSvc, fRepo, "persists")
```

### Contract Management
```mermaid
C4Component
title Contract Management components

Container(con, "Contract Management", "Spring Boot")
Component(cCtl, "ContractController", "REST Controller")
Component(cSvc, "ContractService", "Service")
Component(cRepo, "ContractRepository", "JPA Repository")

Rel(cCtl, cSvc, "invokes")
Rel(cSvc, cRepo, "persists")
```

### Integration
```mermaid
C4Component
title Integration components

Container(integ, "Integration", "Spring Boot")
Component(iCtl, "IntegrationController", "REST Controller")
Component(iSvc, "IntegrationService", "Service", "External system adapters")
Component(iKafka, "KafkaPublisher", "Messaging", "Publishes domain events")

Rel(iCtl, iSvc, "invokes")
Rel(iSvc, iKafka, "publishes events")
```

## Data model highlights
- DocumentProjection: id, filename, documentType, stage, userId, uploadedAt, lastModified, extractedFields, confidenceScore, storagePath, isValid, validationErrors.
- Derived queries: `countByUploadedAtAfter(LocalDateTime)` for daily metrics.
- Document (write model): created_at, modified_at; related entities like ExtractedField, ProcessingError.

### ER overview
```mermaid
erDiagram
  DOCUMENTS ||--o{ EXTRACTED_FIELDS : contains
  DOCUMENTS ||--o{ PROCESSING_ERRORS : contains
  VENDORS ||--o{ DOCUMENTS : supplies
  DOCUMENT_PROJECTIONS }o..o{ EXTRACTED_FIELDS : summarizes

  DOCUMENTS {
    UUID id PK
    TIMESTAMPTZ created_at
    TIMESTAMPTZ modified_at
    TEXT file_name
    BIGINT file_size
    ENUM stage
    ENUM status
    ENUM type
    UUID vendor_id FK
  }

  EXTRACTED_FIELDS {
    UUID id PK
    UUID document_id FK
    TEXT field_name
    TEXT field_value
    NUMERIC_5_4 confidence
    INT page_number
  }

  PROCESSING_ERRORS {
    UUID id PK
    UUID document_id FK
    TIMESTAMPTZ occurred_at
    TEXT error_type
    TEXT error_message
  }

  VENDORS {
    UUID id PK
    TEXT name
    TEXT tax_id
    ENUM status
  }

  DOCUMENT_PROJECTIONS {
    VARCHAR id PK
    VARCHAR filename
    VARCHAR document_type
    VARCHAR stage
    VARCHAR user_id
    TIMESTAMP uploaded_at
    FLOAT confidence_score
  }
```

## Representative code examples

### DocumentController (REST)
```java
// ...excerpt from apex-document-processing/src/main/java/com/apex/document/api/DocumentController.java
@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
@PreAuthorize("hasAnyRole('AP_CLERK', 'VENDOR')")
public ApiResponse<DocumentUploadResponse> uploadDocument(
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "vendorId", required = false) UUID vendorId) {
    UUID documentId = commandService.uploadDocument(file, vendorId);
    notificationService.notifyDocumentUploaded(documentId);
    return ApiResponse.success(DocumentUploadResponse.builder()
            .documentId(documentId)
            .fileName(file.getOriginalFilename())
            .status("UPLOADED").build());
}
```

### SecurityConfig (JWT + public paths)
```java
// ...excerpt from apex-core/src/main/java/com/apex/core/security/SecurityConfig.java
.requestMatchers("/swagger-ui.html").permitAll()
.requestMatchers("/swagger-ui/**").permitAll()
.requestMatchers("/v3/api-docs/**").permitAll()
.requestMatchers("/v3/api-docs.yaml").permitAll()
```

### OpenAPI configuration
```java
// ...excerpt from apex-api-gateway/src/main/java/com/apex/gateway/config/OpenApiConfig.java
return new OpenAPI()
  .info(new Info().title("Apex IDP Platform API").version("1.0.0"))
  .components(new Components().addSecuritySchemes("bearerAuth",
      new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
  .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
```

### Repository derived query
```java
// ...excerpt from apex-document-processing/.../DocumentProjectionRepository.java
long countByUploadedAtAfter(LocalDateTime dateTime);
```

### MapStruct example (Vendor)
```java
// ...excerpt from apex-vendor-management/src/main/java/com/apex/vendor/application/VendorMapper.java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VendorMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "modifiedAt", ignore = true)
  @Mapping(target = "modifiedBy", ignore = true)
  @Mapping(target = "domainEventsList", ignore = true)
  @Mapping(target = "status", ignore = true)
  Vendor toEntity(CreateVendorCommand command);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateVendorFromCommand(UpdateVendorCommand command, @MappingTarget Vendor vendor);
}
```

### WebSocket configuration and auth
```java
// ...excerpt from apex-core/src/main/java/com/apex/core/websocket/WebSocketConfig.java
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
  private final WebSocketAuthInterceptor webSocketAuthInterceptor;
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    var registration = registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
    registration.interceptors(webSocketAuthInterceptor);
    registry.addEndpoint("/ws").withSockJS();
  }
}
```

## ADRs (Architecture Decision Records)
We maintain ADRs under `docs/adr/`.

Initial ADRs to scaffold:
1. Use Spring Boot 3.4.5 + Spring Cloud 2024.0.2 for modular monolith
2. Adopt springdoc-openapi 2.8.9 with Swagger UI enabled in dev
3. JWT-based authentication with public dev paths for docs/health
4. Use MinIO for document storage (docker/prod), local FS in dev
5. Use Redis for caching/sessions; Kafka for event streaming

See: 
- docs/adr/0001-adopt-spring-boot-3-4-5.md
- docs/adr/0002-adopt-springdoc-openapi-2-8-9.md
- docs/adr/0003-jwt-auth-and-public-docs-in-dev.md
- docs/adr/0004-document-storage-minio.md
- docs/adr/0005-redis-cache-and-kafka-events.md

## How to extend
- Add a new bounded context as a module; wire into gateway via dependency and @ComponentScan
- Expose APIs with annotated controllers; define DTOs and MapStruct mappers
- Extend configuration via `apex.*` properties with configuration-processor metadata
- Add diagrams by copying a C4 section and updating nodes/relations
## Technology Stack (auto-detected)
- Backend: Java 17, Spring Boot 3.4.5, Spring Cloud 2024.0.2
- API docs: springdoc-openapi 2.8.9 (Swagger UI at /api/swagger-ui.html)
- Data: JPA/Hibernate; PostgreSQL (docker, prod); H2 (dev)
- Caching/Sessions: Redis (spring.data.redis.*)
- Messaging: Kafka 3.6
- Storage: MinIO (S3 API)
- Mapping: MapStruct 1.5.5.Final; Lombok 1.18.32
- Frontend: React 18, Vite 5, Ant Design 5, TypeScript 5

## Architectural Pattern
- Modular monolith with DDD boundaries per business capability
- Clear module boundaries enforced via dependencies and package scanning
- Profiles: development (H2), docker (Postgres/MinIO), prod

## Core Components
- apex-core: security (JWT), exceptions, config (ApexProperties), shared utilities
- apex-api-gateway: HTTP API, OpenAPI config, scans entities/repositories from other modules
- apex-document-processing: CQRS projection entity/repo, services for document status and analytics
- apex-vendor-management, apex-financial-operations, apex-contract-management, apex-integration: domain-specific APIs and services

## Security
- JWT-based auth with `JwtRequestFilter` and `SecurityConfig`
- Dev profile whitelists: /actuator/**, /swagger-ui.html, /swagger-ui/**, /v3/api-docs/**, /v3/api-docs.yaml

## Data Architecture
- Entities in domain modules; projections in document-processing
- Spring Data repositories; MapStruct mappings; Flyway placeholder (dev uses create-drop)

## Deployment
- Docker Compose for Postgres/Redis/Kafka/MinIO; app runs with `docker` profile

## Extension Points
- New modules plug into gateway via dependency and component scan
- Properties under `apex.*` with configuration processor

## Testing
- Spring Boot test, Testcontainers (Postgres)

## Governance
- Keep module APIs stable; enforce security and config conventions
- CI: generate/update this blueprint as the code evolves
