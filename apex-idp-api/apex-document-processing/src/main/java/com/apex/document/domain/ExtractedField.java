package com.apex.document.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents a field extracted from a document with its confidence score
 */
@Entity
@Table(name = "extracted_fields")
@Getter
@Setter
@NoArgsConstructor
public class ExtractedField {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(name = "document_id", nullable = false)
    private UUID documentId;
    
    @Column(name = "field_name", nullable = false)
    private String fieldName;
    
    @Column(name = "field_value")
    private String value;
    
    @Column(name = "confidence", precision = 5, scale = 4)
    private BigDecimal confidence;
    
    @Column(name = "x_coordinate")
    private Integer xCoordinate;
    
    @Column(name = "y_coordinate")
    private Integer yCoordinate;
    
    @Column(name = "width")
    private Integer width;
    
    @Column(name = "height")
    private Integer height;
    
    @Column(name = "page_number")
    private Integer pageNumber;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    public ExtractedField(String fieldName, String value, BigDecimal confidence) {
        this.fieldName = fieldName;
        this.value = value;
        this.confidence = confidence;
        this.createdAt = Instant.now();
    }
    
    public ExtractedField(String fieldName, String value, BigDecimal confidence, 
                         Integer xCoordinate, Integer yCoordinate, 
                         Integer width, Integer height, Integer pageNumber) {
        this(fieldName, value, confidence);
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.width = width;
        this.height = height;
        this.pageNumber = pageNumber;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
