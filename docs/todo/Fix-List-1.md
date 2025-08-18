Collecting workspace informationFiltering to most relevant informationI can see there are multiple build errors in the apex-idp repository. Let me analyze and fix these issues step by step.

Let me first search Google for the latest Spring Boot WebClient configuration to understand how to properly set it up.

<fetch_webpage>
<url>https://www.google.com/search?q=spring+boot+3+webclient+dependency+maven</url>
</fetch_webpage>

Now let me check the specific Spring Boot documentation for WebClient:

<fetch_webpage>
<url>https://docs.spring.io/spring-framework/reference/web/webflux-webclient.html</url>
</fetch_webpage>

Let me search for more specific information about Spring Boot 3 WebClient setup:

<fetch_webpage>
<url>https://www.google.com/search?q=spring+boot+3.4+webflux+dependency+pom.xml</url>
</fetch_webpage>

Now I'll start fixing the issues systematically. Let me create a todo list for resolving all the problems:

```markdown
- [ ] Step 1: Add missing Spring WebFlux dependency for WebClient
- [ ] Step 2: Fix OpenAI service model classes and interfaces
- [ ] Step 3: Fix BatchEventProducer syntax errors
- [ ] Step 4: Fix OCR service duplicate definitions
- [ ] Step 5: Fix Storage service interface issues
- [ ] Step 6: Fix WebSocket notification service
- [ ] Step 7: Fix missing methods in domain entities
- [ ] Step 8: Fix JWT token provider method references
- [ ] Step 9: Fix duplicate DTO definitions
- [ ] Step 10: Fix frontend build issue
- [ ] Step 11: Run tests to verify all fixes
```

Let me start with Step 1 - Adding the missing WebFlux dependency:

