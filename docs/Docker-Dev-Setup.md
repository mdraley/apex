# Docker Development Environment for Apex IDP Platform

## Understanding Why Docker Transforms Local Development

Before we dive into the setup, let me explain why Docker is particularly valuable for the Apex IDP platform. When you're developing a sophisticated healthcare document processing system, you're not just running a simple application - you're orchestrating an entire ecosystem of services. Think of Docker as your development environment architect, ensuring every developer on your team has an identical, fully-functional setup regardless of their machine or operating system.

Imagine trying to coordinate PostgreSQL, Redis, Kafka, MinIO, and Elasticsearch installations across your team. Each developer might have different versions, different configurations, or worse - conflicting ports with other projects they're working on. Docker eliminates this chaos by creating isolated, reproducible environments that mirror your production setup exactly. This means when you fix a bug on your machine, you can be confident it's actually fixed, not just working due to some quirk of your local setup.

## Prerequisites and Initial Setup

Before we begin building our Docker environment, you'll need to ensure Docker is properly installed on your machine. If you're on Windows or Mac, Docker Desktop provides everything you need in one package. Linux users will install Docker Engine directly. The key thing to understand is that Docker Desktop includes both Docker Engine (which runs containers) and Docker Compose (which orchestrates multiple containers), while Linux users need to install Docker Compose separately.

To verify your installation is working correctly, open your terminal and run these commands:

```bash
# Check Docker version - should be 24.0 or higher for optimal performance
docker --version

# Check Docker Compose version - should be 2.20 or higher
docker compose version

# Verify Docker daemon is running by pulling a test image
docker run hello-world
```

If the hello-world container runs successfully, you're ready to proceed. If not, the error messages will guide you - typically, it's either Docker Desktop not running or permission issues on Linux (which you can solve by adding your user to the docker group).

## Project Structure for Docker Development

Let's organize our project structure to support both local development and future production deployments. This structure separates concerns clearly, making it easy to understand what each component does:

```
apex-idp/
├── docker/                          # All Docker-related files
│   ├── development/                 # Development-specific configurations
│   │   ├── docker-compose.yml      # Main orchestration file
│   │   ├── docker-compose.override.yml  # Local overrides
│   │   └── .env.example            # Environment variables template
│   ├── services/                   # Individual service Dockerfiles
│   │   ├── api/
│   │   │   └── Dockerfile.dev      # Spring Boot development image
│   │   ├── postgres/
│   │   │   └── init-scripts/       # Database initialization scripts
│   │   └── nginx/
│   │       └── nginx.conf          # Reverse proxy configuration
│   └── volumes/                    # Persistent data (git-ignored)
│       ├── postgres-data/
│       ├── redis-data/
│       ├── kafka-data/
│       └── minio-data/
├── apex-api-gateway/               # Your Spring Boot modules
├── apex-core/
├── apex-document-processing/
└── scripts/                        # Utility scripts
    ├── docker-start.sh
    ├── docker-stop.sh
    └── docker-clean.sh
```

## Building the Spring Boot Development Container

Now let's create a Dockerfile specifically optimized for development. This differs from a production Dockerfile because we prioritize developer experience over image size and security hardening. We want fast rebuilds, hot reloading, and debugging capabilities.

```dockerfile
# docker/services/api/Dockerfile.dev
# This Dockerfile is optimized for development, not production
# It includes development tools and enables hot reloading

# Start with Eclipse Temurin (formerly AdoptOpenJDK) for reliability
FROM eclipse-temurin:21-jdk-jammy as development

# Install useful development tools that aren't in the base image
# These help with debugging and troubleshooting
RUN apt-get update && apt-get install -y \
    curl \
    vim \
    netcat \
    postgresql-client \
    && rm -rf /var/lib/apt/lists/*

# Create a non-root user for security (even in development)
# This mirrors production best practices
RUN groupadd -r spring && useradd -r -g spring spring

# Set up the application directory with proper permissions
WORKDIR /app
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring

# Copy Maven wrapper files first for better layer caching
# This means these layers won't rebuild unless the wrapper changes
COPY --chown=spring:spring mvnw .
COPY --chown=spring:spring .mvn .mvn

# Copy POM files next - dependencies change less often than code
# This creates a layer with all our dependencies cached
COPY --chown=spring:spring pom.xml .
COPY --chown=spring:spring */pom.xml ./

# Download dependencies in a separate layer
# This step is slow, so we want it cached when possible
RUN ./mvnw dependency:go-offline -B

# Copy source code last - this changes most frequently
COPY --chown=spring:spring . .

# Expose both the application port and debug port
EXPOSE 8080 5005

# Enable Spring DevTools and remote debugging
# The suspend=n means don't wait for debugger to attach before starting
ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

# Use Maven spring-boot:run for hot reloading
# This watches for changes and automatically restarts
CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.profiles=docker,development"]
```

