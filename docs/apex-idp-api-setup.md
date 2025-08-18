I'll walk you through setting up a robust Spring Boot API framework that perfectly aligns with your Apex IDP platform requirements. Let me build this foundation systematically, explaining each architectural decision as we go, so your team can start developing immediately with confidence.

## Project Structure Foundation

Let's begin by establishing a Domain-Driven Design structure that mirrors your bounded contexts from the SRS. This organization will help your team maintain clear boundaries between different business domains while enabling smooth collaboration.

```
apex-idp-api/
├── apex-core/                          # Shared kernel and common utilities
│   ├── src/main/java/com/apex/core/
│   │   ├── domain/                     # Base domain entities and value objects
│   │   ├── events/                     # Event sourcing base classes
│   │   ├── exceptions/                 # Custom exception hierarchy
│   │   ├── security/                   # Security utilities and JWT handling
│   │   ├── utils/                      # Common utilities
│   │   └── config/                     # Shared configuration classes
│   └── pom.xml
│
├── apex-vendor-management/              # Vendor Management Bounded Context
│   ├── src/main/java/com/apex/vendor/
│   │   ├── domain/                     # Vendor aggregate and entities
│   │   ├── application/                # Application services and use cases
│   │   ├── infrastructure/             # Repository implementations
│   │   ├── api/                        # REST controllers
│   │   └── events/                     # Vendor-specific events
│   └── pom.xml
│
├── apex-document-processing/            # Document Processing Bounded Context
│   ├── src/main/java/com/apex/document/
│   │   ├── domain/                     # Document aggregate
│   │   ├── application/                # OCR, classification services
│   │   ├── infrastructure/             # MinIO integration, ML services
│   │   ├── api/                        # Document upload/processing endpoints
│   │   └── events/                     # Processing events
│   └── pom.xml
│
├── apex-financial-operations/           # Financial Operations Bounded Context
│   ├── src/main/java/com/apex/financial/
│   │   ├── domain/                     # Invoice, Payment aggregates
│   │   ├── application/                # Payment modeling, cash flow services
│   │   ├── infrastructure/             # Financial calculations engine
│   │   ├── api/                        # Dashboard and analytics endpoints
│   │   └── events/                     # Financial events
│   └── pom.xml
│
├── apex-contract-management/            # Contract Lifecycle Management Context
│   └── src/main/java/com/apex/contract/
│
├── apex-integration/                    # Integration Context
│   └── src/main/java/com/apex/integration/
│
└── apex-api-gateway/                   # API Gateway and orchestration
    └── src/main/java/com/apex/gateway/
```

## Parent POM Configuration

