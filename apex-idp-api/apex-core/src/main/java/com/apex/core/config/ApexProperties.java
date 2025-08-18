package com.apex.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

/**
 * Application properties for Apex IDP
 * Centralizes all configuration properties
 */
@Configuration
@ConfigurationProperties(prefix = "apex")
@Data
public class ApexProperties {

    private Jwt jwt = new Jwt();
    private Storage storage = new Storage();
    private Processing processing = new Processing();
    private Integration integration = new Integration();
    private Notification notification = new Notification();

    @Data
    public static class Jwt {
        private String secret = "defaultSecretKeyThatShouldBeChangedInProduction123456789";
        private long expiration = 86400000; // 24 hours
        private long refreshExpiration = 604800000; // 7 days
    }

    @Data
    public static class Storage {
        private String type = "MINIO"; // MINIO, S3, LOCAL
        private String endpoint = "http://localhost:9000";
        private String accessKey = "minioadmin";
        private String secretKey = "minioadmin";
        private String bucketName = "apex-documents";
        private String region = "us-east-1";
    }

    @Data
    public static class Processing {
        private int maxFileSize = 50; // MB
        private String[] allowedTypes = {"application/pdf", "image/jpeg", "image/png", "image/tiff"};
        private int batchSize = 10;
        private int retryAttempts = 3;
        private String ocrEngine = "TESSERACT"; // TESSERACT, GOOGLE_VISION, AWS_TEXTRACT
    }

    @Data
    public static class Integration {
        private Erp erp = new Erp();
        private Email email = new Email();
        
        @Data
        public static class Erp {
            private String type = "SAP"; // SAP, ORACLE, QUICKBOOKS
            private String endpoint = "";
            private String username = "";
            private String password = "";
            private boolean enabled = false;
        }
        
        @Data
        public static class Email {
            private String host = "smtp.gmail.com";
            private int port = 587;
            private String username = "";
            private String password = "";
            private boolean enableTls = true;
        }
    }

    @Data
    public static class Notification {
        private boolean emailEnabled = true;
        private boolean smsEnabled = false;
        private boolean pushEnabled = true;
        private int retentionDays = 30;
    }
}