## Orchestrating Services with Docker Compose

Docker Compose is where the magic happens for local development. Think of it as a conductor orchestrating all your services to work in harmony. Here's our main composition file that brings everything together:

```yaml
# docker/development/docker-compose.yml
# This file defines our entire development environment
version: '3.9'

# Define named networks for service isolation
networks:
  apex-network:
    driver: bridge
    name: apex-development-network

# Define named volumes for data persistence
volumes:
  postgres-data:
    name: apex-postgres-data
  redis-data:
    name: apex-redis-data
  kafka-data:
    name: apex-kafka-data
  zookeeper-data:
    name: apex-zookeeper-data
  minio-data:
    name: apex-minio-data
  elasticsearch-data:
    name: apex-elasticsearch-data

services:
  # PostgreSQL - Our primary database
  # We use version 15 for its improved performance and features
  postgres:
    image: postgres:15-alpine
    container_name: apex-postgres
    environment:
      # These credentials are only for local development
      POSTGRES_USER: apex_user
      POSTGRES_PASSWORD: apex_dev_password
      POSTGRES_DB: apex_idp
      # Enable query logging for development debugging
      POSTGRES_INITDB_ARGS: "-c log_statement=all"
    volumes:
      # Persist database data between container restarts
      - postgres-data:/var/lib/postgresql/data
      # Mount initialization scripts
      - ./services/postgres/init-scripts:/docker-entrypoint-initdb.d
    ports:
      # Expose PostgreSQL on standard port for external tools like DBeaver
      - "5432:5432"
    networks:
      - apex-network
    healthcheck:
      # Ensure database is actually ready, not just container started
      test: ["CMD-SHELL", "pg_isready -U apex_user -d apex_idp"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Redis - For caching and session management
  redis:
    image: redis:7-alpine
    container_name: apex-redis
    command: redis-server --appendonly yes --maxmemory 256mb --maxmemory-policy allkeys-lru
    volumes:
      - redis-data:/data
    ports:
      - "6379:6379"
    networks:
      - apex-network
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Zookeeper - Required for Kafka coordination
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    container_name: apex-zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
      # Disable JMX for local development to reduce memory usage
      ZOOKEEPER_JMX_ENABLED: "false"
    volumes:
      - zookeeper-data:/var/lib/zookeeper/data
    networks:
      - apex-network

  # Kafka - For event streaming and async processing
  kafka:
    image: confluentinc/cp-kafka:7.5.0
    container_name: apex-kafka
    depends_on:
      zookeeper:
        condition: service_started
    environment:
      # Configure Kafka for single-node development
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
      # Listeners configuration for internal and external connections
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
      # Create topics automatically when messages are published
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: "true"
      # Development-friendly settings for smaller resource usage
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
      # Disable JMX to reduce memory footprint
      KAFKA_JMX_ENABLED: "false"
    volumes:
      - kafka-data:/var/lib/kafka/data
    ports:
      - "9092:9092"
    networks:
      - apex-network
    healthcheck:
      test: ["CMD", "kafka-broker-api-versions", "--bootstrap-server", "localhost:9092"]
      interval: 10s
      timeout: 10s
      retries: 5

  # MinIO - S3-compatible object storage for documents
  minio:
    image: minio/minio:latest
    container_name: apex-minio
    command: server /data --console-address ":9001"
    environment:
      # Default credentials for local development
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: minioadmin
      # Create default bucket on startup
      MINIO_DEFAULT_BUCKETS: apex-documents
    volumes:
      - minio-data:/data
    ports:
      # API port for S3 operations
      - "9000:9000"
      # Console port for web UI
      - "9001:9001"
    networks:
      - apex-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9000/minio/health/live"]
      interval: 30s
      timeout: 20s
      retries: 3

  # Elasticsearch - For full-text document search
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
    container_name: apex-elasticsearch
    environment:
      # Single-node configuration for development
      - discovery.type=single-node
      # Disable security for local development simplicity
      - xpack.security.enabled=false
      # Memory settings appropriate for development
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
      # Disable memory swapping
      - bootstrap.memory_lock=true
    ulimits:
      memlock:
        soft: -1
        hard: -1
    volumes:
      - elasticsearch-data:/usr/share/elasticsearch/data
    ports:
      - "9200:9200"
    networks:
      - apex-network
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:9200/_cluster/health || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 5

  # Spring Boot API - Our main application
  apex-api:
    build:
      context: ../..
      dockerfile: docker/services/api/Dockerfile.dev
    container_name: apex-api
    depends_on:
      # Wait for all infrastructure services to be healthy
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
      kafka:
        condition: service_healthy
      minio:
        condition: service_healthy
      elasticsearch:
        condition: service_healthy
    environment:
      # Spring profiles for Docker environment
      SPRING_PROFILES_ACTIVE: docker,development
      # Database configuration
      DB_HOST: postgres
      DB_USERNAME: apex_user
      DB_PASSWORD: apex_dev_password
      # Redis configuration
      REDIS_HOST: redis
      # Kafka configuration
      KAFKA_SERVERS: kafka:29092
      # MinIO configuration
      MINIO_ENDPOINT: http://minio:9000
      MINIO_ACCESS_KEY: minioadmin
      MINIO_SECRET_KEY: minioadmin
      # Elasticsearch configuration
      ELASTICSEARCH_HOST: elasticsearch
      # JWT secret for local development
      JWT_SECRET: local-development-secret-key-change-in-production
      # Enable detailed logging for development
      LOGGING_LEVEL_COM_APEX: DEBUG
    volumes:
      # Mount source code for hot reloading
      - ../..:/app
      # Mount Maven cache to speed up builds
      - ~/.m2:/home/spring/.m2
    ports:
      # Application port
      - "8080:8080"
      # Debug port for IDE remote debugging
      - "5005:5005"
    networks:
      - apex-network
    # Restart on failure but not too aggressively
    restart: on-failure:3

  # Kafka UI - Visual tool for Kafka management
  kafka-ui:
    image: provectuslabs/kafka-ui:latest
    container_name: apex-kafka-ui
    depends_on:
      - kafka
    environment:
      KAFKA_CLUSTERS_0_NAME: local
      KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:29092
      KAFKA_CLUSTERS_0_ZOOKEEPER: zookeeper:2181
    ports:
      - "8090:8080"
    networks:
      - apex-network

  # Adminer - Lightweight database management tool
  adminer:
    image: adminer:latest
    container_name: apex-adminer
    ports:
      - "8091:8080"
    networks:
      - apex-network
    environment:
      ADMINER_DEFAULT_SERVER: postgres
```

