package com.apex.document.application.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for validation queue items
 */
@Data
@Builder
public class ValidationQueueDTO {
    private UUID id;
    private String filename;
    private String documentType;
    private String status;
    private BigDecimal confidence;
    private LocalDateTime submittedAt;
    private String assignedTo;
    private Integer priority;
    private String validationStage;
}
