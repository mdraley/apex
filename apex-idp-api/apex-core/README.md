# apex-core

Purpose: Shared kernel and cross-cutting concerns used by all bounded contexts.

Contents (expected):
- Security configuration (Spring Security, JWT/JJWT)
- Global exception handling and validation
- OpenAPI configuration
- Flyway migrations and base DB configuration
- Common utilities (logging, constants, helpers)

Key integration points:
- PostgreSQL via Spring Data JPA
- Actuator health endpoints
- Optional Kafka client configuration

Testing:
- Unit tests for utilities
- Spring Boot configuration tests