Let me show you the parent POM that establishes your technical foundation. This configuration brings together all the technologies mentioned in your SRS while maintaining version consistency across modules.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.apex</groupId>
    <artifactId>apex-idp-parent</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
        <testcontainers.version>1.19.3</testcontainers.version>
        <mapstruct.version>1.5.5.Final</mapstruct.version>
        <jjwt.version>0.12.3</jjwt.version>
        <minio.version>8.5.7</minio.version>
        <kafka.version>3.6.0</kafka.version>
    </properties>
    
    <modules>
        <module>apex-core</module>
        <module>apex-vendor-management</module>
        <module>apex-document-processing</module>
        <module>apex-financial-operations</module>
        <module>apex-contract-management</module>
        <module>apex-integration</module>
        <module>apex-api-gateway</module>
    </modules>
    
    <dependencyManagement>
        <dependencies>
            <!-- Spring Cloud dependencies for microservices -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            
            <!-- Event Sourcing with Kafka -->
            <dependency>
                <groupId>org.springframework.kafka</groupId>
                <artifactId>spring-kafka</artifactId>
                <version>${kafka.version}</version>
            </dependency>
            
            <!-- Document storage with MinIO -->
            <dependency>
                <groupId>io.minio</groupId>
                <artifactId>minio</artifactId>
                <version>${minio.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

## Core Domain Model with Event Sourcing

Now, let's establish the event sourcing foundation that will provide complete audit trails as required by your healthcare compliance needs. This base structure ensures every change in your system is tracked immutably.

```java
// apex-core/src/main/java/com/apex/core/domain/AggregateRoot.java
package com.apex.core.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.domain.DomainEvents;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Base class for all aggregate roots in the system.
 * Implements event sourcing pattern to maintain complete audit trail
 * as required for HIPAA compliance and healthcare regulations.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class AggregateRoot<T extends AggregateRoot<T>> 
        extends AbstractAggregateRoot<T> {
    
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Version
    private Long version;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;
    
    @Column(name = "modified_at")
    private Instant modifiedAt;
    
    @Column(name = "modified_by")
    private String modifiedBy;
    
    @Transient
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    protected AggregateRoot() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }
    
    /**
     * Registers a domain event that will be published when the aggregate is saved.
     * This ensures our event store maintains a complete history of all changes.
     */
    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
        // This will be published via Spring's @DomainEvents mechanism
        registerEvent((Object) event);
    }
    
    /**
     * Apply an event to reconstruct aggregate state.
     * Used when replaying events from the event store.
     */
    public abstract void apply(DomainEvent event);
    
    @DomainEvents
    protected List<DomainEvent> domainEvents() {
        return new ArrayList<>(domainEvents);
    }
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.createdBy = getCurrentUserId();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.modifiedAt = Instant.now();
        this.modifiedBy = getCurrentUserId();
    }
    
    private String getCurrentUserId() {
        // Extract from security context - we'll implement this shortly
        return SecurityContextHolder.getCurrentUserId();
    }
}
```

```java
// apex-core/src/main/java/com/apex/core/events/DomainEvent.java
package com.apex.core.events;

import lombok.Getter;
import java.time.Instant;
import java.util.UUID;

/**
 * Base domain event that captures all the necessary metadata
 * for audit trails and event replay capabilities.
 */
@Getter
public abstract class DomainEvent {
    private final UUID eventId;
    private final UUID aggregateId;
    private final String eventType;
    private final Instant occurredAt;
    private final String userId;
    private final Long aggregateVersion;
    
    protected DomainEvent(UUID aggregateId, Long aggregateVersion) {
        this.eventId = UUID.randomUUID();
        this.aggregateId = aggregateId;
        this.eventType = this.getClass().getSimpleName();
        this.occurredAt = Instant.now();
        this.userId = SecurityContextHolder.getCurrentUserId();
        this.aggregateVersion = aggregateVersion;
    }
}
```

## Security Configuration with JWT

Your healthcare platform requires robust security from the ground up. Let's implement the JWT-based authentication system that will protect all your API endpoints while maintaining the role-based access control specified in your requirements.

```java
// apex-core/src/main/java/com/apex/core/security/SecurityConfig.java
package com.apex.core.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Security configuration implementing HIPAA-compliant authentication
 * with JWT tokens and role-based access control.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint jwtAuthEntryPoint;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())  // We're using JWT tokens
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/login", "/api/auth/refresh").permitAll()
                .requestMatchers("/api/health", "/api/metrics").permitAll()
                
                // Role-based access control
                .requestMatchers("/api/admin/**").hasRole("IT_ADMIN")
                .requestMatchers("/api/executive/**").hasRole("CFO")
                .requestMatchers("/api/validation/**").hasAnyRole("AP_CLERK", "SUPERVISOR")
                .requestMatchers("/api/vendor/**").hasRole("VENDOR")
                
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> 
                ex.authenticationEntryPoint(jwtAuthEntryPoint))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
    
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Your Next.js app
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
```

```java
// apex-core/src/main/java/com/apex/core/security/JwtService.java
package com.apex.core.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * JWT token management service implementing the 15-minute token expiration
 * and refresh token pattern as specified in the requirements.
 */
@Slf4j
@Service
public class JwtService {
    
    private final SecretKey key;
    private final long accessTokenExpiration = 15; // 15 minutes as per requirements
    private final long refreshTokenExpiration = 7 * 24 * 60; // 7 days
    
    public JwtService(@Value("${apex.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    public String generateAccessToken(UserPrincipal user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(accessTokenExpiration, ChronoUnit.MINUTES);
        
        return Jwts.builder()
            .setSubject(user.getUserId().toString())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiry))
            .claim("email", user.getEmail())
            .claim("role", user.getRole())
            .claim("hospitalId", user.getHospitalId())
            .signWith(key)
            .compact();
    }
    
    public String generateRefreshToken(UUID userId) {
        Instant now = Instant.now();
        Instant expiry = now.plus(refreshTokenExpiration, ChronoUnit.MINUTES);
        
        return Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiry))
            .claim("type", "refresh")
            .signWith(key)
            .compact();
    }
    
    public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            throw new TokenExpiredException("Token has expired");
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token");
        }
    }
}
```

## Document Processing Service Architecture

Now let's implement the document processing bounded context, which is central to your IDP platform. This service handles everything from document upload to OCR processing and AI classification.

```java
// apex-document-processing/src/main/java/com/apex/document/domain/Document.java
package com.apex.document.domain;

import com.apex.core.domain.AggregateRoot;
import com.apex.document.domain.events.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

/**
 * Document aggregate root representing an invoice or other document
 * going through the IDP pipeline.
 */
@Entity
@Table(name = "documents")
@Getter
@NoArgsConstructor
public class Document extends AggregateRoot<Document> {
    
    @Enumerated(EnumType.STRING)
    private DocumentType type;
    
    @Enumerated(EnumType.STRING)
    private DocumentStatus status;
    
    @Enumerated(EnumType.STRING)
    private ProcessingStage stage;
    
    @Column(name = "vendor_id")
    private UUID vendorId;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "mime_type")
    private String mimeType;
    
    @Column(name = "storage_path")
    private String storagePath;
    
    @Column(name = "page_count")
    private Integer pageCount;
    
    // Classification confidence score
    @Column(name = "classification_confidence")
    private BigDecimal classificationConfidence;
    
    // Overall extraction confidence
    @Column(name = "extraction_confidence")
    private BigDecimal extractionConfidence;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private List<ExtractedField> extractedFields = new ArrayList<>();
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private List<ProcessingError> processingErrors = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "document_metadata")
    private Map<String, String> metadata = new HashMap<>();
    
    /**
     * Factory method to create a new document from an upload.
     * This triggers the DocumentUploaded event for the event store.
     */
    public static Document createFromUpload(
            String fileName,
            Long fileSize,
            String mimeType,
            String storagePath,
            UUID vendorId) {
        
        Document document = new Document();
        document.fileName = fileName;
        document.fileSize = fileSize;
        document.mimeType = mimeType;
        document.storagePath = storagePath;
        document.vendorId = vendorId;
        document.status = DocumentStatus.UPLOADED;
        document.stage = ProcessingStage.PENDING_OCR;
        
        // Register the domain event for event sourcing
        document.registerEvent(new DocumentUploadedEvent(
            document.getId(),
            document.getVersion(),
            fileName,
            vendorId
        ));
        
        return document;
    }
    
    /**
     * Start OCR processing for this document.
     * Validates that the document is in the correct state.
     */
    public void startOcrProcessing() {
        if (this.stage != ProcessingStage.PENDING_OCR) {
            throw new IllegalStateException(
                "Document must be in PENDING_OCR stage to start OCR processing"
            );
        }
        
        this.stage = ProcessingStage.OCR_IN_PROGRESS;
        this.status = DocumentStatus.PROCESSING;
        
        registerEvent(new OcrProcessingStartedEvent(
            this.getId(),
            this.getVersion()
        ));
    }
    
    /**
     * Complete OCR processing with extracted text.
     * Moves document to classification stage.
     */
    public void completeOcrProcessing(String extractedText, Integer pageCount) {
        if (this.stage != ProcessingStage.OCR_IN_PROGRESS) {
            throw new IllegalStateException(
                "Document must be in OCR_IN_PROGRESS stage to complete OCR"
            );
        }
        
        this.pageCount = pageCount;
        this.stage = ProcessingStage.PENDING_CLASSIFICATION;
        this.metadata.put("extracted_text_length", String.valueOf(extractedText.length()));
        
        registerEvent(new OcrProcessingCompletedEvent(
            this.getId(),
            this.getVersion(),
            pageCount
        ));
    }
    
    /**
     * Apply AI classification to determine document type.
     * If confidence is below threshold, route to manual classification.
     */
    public void applyClassification(DocumentType type, BigDecimal confidence) {
        this.type = type;
        this.classificationConfidence = confidence;
        
        // Route based on confidence threshold (70% as per requirements)
        if (confidence.compareTo(new BigDecimal("0.70")) >= 0) {
            this.stage = ProcessingStage.PENDING_EXTRACTION;
            registerEvent(new DocumentClassifiedEvent(
                this.getId(),
                this.getVersion(),
                type,
                confidence
            ));
        } else {
            this.stage = ProcessingStage.PENDING_MANUAL_CLASSIFICATION;
            registerEvent(new DocumentNeedsManualClassificationEvent(
                this.getId(),
                this.getVersion(),
                confidence
            ));
        }
    }
    
    /**
     * Add extracted field data from AI processing.
     * Each field includes its own confidence score.
     */
    public void addExtractedField(ExtractedField field) {
        this.extractedFields.add(field);
        recalculateExtractionConfidence();
        
        registerEvent(new FieldExtractedEvent(
            this.getId(),
            this.getVersion(),
            field.getFieldName(),
            field.getConfidence()
        ));
    }
    
    /**
     * Validate extracted data and determine if human review is needed.
     * Uses 80% threshold as specified in requirements.
     */
    public void validateExtraction() {
        BigDecimal threshold = new BigDecimal("0.80");
        
        boolean needsReview = extractedFields.stream()
            .anyMatch(field -> field.getConfidence().compareTo(threshold) < 0);
        
        if (needsReview) {
            this.stage = ProcessingStage.PENDING_VALIDATION;
            this.status = DocumentStatus.PENDING_REVIEW;
            registerEvent(new DocumentNeedsValidationEvent(
                this.getId(),
                this.getVersion(),
                this.extractionConfidence
            ));
        } else {
            this.stage = ProcessingStage.COMPLETED;
            this.status = DocumentStatus.APPROVED;
            registerEvent(new DocumentAutoApprovedEvent(
                this.getId(),
                this.getVersion()
            ));
        }
    }
    
    private void recalculateExtractionConfidence() {
        if (extractedFields.isEmpty()) {
            this.extractionConfidence = BigDecimal.ZERO;
            return;
        }
        
        BigDecimal sum = extractedFields.stream()
            .map(ExtractedField::getConfidence)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.extractionConfidence = sum.divide(
            new BigDecimal(extractedFields.size()),
            2,
            BigDecimal.ROUND_HALF_UP
        );
    }
    
    @Override
    public void apply(DomainEvent event) {
        // Event replay logic for event sourcing
        // This allows us to reconstruct state from events
        switch (event) {
            case DocumentUploadedEvent e -> {
                this.fileName = e.getFileName();
                this.vendorId = e.getVendorId();
                this.status = DocumentStatus.UPLOADED;
            }
            case OcrProcessingCompletedEvent e -> {
                this.pageCount = e.getPageCount();
                this.stage = ProcessingStage.PENDING_CLASSIFICATION;
            }
            case DocumentClassifiedEvent e -> {
                this.type = e.getDocumentType();
                this.classificationConfidence = e.getConfidence();
            }
            // Handle other events...
            default -> log.warn("Unknown event type: {}", event.getClass());
        }
    }
}
```

## Application Service Layer with CQRS

Let's implement the application service layer using CQRS pattern to separate our read and write operations. This provides better scalability and allows us to optimize each side independently.

```java
// apex-document-processing/src/main/java/com/apex/document/application/DocumentCommandService.java
package com.apex.document.application;

import com.apex.core.events.EventPublisher;
import com.apex.document.domain.Document;
import com.apex.document.domain.DocumentRepository;
import com.apex.document.infrastructure.storage.DocumentStorageService;
import com.apex.document.infrastructure.ocr.OcrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Command service handling all write operations for documents.
 * This is the write side of our CQRS implementation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentCommandService {
    
    private final DocumentRepository documentRepository;
    private final DocumentStorageService storageService;
    private final OcrService ocrService;
    private final EventPublisher eventPublisher;
    private final DocumentProcessingOrchestrator orchestrator;
    
    /**
     * Upload a new document and initiate processing pipeline.
     * This is the entry point for document ingestion.
     */
    @Transactional
    public UUID uploadDocument(MultipartFile file, UUID vendorId) {
        log.info("Uploading document: {} for vendor: {}", file.getOriginalFilename(), vendorId);
        
        // Validate file
        validateFile(file);
        
        // Store file in MinIO
        String storagePath = storageService.storeDocument(file);
        
        // Create document aggregate
        Document document = Document.createFromUpload(
            file.getOriginalFilename(),
            file.getSize(),
            file.getContentType(),
            storagePath,
            vendorId
        );
        
        // Save to repository - this will also publish domain events
        document = documentRepository.save(document);
        
        // Initiate async processing pipeline
        orchestrator.startProcessingPipeline(document.getId());
        
        log.info("Document uploaded successfully with ID: {}", document.getId());
        return document.getId();
    }
    
    /**
     * Process OCR for a document.
     * Called by the processing pipeline orchestrator.
     */
    @Transactional
    public void processOcr(UUID documentId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new DocumentNotFoundException(documentId));
        
        try {
            document.startOcrProcessing();
            documentRepository.save(document);
            
            // Perform OCR using ensemble of engines
            OcrResult result = ocrService.performOcr(document.getStoragePath());
            
            document.completeOcrProcessing(result.getText(), result.getPageCount());
            documentRepository.save(document);
            
            // Continue to next stage
            orchestrator.proceedToClassification(documentId);
            
        } catch (Exception e) {
            log.error("OCR processing failed for document: {}", documentId, e);
            document.recordProcessingError("OCR_FAILED", e.getMessage());
            documentRepository.save(document);
        }
    }
    
    /**
     * Apply AI classification to determine document type.
     */
    @Transactional
    public void classifyDocument(UUID documentId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new DocumentNotFoundException(documentId));
        
        ClassificationResult result = aiClassificationService.classify(document);
        
        document.applyClassification(result.getType(), result.getConfidence());
        documentRepository.save(document);
        
        if (document.getStage() == ProcessingStage.PENDING_EXTRACTION) {
            orchestrator.proceedToExtraction(documentId);
        }
    }
    
    /**
     * Validate document after human review.
     * This is called when an AP Clerk approves/corrects extracted data.
     */
    @Transactional
    public void validateDocument(UUID documentId, ValidationRequest request) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new DocumentNotFoundException(documentId));
        
        // Apply field corrections
        request.getFieldCorrections().forEach(correction -> {
            document.correctField(correction.getFieldName(), correction.getCorrectedValue());
        });
        
        // Update validation status
        if (request.isApproved()) {
            document.approve(request.getUserId());
        } else {
            document.reject(request.getUserId(), request.getRejectionReason());
        }
        
        documentRepository.save(document);
        
        // If approved, send to ERP integration
        if (document.getStatus() == DocumentStatus.APPROVED) {
            integrationService.sendToErp(documentId);
        }
    }
    
    private void validateFile(MultipartFile file) {
        // Check file size (50MB limit as per requirements)
        if (file.getSize() > 50 * 1024 * 1024) {
            throw new FileTooLargeException("File exceeds 50MB limit");
        }
        
        // Check file type
        String contentType = file.getContentType();
        if (!isValidFileType(contentType)) {
            throw new InvalidFileTypeException("Unsupported file type: " + contentType);
        }
        
        // Virus scanning would go here
        virusScanService.scan(file);
    }
}
```

```java
// apex-document-processing/src/main/java/com/apex/document/application/DocumentQueryService.java
package com.apex.document.application;

import com.apex.document.application.dto.DocumentDTO;
import com.apex.document.application.dto.ValidationQueueDTO;
import com.apex.document.infrastructure.projection.DocumentProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Query service for document read operations.
 * This is the read side of our CQRS implementation, optimized for queries.
 */
@Service
@RequiredArgsConstructor
public class DocumentQueryService {
    
    private final DocumentProjectionRepository projectionRepository;
    private final DocumentSearchService searchService;
    
    /**
     * Get documents pending validation for AP Clerks.
     * Returns documents with confidence below threshold that need review.
     */
    public Page<ValidationQueueDTO> getValidationQueue(Pageable pageable) {
        return projectionRepository.findByStatusAndConfidenceLessThan(
            DocumentStatus.PENDING_REVIEW,
            new BigDecimal("0.80"),
            pageable
        ).map(this::toValidationQueueDTO);
    }
    
    /**
     * Get document details with extracted fields for validation UI.
     */
    public DocumentDTO getDocumentForValidation(UUID documentId) {
        DocumentProjection projection = projectionRepository.findById(documentId)
            .orElseThrow(() -> new DocumentNotFoundException(documentId));
        
        return DocumentDTO.builder()
            .id(projection.getId())
            .fileName(projection.getFileName())
            .documentType(projection.getType())
            .status(projection.getStatus())
            .vendorName(projection.getVendorName())
            .uploadedAt(projection.getCreatedAt())
            .extractedFields(mapExtractedFields(projection.getExtractedFields()))
            .documentImageUrl(generateSecureUrl(projection.getStoragePath()))
            .overallConfidence(projection.getExtractionConfidence())
            .processingStage(projection.getStage())
            .build();
    }
    
    /**
     * Search documents using Elasticsearch for full-text search.
     */
    public Page<DocumentDTO> searchDocuments(String query, Pageable pageable) {
        return searchService.search(query, pageable)
            .map(this::toDocumentDTO);
    }
    
    /**
     * Get processing statistics for dashboard.
     */
    public ProcessingStatistics getProcessingStatistics() {
        return ProcessingStatistics.builder()
            .totalDocuments(projectionRepository.count())
            .pendingValidation(projectionRepository.countByStatus(DocumentStatus.PENDING_REVIEW))
            .processingToday(projectionRepository.countByCreatedAtAfter(LocalDate.now().atStartOfDay()))
            .averageConfidence(projectionRepository.calculateAverageConfidence())
            .processingErrors(projectionRepository.countByStatus(DocumentStatus.ERROR))
            .build();
    }
}
```

## REST Controller Layer

Now let's create the REST API endpoints that your frontend will interact with. These controllers implement the OpenAPI 3.0 specification mentioned in your requirements.

```java
// apex-document-processing/src/main/java/com/apex/document/api/DocumentController.java
package com.apex.document.api;

import com.apex.core.api.ApiResponse;
import com.apex.document.application.DocumentCommandService;
import com.apex.document.application.DocumentQueryService;
import com.apex.document.application.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for document processing operations.
 * Implements the document upload and processing endpoints defined in the user stories.
 */
@Slf4j
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Tag(name = "Document Processing", description = "Document upload, processing, and validation endpoints")
@SecurityRequirement(name = "bearer-jwt")
public class DocumentController {
    
    private final DocumentCommandService commandService;
    private final DocumentQueryService queryService;
    private final WebSocketNotificationService notificationService;
    
    /**
     * Upload a single document for processing.
     * Implements Story 2.1 and 2.2 from the epic breakdown.
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a document for processing")
    @PreAuthorize("hasAnyRole('AP_CLERK', 'VENDOR')")
    public ApiResponse<DocumentUploadResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "vendorId", required = false) UUID vendorId,
            @RequestParam(value = "priority", defaultValue = "NORMAL") String priority) {
        
        log.info("Received document upload request: {}", file.getOriginalFilename());
        
        // If vendor is uploading, use their ID from security context
        if (vendorId == null && SecurityUtils.hasRole("VENDOR")) {
            vendorId = SecurityUtils.getCurrentVendorId();
        }
        
        UUID documentId = commandService.uploadDocument(file, vendorId);
        
        // Send real-time notification about upload
        notificationService.notifyDocumentUploaded(documentId);
        
        return ApiResponse.success(
            DocumentUploadResponse.builder()
                .documentId(documentId)
                .fileName(file.getOriginalFilename())
                .status("UPLOADED")
                .message("Document uploaded successfully and queued for processing")
                .build()
        );
    }
    
    /**
     * Upload multiple documents for batch processing.
     * Implements Story 2.3 from the epic breakdown.
     */
    @PostMapping(value = "/upload/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload multiple documents for batch processing")
    @PreAuthorize("hasRole('AP_CLERK')")
    public ApiResponse<BatchUploadResponse> uploadBatch(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "batchName") String batchName,
            @RequestParam(value = "vendorId", required = false) UUID vendorId) {
        
        log.info("Received batch upload request: {} files", files.size());
        
        BatchUploadResult result = commandService.uploadBatch(files, batchName, vendorId);
        
        return ApiResponse.success(
            BatchUploadResponse.builder()
                .batchId(result.getBatchId())
                .totalFiles(files.size())
                .successfulUploads(result.getSuccessfulUploads())
                .failedUploads(result.getFailedUploads())
                .documentIds(result.getDocumentIds())
                .build()
        );
    }
    
    /**
     * Get documents pending validation.
     * Implements Story 5.1 and 5.2 from the epic breakdown.
     */
    @GetMapping("/validation/queue")
    @Operation(summary = "Get documents pending validation")
    @PreAuthorize("hasAnyRole('AP_CLERK', 'SUPERVISOR')")
    public ApiResponse<Page<ValidationQueueDTO>> getValidationQueue(
            @RequestParam(value = "confidenceThreshold", defaultValue = "0.80") BigDecimal threshold,
            Pageable pageable) {
        
        Page<ValidationQueueDTO> queue = queryService.getValidationQueue(pageable);
        return ApiResponse.success(queue);
    }
    
    /**
     * Get document details for validation.
     * Returns document with extracted fields and confidence scores.
     */
    @GetMapping("/{documentId}/validation")
    @Operation(summary = "Get document details for validation")
    @PreAuthorize("hasAnyRole('AP_CLERK', 'SUPERVISOR')")
    public ApiResponse<DocumentValidationDTO> getDocumentForValidation(
            @PathVariable UUID documentId) {
        
        DocumentValidationDTO document = queryService.getDocumentForValidation(documentId);
        
        // Lock document for current user to prevent concurrent editing
        commandService.lockDocumentForValidation(documentId, SecurityUtils.getCurrentUserId());
        
        return ApiResponse.success(document);
    }
    
    /**
     * Submit validation results for a document.
     * Implements Story 5.2 from the epic breakdown.
     */
    @PostMapping("/{documentId}/validate")
    @Operation(summary = "Submit validation results for a document")
    @PreAuthorize("hasAnyRole('AP_CLERK', 'SUPERVISOR')")
    public ApiResponse<Void> validateDocument(
            @PathVariable UUID documentId,
            @RequestBody @Valid ValidationRequest request) {
        
        log.info("Validating document: {} by user: {}", documentId, SecurityUtils.getCurrentUserId());
        
        commandService.validateDocument(documentId, request);
        
        // Send notification about validation completion
        notificationService.notifyValidationComplete(documentId, request.isApproved());
        
        return ApiResponse.success("Document validated successfully");
    }
    
    /**
     * Get real-time processing status for a document.
     * Used by frontend to show processing progress.
     */
    @GetMapping("/{documentId}/status")
    @Operation(summary = "Get document processing status")
    public ApiResponse<DocumentStatusDTO> getDocumentStatus(@PathVariable UUID documentId) {
        DocumentStatusDTO status = queryService.getDocumentStatus(documentId);
        return ApiResponse.success(status);
    }
    
    /**
     * Search documents using full-text search.
     * Implements search functionality across all processed documents.
     */
    @GetMapping("/search")
    @Operation(summary = "Search documents")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Page<DocumentSearchResultDTO>> searchDocuments(
            @RequestParam("query") String query,
            @RequestParam(value = "type", required = false) DocumentType type,
            @RequestParam(value = "vendorId", required = false) UUID vendorId,
            @RequestParam(value = "dateFrom", required = false) LocalDate dateFrom,
            @RequestParam(value = "dateTo", required = false) LocalDate dateTo,
            Pageable pageable) {
        
        SearchCriteria criteria = SearchCriteria.builder()
            .query(query)
            .documentType(type)
            .vendorId(vendorId)
            .dateFrom(dateFrom)
            .dateTo(dateTo)
            .build();
        
        Page<DocumentSearchResultDTO> results = queryService.searchDocuments(criteria, pageable);
        return ApiResponse.success(results);
    }
}
```

## WebSocket Configuration for Real-time Updates

Your platform requires real-time updates for document processing status. Let's implement the WebSocket configuration that will power these live updates.

```java
// apex-core/src/main/java/com/apex/core/websocket/WebSocketConfig.java
package com.apex.core.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * WebSocket configuration for real-time notifications.
 * Implements Story 10.1 from the epic breakdown.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple broker for topic and queue destinations
        config.enableSimpleBroker("/topic", "/queue");
        // Set application destination prefix for client messages
        config.setApplicationDestinationPrefixes("/app");
        // Set user destination prefix for user-specific messages
        config.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register STOMP endpoint with SockJS fallback
        registry.addEndpoint("/ws")
            .setAllowedOrigins("http://localhost:3000") // Your Next.js app
            .withSockJS();
    }
    
    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
            .nullDestMatcher().authenticated()
            .simpDestMatchers("/app/**").authenticated()
            .simpSubscribeDestMatchers("/topic/**", "/queue/**", "/user/**").authenticated()
            .anyMessage().denyAll();
    }
}
```

## Application Properties Configuration

Finally, let's set up the application properties that configure all the services we've defined.

```yaml
# apex-api-gateway/src/main/resources/application.yml
spring:
  application:
    name: apex-api-gateway
  
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:development}
  
  # Database configuration
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:5432/apex_idp
    username: ${DB_USERNAME:apex_user}
    password: ${DB_PASSWORD:secure_password}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
  
  # JPA configuration
  jpa:
    hibernate:
      ddl-auto: validate  # Use Flyway for migrations
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        jdbc:
          batch_size: 25
        order_inserts: true
        order_updates: true
  
  # Redis configuration for caching and sessions
  redis:
    host: ${REDIS_HOST:localhost}
    port: 6379
    password: ${REDIS_PASSWORD:}
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 10
        max-idle: 5
  
  # Kafka configuration for event streaming
  kafka:
    bootstrap-servers: ${KAFKA_SERVERS:localhost:9092}
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: all
      retries: 3
    consumer:
      group-id: apex-idp-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "com.apex.*"
  
  # File upload configuration
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 200MB  # For batch uploads

# MinIO configuration for document storage
minio:
  endpoint: ${MINIO_ENDPOINT:http://localhost:9000}
  access-key: ${MINIO_ACCESS_KEY:minioadmin}
  secret-key: ${MINIO_SECRET_KEY:minioadmin}
  bucket-name: apex-documents
  secure: false

# JWT configuration
apex:
  jwt:
    secret: ${JWT_SECRET:your-256-bit-secret-key-for-production-use-env-variable}
    access-token-expiration: 15  # minutes
    refresh-token-expiration: 10080  # 7 days in minutes

# OCR service configuration
ocr:
  tesseract:
    enabled: true
    language: eng
    dpi: 300
  layoutlm:
    enabled: true
    model-path: ${LAYOUTLM_MODEL_PATH:/models/layoutlmv3}
  confidence-threshold: 0.80

# Integration endpoints
integration:
  cpsi:
    enabled: ${CPSI_ENABLED:false}
    endpoint: ${CPSI_ENDPOINT:}
    api-key: ${CPSI_API_KEY:}
  eclinicalworks:
    enabled: ${ECW_ENABLED:false}
    fhir-endpoint: ${ECW_FHIR_ENDPOINT:}
    client-id: ${ECW_CLIENT_ID:}
    client-secret: ${ECW_CLIENT_SECRET:}

# Monitoring and metrics
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
  health:
    redis:
      enabled: true
    db:
      enabled: true

# Logging configuration
logging:
  level:
    com.apex: DEBUG
    org.springframework.security: DEBUG
    org.springframework.web: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/apex-idp.log
```

## Testing Infrastructure

Let's also set up the testing infrastructure so your team can write tests from day one.

```java
// apex-core/src/test/java/com/apex/core/test/BaseIntegrationTest.java
package com.apex.core.test;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Base class for integration tests using Testcontainers.
 * Provides real PostgreSQL, Kafka, and Redis instances for testing.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("apex_test")
        .withUsername("test")
        .withPassword("test");
    
    static KafkaContainer kafka = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:latest")
    );
    
    static GenericContainer<?> redis = new GenericContainer<>("redis:7")
        .withExposedPorts(6379);
    
    static {
        postgres.start();
        kafka.start();
        redis.start();
    }
    
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
    }
}
```

## Summary and Next Steps

This Spring Boot framework provides a solid foundation aligned with your Software Requirements Specification. The architecture implements:

1. **Domain-Driven Design** with clear bounded contexts matching your business domains
2. **Event Sourcing** for complete audit trails required by healthcare compliance
3. **CQRS Pattern** to optimize read and write operations independently
4. **JWT-based Security** with role-based access control for all user types
5. **WebSocket Support** for real-time notifications and updates
6. **Comprehensive Testing** infrastructure with Testcontainers
7. **Cloud-Native Architecture** ready for Kubernetes deployment

Your development team can now start implementing the user stories immediately. Each bounded context can be developed independently by different team members, and the event-driven architecture ensures loose coupling between services.

The modular structure allows you to deploy services incrementally - you could start with just the document upload and OCR processing, then add validation workflows, and finally integrate the advanced features like analytics and ERP integration. This aligns perfectly with your phased rollout plan (Foundation → Intelligence → Integration → Scale).

Remember to set up your CI/CD pipeline early to automate testing and deployment. The Testcontainers setup ensures your tests run reliably across different environments, and the configuration externalization makes it easy to deploy to different environments without code changes.