<write_to_file>
<path>pom.xml</path>
<content><?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.5</version>
        <relativePath/>
    </parent>
    <groupId>com.apex</groupId>
    <artifactId>apex-idp</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    <name>APEX Intelligent Document Processing</name>
    <description>AI-powered document processing for hospital accounts payable</description>
    <!-- Add to properties section -->
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <java.version>17</java.version>
        <node.version>v18.18.0</node.version>
        <npm.version>9.8.1</npm.version>
        <spring-boot.version>3.4.5</spring-boot.version>
        <openai-java.version>0.18.2</openai-java.version>
        <okhttp.version>4.12.0</okhttp.version>
        <frontend-maven-plugin.version>1.13.4</frontend-maven-plugin.version>
    </properties>
    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>
        <!-- Add WebFlux for WebClient -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>
        <!-- Kafka -->
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!-- Redis -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <!-- Database -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <!-- Liquibase -->
        <dependency>
            <groupId>org.liquibase</groupId>
            <artifactId>liquibase-core</artifactId>
        </dependency>
        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.11.5</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        <!-- MinIO -->
        <dependency>
            <groupId>io.minio</groupId>
            <artifactId>minio</artifactId>
            <version>8.5.7</version>
        </dependency>
        <!-- Apache Commons -->
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>2.11.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
        </dependency>
        <!-- OpenAI -->
        <dependency>
            <groupId>com.theokanning.openai-gpt3-java</groupId>
            <artifactId>service</artifactId>
            <version>${openai-java.version}</version>
        </dependency>
        <!-- OkHttp (for OpenAI client) -->
        <dependency>
            <groupId>com.squareup.okhttp3</groupId>
            <artifactId>okhttp</artifactId>
            <version>${okhttp.version}</version>
        </dependency>
        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <!-- Swagger/OpenAPI -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.5.0</version>
        </dependency>
        <!-- Retry -->
        <dependency>
            <groupId>org.springframework.retry</groupId>
            <artifactId>spring-retry</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aspects</artifactId>
        </dependency>
        <!-- PDF Processing -->
        <dependency>
            <groupId>org.apache.pdfbox</groupId>
            <artifactId>pdfbox</artifactId>
            <version>3.0.0</version>
        </dependency>
        <!-- Test dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>testcontainers</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <!-- Frontend build plugin -->
            <plugin>
                <groupId>com.github.eirslett</groupId>
                <artifactId>frontend-maven-plugin</artifactId>
                <version>${frontend-maven-plugin.version}</version>
                <configuration>
                    <workingDirectory>frontend</workingDirectory>
                </configuration>
                <executions>
                    <execution>
                        <id>install node and npm</id>
                        <goals>
                            <goal>install-node-and-npm</goal>
                        </goals>
                        <configuration>
                            <nodeVersion>${node.version}</nodeVersion>
                            <npmVersion>${npm.version}</npmVersion>
                        </configuration>
                    </execution>
                    <execution>
                        <id>npm install</id>
                        <goals>
                            <goal>npm</goal>
                        </goals>
                        <configuration>
                            <arguments>install</arguments>
                        </configuration>
                    </execution>
                    <execution>
                        <id>npm run build</id>
                        <goals>
                            <goal>npm</goal>
                        </goals>
                        <configuration>
                            <arguments>run build</arguments>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <!-- Copy frontend build to Spring static resources -->
            <plugin>
                <artifactId>maven-resources-plugin</artifactId>
                <executions>
                    <execution>
                        <id>copy-frontend-build</id>
                        <phase>process-resources</phase>
                        <goals>
                            <goal>copy-resources</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>${project.basedir}/src/main/resources/static</outputDirectory>
                            <overwrite>true</overwrite>
                            <resources>
                                <resource>
                                    <directory>${project.basedir}/frontend/out</directory>
                                    <excludes>
                                        <exclude>**/*.map</exclude>
                                    </excludes>
                                </resource>
                            </resources>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
</content>
</write_to_file>

```markdown
- [x] Step 1: Add missing Spring WebFlux dependency for WebClient
- [ ] Step 2: Fix OpenAI service model classes and interfaces
- [ ] Step 3: Fix BatchEventProducer syntax errors
- [ ] Step 4: Fix OCR service duplicate definitions
- [ ] Step 5: Fix Storage service interface issues
- [ ] Step 6: Fix WebSocket notification service
- [ ] Step 7: Fix missing methods in domain entities
- [ ] Step 8: Fix JWT token provider method references
- [ ] Step 9: Fix duplicate DTO definitions
- [ ] Step 10: Fix frontend build issue
- [ ] Step 11: Run tests to verify all fixes
```

Now let me create the missing OpenAI model classes:

<write_to_file>
<path>src/main/java/com/apex/idp/infrastructure/ai/model/AnalysisResult.java</path>
<content>package com.apex.idp.infrastructure.ai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResult {
    private String summary;
    private double confidenceScore;
    private List<String> keyInsights;
    private Map<String, Object> extractedData;
    private List<String> warnings;
    private int documentCount;
    private long processingTimeMs;
    private List<DocumentReference> references;
}
</content>
</write_to_file>

<write_to_file>
<path>src/main/java/com/apex/idp/infrastructure/ai/model/ChatContext.java</path>
<content>package com.apex.idp.infrastructure.ai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatContext {
    private Long invoiceId;
    private Long batchId;
    private Map<String, Object> metadata;
    private String contextType;
    private String userId;
}
</content>
</write_to_file>

<write_to_file>
<path>src/main/java/com/apex/idp/infrastructure/ai/model/ChatMessage.java</path>
<content>package com.apex.idp.infrastructure.ai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String role;
    private String content;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
}
</content>
</write_to_file>

<write_to_file>
<path>src/main/java/com/apex/idp/infrastructure/ai/model/ChatResponse.java</path>
<content>package com.apex.idp.infrastructure.ai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String message;
    private List<DocumentReference> references;
    private Map<String, Object> metadata;
    private double confidenceScore;
    private String responseType;
}
</content>
</write_to_file>

<write_to_file>
<path>src/main/java/com/apex/idp/infrastructure/ai/model/DocumentReference.java</path>
<content>package com.apex.idp.infrastructure.ai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentReference {
    private Long documentId;
    private String documentType;
    private String title;
    private String relevantSection;
    private double relevanceScore;
    private String url;
}
</content>
</write_to_file>

<write_to_file>
<path>src/main/java/com/apex/idp/infrastructure/ai/model/InvoiceExtractionResult.java</path>
<content>package com.apex.idp.infrastructure.ai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceExtractionResult {
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private String vendorName;
    private String vendorTaxId;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal subtotal;
    private String currency;
    private List<LineItemData> lineItems;
    private Map<String, String> additionalFields;
    private double confidenceScore;
}
</content>
</write_to_file>

<write_to_file>
<path>src/main/java/com/apex/idp/infrastructure/ai/model/LineItemData.java</path>
<content>package com.apex.idp.infrastructure.ai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineItemData {
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String unit;
    private String productCode;
    private String category;
}
</content>
</write_to_file>

<write_to_file>
<path>src/main/java/com/apex/idp/infrastructure/ai/model/ClassificationResult.java</path>
<content>package com.apex.idp.infrastructure.ai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassificationResult {
    private String documentType;
    private String category;
    private double confidenceScore;
    private Map<String, Double> alternativeClassifications;
    private String language;
    private boolean requiresHumanReview;
}
</content>
</write_to_file>

<write_to_file>
<path>src/main/java/com/apex/idp/infrastructure/ai/model/InvoiceSummary.java</path>
<content>package com.apex.idp.infrastructure.ai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceSummary {
    private String summary;
    private List<String> keyPoints;
    private BigDecimal totalValue;
    private String vendor;
    private String category;
    private Map<String, Object> insights;
    private List<String> anomalies;
}
</content>
</write_to_file>

<write_to_file>
<path>src/main/java/com/apex/idp/infrastructure/ai/AIServiceException.java</path>
<content>package com.apex.idp.infrastructure.ai;

public class AIServiceException extends RuntimeException {
    
    public AIServiceException(String message) {
        super(message);
    }
    
    public AIServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
</content>
</write_to_file>

```markdown
- [x] Step 1: Add missing Spring WebFlux dependency for WebClient
- [x] Step 2: Fix OpenAI service model classes and interfaces
- [ ] Step 3: Fix BatchEventProducer syntax errors
- [ ] Step 4: Fix OCR service duplicate definitions
- [ ] Step 5: Fix Storage service interface issues
- [ ] Step 6: Fix WebSocket notification service
- [ ] Step 7: Fix missing methods in domain entities
- [ ] Step 8: Fix JWT token provider method references
- [ ] Step 9: Fix duplicate DTO definitions
- [ ] Step 10: Fix frontend build issue
- [ ] Step 11: Run tests to verify all fixes
```

Now let me fix the BatchEventProducer syntax errors. First, let me check what's in that file:

<read_file>
<path>src/main/java/com/apex/idp/infrastructure/messaging/BatchEventProducer.java</path>
</read_file>

I see the issue - there's a duplicate package declaration and malformed syntax. Let me fix it:

<write_to_file>
<path>src/main/java/com/apex/idp/infrastructure/messaging/BatchEventProducer.java</path>
<content>package com.apex.idp.infrastructure.messaging;

import com.apex.idp.domain.model.Batch;
import com.apex.idp.interfaces.dto.BatchEventDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Produces batch-related events to Kafka topics.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BatchEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.batch-events:batch-events}")
    private String batchEventsTopic;

    /**
     * Sends a batch created event.
     */
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void sendBatchCreatedEvent(Batch batch) {
        try {
            BatchEventDTO event = BatchEventDTO.builder()
                    .batchId(batch.getId())
                    .name(batch.getName())
                    .status(batch.getStatus().name())
                    .eventType("BATCH_CREATED")
                    .timestamp(System.currentTimeMillis())
                    .build();

            String eventJson = objectMapper.writeValueAsString(event);
            
            CompletableFuture<SendResult<String, String>> future = 
                kafkaTemplate.send(batchEventsTopic, batch.getId().toString(), eventJson);
                
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent batch created event for batch: {}", batch.getId());
                } else {
                    log.error("Failed to send batch created event for batch: {}", batch.getId(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Error sending batch created event", e);
            throw new RuntimeException("Failed to send batch created event", e);
        }
    }

    /**
     * Sends a batch updated event.
     */
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void sendBatchUpdatedEvent(Batch batch) {
        try {
            BatchEventDTO event = BatchEventDTO.builder()
                    .batchId(batch.getId())
                    .name(batch.getName())
                    .status(batch.getStatus().name())
                    .eventType("BATCH_UPDATED")
                    .timestamp(System.currentTimeMillis())
                    .build();

            String eventJson = objectMapper.writeValueAsString(event);
            
            CompletableFuture<SendResult<String, String>> future = 
                kafkaTemplate.send(batchEventsTopic, batch.getId().toString(), eventJson);
                
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent batch updated event for batch: {}", batch.getId());
                } else {
                    log.error("Failed to send batch updated event for batch: {}", batch.getId(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Error sending batch updated event", e);
            throw new RuntimeException("Failed to send batch updated event", e);
        }
    }

    /**
     * Sends a batch completed event.
     */
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void sendBatchCompletedEvent(Batch batch) {
        try {
            BatchEventDTO event = BatchEventDTO.builder()
                    .batchId(batch.getId())
                    .name(batch.getName())
                    .status(batch.getStatus().name())
                    .eventType("BATCH_COMPLETED")
                    .timestamp(System.currentTimeMillis())
                    .build();

            String eventJson = objectMapper.writeValueAsString(event);
            
            CompletableFuture<SendResult<String, String>> future = 
                kafkaTemplate.send(batchEventsTopic, batch.getId().toString(), eventJson);
                
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent batch completed event for batch: {}", batch.getId());
                } else {
                    log.error("Failed to send batch completed event for batch: {}", batch.getId(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Error sending batch completed event", e);
            throw new RuntimeException("Failed to send batch completed event", e);
        }
    }

    /**
     * Sends a batch failed event.
     */
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void sendBatchFailedEvent(Batch batch, String reason) {
        try {
            BatchEventDTO event = BatchEventDTO.builder()
                    .batchId(batch.getId())
                    .name(batch.getName())
                    .status(batch.getStatus().name())
                    .eventType("BATCH_FAILED")
                    .timestamp(System.currentTimeMillis())
                    .build();

            String eventJson = objectMapper.writeValueAsString(event);
            
            CompletableFuture<SendResult<String, String>> future = 
                kafkaTemplate.send(batchEventsTopic, batch.getId().toString(), eventJson);
                
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent batch failed event for batch: {}", batch.getId());
                } else {
                    log.error("Failed to send batch failed event for batch: {}", batch.getId(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Error sending batch failed event", e);
            throw new RuntimeException("Failed to send batch failed event", e);
        }
    }
}
</content>
</write_to_file>

```markdown
- [x] Step 1: Add missing Spring WebFlux dependency for WebClient
- [x] Step 2: Fix OpenAI service model classes and interfaces
- [x] Step 3: Fix BatchEventProducer syntax errors
- [ ] Step 4: Fix OCR service duplicate definitions
- [ ] Step 5: Fix Storage service interface issues
- [ ] Step 6: Fix WebSocket notification service
- [ ] Step 7: Fix missing methods in domain entities
- [ ] Step 8: Fix JWT token provider method references
- [ ] Step 9: Fix duplicate DTO definitions
- [ ] Step 10: Fix frontend build issue
- [ ] Step 11: Run tests to verify all fixes
```

Now let me check the OCR service files to fix the duplicate definitions:

<read_file>
<path>src/main/java/com/apex/idp/infrastructure/ocr/OCRService.java</path>
</read_file>

Let me fix the OCR service interface:

<write_to_file>
<path>src/main/java/com/apex/idp/infrastructure/ocr/OCRService.java</path>
<content>package com.apex.idp.infrastructure.ocr;

import com.apex.idp.domain.model.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for OCR processing services.
 */
public interface OCRService {
    
    /**
     * Performs OCR on a multipart file.
     */
    OCRResult performOCR(MultipartFile file);
    
    /**
     * Performs OCR on an input stream.
     */
    OCRResult performOCR(InputStream inputStream, String fileName, String contentType);
    
    /**
     * Performs OCR on a document.
     */
    OCRResult performOCR(Document document);
    
    /**
     * Performs batch OCR on multiple documents.
     */
    List<OCRResult> performBatchOCR(List<Document> documents);
    
    /**
     * Extracts structured data from text.
     */
    Map<String, Object> extractStructuredData(String text, String documentType);
    
    /**
     * Performs layout-based OCR with options.
     */
    OCRResult performLayoutOCR(MultipartFile file, LayoutOptions options);
    
    /**
     * Validates if file is suitable for OCR.
     */
    boolean validateFile(MultipartFile file);
    
    /**
     * Preprocesses image for better OCR results.
     */
    byte[] preprocessImage(InputStream image, PreprocessingOptions options);
    
    /**
     * Gets supported file formats.
     */
    Set<String> getSupportedFormats();
    
    /**
     * Estimates processing time based on file size and page count.
     */
    long estimateProcessingTime(long fileSize, int pageCount);
    
    /**
     * Extracts text from document.
     */
    String extractText(Document document);
}
</content>
</write_to_file>

Now let me fix the OCRResult class:

<write_to_file>
<path>src/main/java/com/apex/idp/infrastructure/ocr/OCRResult.java</path>
<content>package com.apex.idp.infrastructure.ocr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Result of OCR processing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OCRResult {
    private String extractedText;
    private double confidenceScore;
    private String language;
    private int pageCount;
    private Map<String, Object> metadata;
    private long processingTimeMs;
    private String errorMessage;
    private boolean success;
}
</content>
</write_to_file>

Now let me create the missing OCR helper classes:

<write_to_file>
<path>src/main/java/com/apex/idp/infrastructure/ocr/LayoutOptions.java</path>
<content>package com.apex.idp.infrastructure.ocr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Options for layout-based OCR processing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LayoutOptions {
    private boolean preserveLayout;
    private boolean extractTables;
    private boolean extractForms;
    private boolean detectColumns;
    private String outputFormat; // text, json, xml
}
</content>
</write_to_file>

<write_to_file>
<path>src/main/java/com/apex/idp/infrastructure/ocr/PreprocessingOptions.java</path>
<content>package com.apex.idp.infrastructure.ocr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Options for image preprocessing before OCR.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreprocessingOptions {
    private boolean deskew;
    private boolean removeNoise;
    private boolean enhanceContrast;
    private boolean binarize;
    private int targetDpi;
    private double rotationAngle;
}
</content>
</write_to_file>

```markdown
- [x] Step 1: Add missing Spring WebFlux dependency for WebClient
- [x] Step 2: Fix OpenAI service model classes and interfaces
- [x] Step 3: Fix BatchEventProducer syntax errors
- [x] Step 4: Fix OCR service duplicate definitions
- [ ] Step 5: Fix Storage service interface issues
- [ ] Step 6: Fix WebSocket notification service
- [ ] Step 7: Fix missing methods in domain entities
- [ ] Step 8: Fix JWT token provider method references
- [ ] Step 9: Fix duplicate DTO definitions
- [ ] Step 10: Fix frontend build issue
- [ ] Step 11: Run tests to verify all fixes
```

Now let me check and fix the Storage service:

<read_file>
<path>src/main/java/com/apex/idp/infrastructure/storage/StorageService.java</path>
</read_file>

Let me fix the StorageService interface:

<write_to_file>
<path>src/main/java/com/apex/idp/infrastructure/storage/StorageService.java</path>
<content>package com.apex.idp.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * Interface for file storage operations.
 */
public interface StorageService {
    
    /**
     * Stores a file and returns the storage path.
     */
    String store(MultipartFile file, Long batchId);
    
    /**
     * Stores a file from input stream.
     */
    String store(InputStream inputStream, String fileName, Long batchId);
    
    /**
     * Retrieves a file as input stream.
     */
    InputStream retrieve(String path);
    
    /**
     * Retrieves a file as byte array.
     */
    byte[] retrieveAsBytes(String path);
    
    /**
     * Retrieves a document by batch ID and file name.
     */
    InputStream retrieveDocument(Long batchId, String fileName);
    
    /**
     * Deletes a file.
     */
    void delete(String path);
    
    /**
     * Deletes a file by batch ID and file name.
     */
    void delete(Long batchId, String fileName);
    
    /**
     * Deletes multiple files.
     */
    void deleteMultiple(List<String> paths);
    
    /**
     * Checks if file exists.
     */
    boolean exists(String path);
    
    /**
     * Gets a presigned URL for direct access.
     */
    String getPresignedUrl(String path, int expirationMinutes);
    
    /**
     * Lists files in a directory/prefix.
     */
    List<String> listFiles(String prefix);
    
    /**
     * Gets file metadata.
     */
    FileMetadata getMetadata(String path);
    
    /**
     * Custom exception for storage operations.
     */
    class StorageException extends RuntimeException {
        public StorageException(String message) {
            super(message);
        }
        
        public StorageException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
</content>
</write_to_file>

Now let me create the FileMetadata class:

<write_to_file>
<path>src/main/java/com/apex/idp/infrastructure/storage/FileMetadata.java</path>
<content>package com.apex.idp.infrastructure.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Metadata for stored files.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {
    private String path;
    private String fileName;
    private long size;
    private String contentType;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private String etag;
    private Map<String, String> userMetadata;
}
</content>
</write_to_file>

```markdown
- [x] Step 1: Add missing Spring WebFlux dependency for WebClient
- [x] Step 2: Fix OpenAI service model classes and interfaces
- [x] Step 3: Fix BatchEventProducer syntax errors
- [x] Step 4: Fix OCR service duplicate definitions
- [x] Step 5: Fix Storage service interface issues
- [ ] Step 6: Fix WebSocket notification service
- [ ] Step 7: Fix missing methods in domain entities
- [ ] Step 8: Fix JWT token provider method references
- [ ] Step 9: Fix duplicate DTO definitions
- [ ] Step 10: Fix frontend build issue
- [ ] Step 11: Run tests to verify all fixes
```

Now let me check and fix the WebSocket notification service:

<read_file>
<path>src/main/java/com/apex/idp/infrastructure/websocket/WebSocketNotificationService.java</path>
</read_file>

Let me fix the WebSocketNotificationService:

<write_to_file>
<path>src/main/java/com/apex/idp/infrastructure/websocket/WebSocketNotificationService.java</path>
<content>package com.apex.idp.infrastructure.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for sending real-time notifications via WebSocket.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Sends a notification to a specific user.
     */
    public void sendToUser(String username, String destination, Object payload) {
        try {
            messagingTemplate.convertAndSendToUser(username, destination, payload);
            log.debug("Sent notification to user: {} at destination: {}", username, destination);
        } catch (Exception e) {
            log.error("Error sending notification to user: {}", username, e);
        }
    }

    /**
     * Broadcasts a notification to all connected clients.
     */
    public void broadcast(String destination, Object payload) {
        try {
            messagingTemplate.convertAndSend(destination, payload);
            log.debug("Broadcast notification to destination: {}", destination);
        } catch (Exception e) {
            log.error("Error broadcasting notification", e);
        }
    }

    /**
     * Sends batch status update notification.
     */
    public void notifyBatchStatusUpdate(Long batchId, String status) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "BATCH_STATUS_UPDATE");
        notification.put("batchId", batchId);
        notification.put("status", status);
        notification.put("timestamp", System.currentTimeMillis());
        
        broadcast("/topic/batch-updates", notification);
    }

    /**
     * Sends document processing update notification.
     */
    public void notifyDocumentStatusUpdate(String documentId, String status) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "DOCUMENT_STATUS_UPDATE");
        notification.put("documentId", documentId);
        notification.put("status", status);
        notification.put("timestamp", System.currentTimeMillis());
        
        broadcast("/topic/document-updates", notification);
    }

    /**
     * Sends processing progress notification.
     */
    public void notifyProcessingProgress(Long batchId, int processed, int total) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "PROCESSING_PROGRESS");
        notification.put("batchId", batchId);
        notification.put("processed", processed);
        notification.put("total", total);
        notification.put("percentage", (processed * 100.0) / total);
        notification.put("timestamp", System.currentTimeMillis());
        
        broadcast("/topic/progress-updates", notification);
    }

    /**
     * Sends error notification.
     */
    public void notifyError(String context, String errorMessage, String details) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "ERROR");
        notification.put("context", context);
        notification.put("message", errorMessage);
        notification.put("details", details);
        notification.put("timestamp", System.currentTimeMillis());
        
        broadcast("/topic/error-notifications", notification);
    }

    /**
     * Sends validation required notification.
     */
    public void notifyValidationRequired(String documentId, String username) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "VALIDATION_REQUIRED");
        notification.put("documentId", documentId);
        notification.put("timestamp", System.currentTimeMillis());
        
        sendToUser(username, "/queue/validations", notification);
    }
}
</content>
</write_to_file>

