package com.apex.core.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO configuration for document storage
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "apex.storage", name = "type", havingValue = "MINIO")
public class MinioConfig {

    private final ApexProperties apexProperties;

    @Bean
    public MinioClient minioClient() {
        ApexProperties.Storage storage = apexProperties.getStorage();
        
        return MinioClient.builder()
                .endpoint(storage.getEndpoint())
                .credentials(storage.getAccessKey(), storage.getSecretKey())
                .build();
    }
}