## Environment Configuration

Let's create an environment file that makes it easy to customize your local setup without modifying the docker-compose file directly. This separation of configuration from orchestration is a Docker best practice:

```bash
# docker/development/.env.example
# Copy this to .env and customize for your local development

# Application Configuration
COMPOSE_PROJECT_NAME=apex-idp
APEX_ENV=development

# Version tags for reproducible builds
POSTGRES_VERSION=15-alpine
REDIS_VERSION=7-alpine
KAFKA_VERSION=7.5.0
ELASTICSEARCH_VERSION=8.11.0
MINIO_VERSION=latest

# Database Configuration
DB_HOST=postgres
DB_PORT=5432
DB_NAME=apex_idp
DB_USERNAME=apex_user
DB_PASSWORD=apex_dev_password

# Redis Configuration
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_PASSWORD=

# Kafka Configuration
KAFKA_SERVERS=kafka:29092
KAFKA_GROUP_ID=apex-dev-group

# MinIO Configuration
MINIO_ENDPOINT=http://minio:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
MINIO_BUCKET=apex-documents

# Elasticsearch Configuration
ELASTICSEARCH_HOST=elasticsearch
ELASTICSEARCH_PORT=9200

# Application Ports
API_PORT=8080
API_DEBUG_PORT=5005
KAFKA_UI_PORT=8090
ADMINER_PORT=8091
MINIO_CONSOLE_PORT=9001

# Memory Limits (adjust based on your machine)
POSTGRES_MEMORY=512m
REDIS_MEMORY=256m
KAFKA_MEMORY=1g
ELASTICSEARCH_MEMORY=1g
API_MEMORY=2g

# Development Settings
ENABLE_DEBUG=true
ENABLE_HOT_RELOAD=true
LOG_LEVEL=DEBUG
```

## Utility Scripts for Development Workflow

To make your daily development workflow smooth, let's create helper scripts that handle common tasks. These scripts abstract away the complexity of Docker commands and provide a consistent interface for your team:

```bash
#!/bin/bash
# scripts/docker-start.sh
# Starts the development environment with helpful output

set -e  # Exit on error

echo "🚀 Starting Apex IDP Development Environment..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker Desktop and try again."
    exit 1
fi

# Navigate to docker directory
cd docker/development

# Check if .env exists, create from example if not
if [ ! -f .env ]; then
    echo "📝 Creating .env file from template..."
    cp .env.example .env
    echo "⚠️  Please review .env file and adjust settings if needed"
fi

# Pull latest images
echo "📦 Pulling latest Docker images..."
docker compose pull

# Start infrastructure services first
echo "🏗️  Starting infrastructure services..."
docker compose up -d postgres redis zookeeper kafka minio elasticsearch

# Wait for services to be healthy
echo "⏳ Waiting for infrastructure services to be healthy..."
sleep 10

# Check service health
for service in postgres redis kafka minio elasticsearch; do
    if docker compose ps $service | grep -q "healthy"; then
        echo "✅ $service is healthy"
    else
        echo "⚠️  $service may not be fully ready"
    fi
done

# Start application
echo "🎯 Starting Spring Boot application..."
docker compose up -d apex-api

# Start auxiliary services
echo "🛠️  Starting auxiliary services..."
docker compose up -d kafka-ui adminer

echo "✨ Development environment is starting!"
echo ""
echo "📍 Service URLs:"
echo "   • API:           http://localhost:8080"
echo "   • API Health:    http://localhost:8080/actuator/health"
echo "   • Kafka UI:      http://localhost:8090"
echo "   • MinIO Console: http://localhost:9001 (minioadmin/minioadmin)"
echo "   • Adminer:       http://localhost:8091 (apex_user/apex_dev_password)"
echo "   • Elasticsearch: http://localhost:9200"
echo ""
echo "📝 Logs: docker compose logs -f apex-api"
echo "🛑 Stop: ./docker-stop.sh"
```

```bash
#!/bin/bash
# scripts/docker-stop.sh
# Gracefully stops the development environment

set -e

echo "🛑 Stopping Apex IDP Development Environment..."

cd docker/development

# Stop all services
docker compose down

echo "✅ All services stopped"
echo "💡 Data is preserved in Docker volumes"
echo "   To completely reset, run: ./docker-clean.sh"
```

```bash
#!/bin/bash
# scripts/docker-clean.sh
# Completely resets the development environment

set -e

echo "🧹 Cleaning Apex IDP Development Environment..."
echo "⚠️  WARNING: This will delete all data in Docker volumes!"
read -p "Are you sure? (y/N) " -n 1 -r
echo

if [[ $REPLY =~ ^[Yy]$ ]]; then
    cd docker/development
    
    # Stop and remove everything including volumes
    docker compose down -v
    
    # Remove any dangling images
    docker image prune -f
    
    echo "✅ Environment cleaned"
    echo "   Run ./docker-start.sh to start fresh"
else
    echo "❌ Cleanup cancelled"
fi
```

Make these scripts executable:
```bash
chmod +x scripts/*.sh
```

## Development Workflow Patterns

Now that your environment is set up, let me explain the typical development workflow patterns that will make you most productive.

When you start your day, run `./scripts/docker-start.sh` and grab a coffee while the services spin up. The Spring Boot application uses Spring DevTools, which means your code changes will automatically trigger a restart of the application context. For most changes, you don't need to rebuild the Docker image - just save your Java files and watch the logs to see the application restart.

For database schema changes, you should use Flyway migrations. Create a new migration file in `src/main/resources/db/migration/` with a versioned name like `V002__add_vendor_risk_table.sql`. When the application restarts, Flyway will automatically apply your migration. This ensures your database changes are version-controlled and reproducible across the team.

When you need to debug, your IDE can connect to the remote debugger on port 5005. In IntelliJ IDEA, create a Remote JVM Debug configuration pointing to `localhost:5005`. Set your breakpoints and attach the debugger - you can now step through code running inside the Docker container as if it were running locally.

## Troubleshooting Common Issues

Let me address the most common issues developers encounter with Docker development environments, as understanding these will save you hours of frustration.

**Port Conflicts**: If you see "bind: address already in use" errors, another service is using that port. You can either stop the conflicting service or modify the port mapping in docker-compose.yml. For example, change `"8080:8080"` to `"8081:8080"` to expose the API on port 8081 instead.

