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
    
    static GenericContainer<?> minio = new GenericContainer<>("minio/minio:latest")
        .withExposedPorts(9000)
        .withEnv("MINIO_ROOT_USER", "testuser")
        .withEnv("MINIO_ROOT_PASSWORD", "testpassword")
        .withCommand("server", "/data");
    
    static {
        postgres.start();
        kafka.start();
        redis.start();
        minio.start();
    }
    
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        // Database properties
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        
        // Kafka properties
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        
        // Redis properties
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
        
        // MinIO properties
        registry.add("minio.endpoint", () -> "http://" + minio.getHost() + ":" + minio.getFirstMappedPort());
        registry.add("minio.access-key", () -> "testuser");
        registry.add("minio.secret-key", () -> "testpassword");
        
        // Test specific properties
        registry.add("apex.jwt.secret", () -> "test-secret-key-for-testing-purposes-only");
        registry.add("apex.jwt.access-token-expiration", () -> "60"); // 1 hour for tests
    }
    
    @BeforeEach
    void setUp() {
        // Common setup for all integration tests
        // Initialize test data, clear caches, etc.
    }
}