```markdown
- [x] Step 1: Add missing Spring WebFlux dependency for WebClient
- [x] Step 2: Fix OpenAI service model classes and interfaces
- [x] Step 3: Fix BatchEventProducer syntax errors
- [x] Step 4: Fix OCR service duplicate definitions
- [x] Step 5: Fix Storage service interface issues
- [x] Step 6: Fix WebSocket notification service
- [ ] Step 7: Fix missing methods in domain entities
- [ ] Step 8: Fix JWT token provider method references
- [ ] Step 9: Fix duplicate DTO definitions
- [ ] Step 10: Fix frontend build issue
- [ ] Step 11: Run tests to verify all fixes
```

Now let me fix the missing methods in domain entities. First, let me check the Batch entity:

<read_file>
<path>src/main/java/com/apex/idp/domain/model/Batch.java</path>
</read_file>

I need to add missing methods to the Batch entity:

<write_to_file>
<path>src/main/java/com/apex/idp/domain/model/Batch.java</path>
<content>package com.apex.idp.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a batch of documents for processing.
 * This is an aggregate root in the Document Processing bounded context.
 */
@Entity
@Table(name = "batches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Batch {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BatchStatus status;
    
    @Column(name = "created_by", nullable = false)
    private String createdBy;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Document> documents = new ArrayList<>();
    
    @Column(name = "total_documents")
    private Integer totalDocuments;
    
    @Column(name = "processed_documents")
    private Integer processedDocuments;
    
    @Column(name = "failed_documents")
    private Integer failedDocuments;
    
    /**
     * Factory method to create a new batch.
     */
    public static Batch create(String name, String description, String createdBy) {
        Batch batch = new Batch();
        batch.name = name;
        batch.description = description;
        batch.createdBy = createdBy;
        batch.status = BatchStatus.CREATED;
        batch.createdAt = LocalDateTime.now();
        batch.updatedAt = LocalDateTime.now();
        batch.totalDocuments = 0;
        batch.processedDocuments = 0;
        batch.failedDocuments = 0;
        return batch;
    }
    
    /**
     * Factory method to create a new batch with just a name.
     */
    public static Batch create(String name) {
        return create(name, null, "system");
    }
    
    /**
     * Adds a document to the batch.
     */
    public void addDocument(Document document) {
        documents.add(document);
        document.setBatch(this);
        this.totalDocuments = documents.size();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Starts processing the batch.
     */
    public void startProcessing() {
        if (this.status != BatchStatus.CREATED) {
            throw new IllegalStateException("Can only start processing for batches in CREATED status");
        }
        this.status = BatchStatus.PROCESSING;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Marks the batch as completed.
     */
    public void complete() {
        this.status = BatchStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Marks the batch as failed.
     */
    public void fail() {
        this.status = BatchStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Marks the batch as failed with a reason.
     */
    public void failAnalysis(String reason) {
        this.status = BatchStatus.FAILED;
        this.description = (this.description != null ? this.description + " | " : "") + "Analysis failed: " + reason;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Updates processing progress.
     */
    public void updateProgress(int processed, int failed) {
        this.processedDocuments = processed;
        this.failedDocuments = failed;
        this.updatedAt = LocalDateTime.now();
        
        if (processed + failed >= totalDocuments) {
            if (failed == 0) {
                complete();
            } else if (processed == 0) {
                fail();
            } else {
                this.status = BatchStatus.PARTIALLY_COMPLETED;
                this.completedAt = LocalDateTime.now();
            }
        }
    }
    
    /**
     * Gets the count of documents in the batch.
     */
    public int getDocumentCount() {
        return documents != null ? documents.size() : 0;
    }
    
    /**
     * Gets the count of processed documents.
     */
    public int getProcessedDocumentCount() {
        return processedDocuments != null ? processedDocuments : 0;
    }
    
    /**
     * Gets the count of failed documents.
     */
    public int getFailedDocumentCount() {
        return failedDocuments != null ? failedDocuments : 0;
    }
    
    /**
     * Checks if batch processing is complete.
     */
    public boolean isComplete() {
        return status == BatchStatus.COMPLETED || 
               status == BatchStatus.FAILED || 
               status == BatchStatus.PARTIALLY_COMPLETED;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (totalDocuments == null) {
            totalDocuments = 0;
        }
        if (processedDocuments == null) {
            processedDocuments = 0;
        }
        if (failedDocuments == null) {
            failedDocuments = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
</content>
</write_to_file>

Now let me add the BatchStatus enum:

<write_to_file>
<path>src/main/java/com/apex/idp/domain/model/BatchStatus.java</path>
<content>package com.apex.idp.domain.model;

/**
 * Represents the status of a batch in the processing pipeline.
 */
public enum BatchStatus {
    CREATED,
    PROCESSING,
    COMPLETED,
    FAILED,
    PARTIALLY_COMPLETED,
    CANCELLED
}
</content>
</write_to_file>

Now let me check and fix the Invoice entity:

<read_file>
<path>src/main/java/com/apex/idp/domain/model/Invoice.java</path>
</read_file>

Let me fix the Invoice entity to add the missing approve method:

<write_to_file>
<path>src/main/java/com/apex/idp/domain/model/Invoice.java</path>
<content>package com.apex.idp.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an invoice in the system.
 * This is an aggregate root in the Financial Operations bounded context.
 */
@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "invoice_number", nullable = false, unique = true)
    private String invoiceNumber;
    
    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;
    
    @Column(name = "due_date")
    private LocalDate dueDate;
    
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "tax_amount", precision = 19, scale = 2)
    private BigDecimal taxAmount;
    
    @Column(name = "subtotal", precision = 19, scale = 2)
    private BigDecimal subtotal;
    
    @Column(length = 3)
    private String currency;
    
    @Column(length = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;
    
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LineItem> lineItems = new ArrayList<>();
    
    @Column(name = "approved_by")
    private String approvedBy;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "created_by", nullable = false)
    private String createdBy;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "confidence_score")
    private Double confidenceScore;
    
    @Column(name = "payment_terms")
    private String paymentTerms;
    
    @Column(name = "po_number")
    private String poNumber;
    
    /**
     * Factory method to create a new invoice.
     */
    public static Invoice create(String invoiceNumber, LocalDate invoiceDate, 
                                BigDecimal totalAmount, String createdBy) {
        Invoice invoice = new Invoice();
        invoice.invoiceNumber = invoiceNumber;
        invoice.invoiceDate = invoiceDate;
        invoice.totalAmount = totalAmount;
        invoice.createdBy = createdBy;
        invoice.status = InvoiceStatus.DRAFT;
        invoice.createdAt = LocalDateTime.now();
        invoice.updatedAt = LocalDateTime.now();
        invoice.currency = "USD"; // Default currency
        return invoice;
    }
    
    /**
     * Adds a line item to the invoice.
     */
    public void addLineItem(LineItem lineItem) {
        lineItems.add(lineItem);
        lineItem.setInvoice(this);
        recalculateTotals();
    }
    
    /**
     * Removes a line item from the invoice.
     */
    public void removeLineItem(LineItem lineItem) {
        lineItems.remove(lineItem);
        lineItem.setInvoice(null);
        recalculateTotals();
    }
    
    /**
     * Recalculates invoice totals based on line items.
     */
    private void recalculateTotals() {
        if (lineItems.isEmpty()) {
            subtotal = BigDecimal.ZERO;
            return;
        }
        
        subtotal = lineItems.stream()
            .map(LineItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // If tax amount is set, calculate total
        if (taxAmount != null) {
            totalAmount = subtotal.add(taxAmount);
        } else {
            totalAmount = subtotal;
        }
        
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Submits the invoice for processing.
     */
    public void submit() {
        if (status != InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Can only submit invoices in DRAFT status");
        }
        status = InvoiceStatus.SUBMITTED;
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Approves the invoice.
     */
    public void approve(String approver) {
        if (status != InvoiceStatus.SUBMITTED && status != InvoiceStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Can only approve invoices in SUBMITTED or PENDING_APPROVAL status");
        }
        status = InvoiceStatus.APPROVED;
        approvedBy = approver;
        approvedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Approves the invoice (overload for backward compatibility).
     */
    public void approve() {
        approve("system");
    }
    
    /**
     * Rejects the invoice.
     */
    public void reject(String reason) {
        if (status == InvoiceStatus.PAID || status == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException("Cannot reject invoices in PAID or CANCELLED status");
        }
        status = InvoiceStatus.REJECTED;
        if (description != null) {
            description = description + " | Rejected: " + reason;
        } else {
            description = "Rejected: " + reason;
        }
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Marks the invoice as paid.
     */
    public void markAsPaid() {
        if (status != InvoiceStatus.APPROVED) {
            throw new IllegalStateException("Can only mark approved invoices as paid");
        }
        status = InvoiceStatus.PAID;
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Cancels the invoice.
     */
    public void cancel() {
        if (status == InvoiceStatus.PAID) {
            throw new IllegalStateException("Cannot cancel paid invoices");
        }
        status = InvoiceStatus.CANCELLED;
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Sets the invoice for manual review.
     */
    public void requireApproval() {
        if (status != InvoiceStatus.SUBMITTED) {
            throw new IllegalStateException("Can only set SUBMITTED invoices for approval");
        }
        status = InvoiceStatus.PENDING_APPROVAL;
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Checks if the invoice is editable.
     */
    public boolean isEditable() {
        return status == InvoiceStatus.DRAFT || 
               status == InvoiceStatus.REJECTED;
    }
    
    /**
     * Checks if the invoice is payable.
     */
    public boolean isPayable() {
        return status == InvoiceStatus.APPROVED;
    }
    
    /**
     * Gets the description of the invoice.
     */
    public String getDescription() {
        return description;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = InvoiceStatus.DRAFT;
        }
        if (currency == null) {
            currency = "USD";
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
</content>
</write_to_file>

Now let me create the InvoiceStatus enum:

<write_to_file>
<path>src/main/java/com/apex/idp/domain/model/InvoiceStatus.java</path>
<content>package com.apex.idp.domain.model;

/**
 * Represents the status of an invoice in the processing pipeline.
 */
public enum InvoiceStatus {
    DRAFT,
    SUBMITTED,
    PENDING_APPROVAL,
    APPROVED,
    REJECTED,
    PAID,
    CANCELLED
}
</content>
</write_to_file>

```markdown
- [x] Step 1: Add missing Spring WebFlux dependency for WebClient
- [x] Step 2: Fix OpenAI service model classes and interfaces
- [x] Step 3: Fix BatchEventProducer syntax errors
- [x] Step 4: Fix OCR service duplicate definitions
- [x] Step 5: Fix Storage service interface issues
- [x] Step 6: Fix WebSocket notification service
- [x] Step 7: Fix missing methods in domain entities
- [ ] Step 8: Fix JWT token provider method references
- [ ] Step 9: Fix duplicate DTO definitions
- [ ] Step 10: Fix frontend build issue
- [ ] Step 11: Run tests to verify all fixes
```

Now let me fix the JWT token provider missing method:

<write_to_file>
<path>src/main/java/com/apex/idp/security/JwtTokenProvider.java</path>
<content>package com.apex.idp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT token provider for generating and validating JWT tokens.
 * Handles token creation, validation, and claim extraction.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final String secret;
    private final long expiration;
    private final CustomUserDetailsService customUserDetailsService;
    private final SecretKey key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.expiration}") long expiration,
                            CustomUserDetailsService customUserDetailsService) {
        this.secret = secret;
        this.expiration = expiration;
        this.customUserDetailsService =

Similar code found with 6 license types