**Memory Issues**: Docker Desktop has memory limits that might be too low for running all services. If containers are being killed or running slowly, increase Docker Desktop's memory allocation in Settings > Resources. For the Apex platform, allocate at least 8GB of RAM to Docker.

**Slow Performance on Mac/Windows**: Docker Desktop uses a virtual machine on these platforms, which can impact file system performance. The mounted volumes for hot reloading might be slow. Consider using Docker's cached mount option by modifying the volume mount to: `- ../..:/app:cached`.

**Database Connection Issues**: If the Spring Boot application can't connect to PostgreSQL, it's usually a timing issue. Even though we have health checks, sometimes the application starts before the database is truly ready. The restart policy will handle this, but you can also add a startup delay to the application.

**Container Logs**: When something isn't working, the logs are your best friend. Use `docker compose logs -f [service-name]` to tail logs for a specific service. For example, `docker compose logs -f apex-api` shows only the Spring Boot logs.

## Connecting External Tools

Your containerized services are accessible from your host machine, which means you can connect your favorite development tools.

For database management, you can connect DBeaver, pgAdmin, or any PostgreSQL client to `localhost:5432` using the credentials from the .env file. This lets you inspect data, run queries, and manage the schema visually while the application runs.

The MinIO console at `http://localhost:9001` provides a web interface for managing stored documents. You can browse buckets, upload test files, and verify that document processing is working correctly.

Kafka UI at `http://localhost:8090` lets you inspect topics, view messages, and monitor consumer groups. This is invaluable when debugging event-driven features or tracking down why a document isn't processing.

## Performance Optimization Tips

Let me share some performance optimizations that will make your development experience smoother.

First, use Docker BuildKit for faster builds by setting `export DOCKER_BUILDKIT=1` in your shell. BuildKit parallelizes build steps and provides better caching, significantly reducing build times.

Second, leverage Maven's dependency caching by mounting your local .m2 directory into the container. This prevents re-downloading dependencies every time you rebuild. The docker-compose file already includes this optimization.

Third, consider using Docker's tmpfs mounts for test databases. If you're running integration tests frequently, mounting `/var/lib/postgresql/data` as tmpfs puts the database in memory, making tests much faster. Add this to your test service:

```yaml
tmpfs:
  - /var/lib/postgresql/data:size=512M
```

Finally, use docker-compose profiles to start only the services you need. If you're working on just the document upload feature, you might not need Elasticsearch running. Define profiles in your docker-compose file and start specific profiles with `docker compose --profile minimal up`.

## Best Practices for Team Development

When working with a team, consistency is crucial. Here are practices that will keep everyone productive and prevent "works on my machine" problems.

Always use the same Docker Compose version across the team by specifying it in the compose file. Document any local overrides in docker-compose.override.yml, which Docker Compose automatically reads but which you can gitignore to allow personal customization.

Create a `docker-compose.test.yml` for running integration tests. This file can override settings like database names and ports to isolate test runs from development data.

Use Docker secrets for sensitive data, even in development. While the example uses environment variables for simplicity, production-like secret management helps catch configuration issues early.

Regular housekeeping is important. Docker can accumulate unused images and volumes over time. Run `docker system prune -a` weekly to clean up unused resources, but be careful not to remove volumes with important development data.

## Transitioning to Production

While this setup is optimized for development, understanding how it differs from production helps you write better code.

In production, you'll use multi-stage builds to create minimal images without development tools. The Spring Boot application will run with `java -jar` instead of Maven. You'll use proper orchestration like Kubernetes instead of Docker Compose.

Production databases will have persistent storage with backups, not local Docker volumes. Secrets will come from a secret management system like HashiCorp Vault or Kubernetes Secrets. Services will run with resource limits and health checks that trigger automatic restarts.

The beauty of Docker is that your application code doesn't change between development and production - only the infrastructure configuration does. This consistency reduces deployment surprises and gives you confidence that what works locally will work in production.

## Conclusion

This Docker development environment gives you a complete, production-like platform on your local machine. You can develop, test, and debug with confidence, knowing that your environment matches what your teammates and production systems are running.

Remember, the goal isn't just to run containers - it's to create a development experience that lets you focus on building features rather than fighting with environment setup. These tools and patterns will evolve with your project, but the foundation we've built here will serve you throughout the entire development lifecycle of the Apex IDP platform.

As you become comfortable with this setup, you'll discover your own optimizations and workflows. Share these with your team - the best development environments are those that evolve through collective experience and continuous improvement.