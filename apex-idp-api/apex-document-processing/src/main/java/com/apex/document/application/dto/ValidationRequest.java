package com.apex.document.application.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for document validation
 */
@Data
public class ValidationRequest {
    private UUID userId;
    private boolean approved;
    private String rejectionReason;
    private List<FieldCorrection> fieldCorrections;
    
    @Data
    public static class FieldCorrection {
        private String fieldName;
        private String correctedValue;
    }
}
