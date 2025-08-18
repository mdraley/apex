package com.apex.core.test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Composite annotation for integration tests with all required infrastructure
 * Uses Testcontainers for PostgreSQL, Kafka, Redis, and MinIO
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public @interface IntegrationTest {

    /**
     * Base class for integration tests with common infrastructure setup
     */
    abstract class Base {

        @Container
        @SuppressWarnings("resource")
        static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("apex_test")
                .withUsername("test")
                .withPassword("test");

        @Container
        @SuppressWarnings("resource")
        static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

        @Container
        @SuppressWarnings("resource")
        static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
                .withExposedPorts(6379);

        @Container
        @SuppressWarnings("resource")
        static GenericContainer<?> minio = new GenericContainer<>("minio/minio:latest")
                .withExposedPorts(9000, 9001)
                .withEnv("MINIO_ACCESS_KEY", "minioadmin")
                .withEnv("MINIO_SECRET_KEY", "minioadmin")
                .withCommand("server", "/data", "--console-address", ":9001");

        @DynamicPropertySource
        static void configureProperties(DynamicPropertyRegistry registry) {
            // PostgreSQL properties
            registry.add("spring.datasource.url", postgres::getJdbcUrl);
            registry.add("spring.datasource.username", postgres::getUsername);
            registry.add("spring.datasource.password", postgres::getPassword);
            registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
            
            // Kafka properties
            registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
            
            // Redis properties
            registry.add("spring.data.redis.host", redis::getHost);
            registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
            
            // MinIO properties
            registry.add("apex.storage.endpoint", () -> "http://" + minio.getHost() + ":" + minio.getMappedPort(9000));
            registry.add("apex.storage.access-key", () -> "minioadmin");
            registry.add("apex.storage.secret-key", () -> "minioadmin");
            
            // Test specific properties
            registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
            registry.add("spring.liquibase.enabled", () -> "false");
            registry.add("logging.level.com.apex", () -> "DEBUG");
        }
    }
}
