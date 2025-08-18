# Apex IDP Docker Development Environment

This directory contains the Docker configuration for running the Apex IDP platform in a local development environment.

## 🚀 Quick Start

### Prerequisites
- Docker Desktop installed and running
- PowerShell (for Windows) or Bash (for Linux/macOS)
- Java 17 and Maven 3.9+ (for building)

### Start the Environment

**Windows PowerShell:**
```powershell
.\dev-start.ps1
```

**Linux/macOS Bash:**
```bash
chmod +x dev-start.sh
./dev-start.sh
```

**Manual Docker Compose:**
```bash
# Build the application first
mvn clean package -DskipTests

# Start all services
docker-compose up -d

# Check status
docker-compose ps
```

### Stop the Environment

**Windows PowerShell:**
```powershell
# Stop normally
.\dev-stop.ps1

# Stop and remove volumes (clean slate)
.\dev-stop.ps1 -RemoveVolumes

# Stop and remove everything
.\dev-stop.ps1 -RemoveVolumes -RemoveImages
```

**Manual Docker Compose:**
```bash
# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

## 📋 Services

| Service | Port | Description | Health Check |
|---------|------|-------------|--------------|
| **apex-api** | 8080 | Main Spring Boot API Gateway | http://localhost:8080/api/actuator/health |
| **postgres** | 5432 | PostgreSQL Database | Internal |
| **redis** | 6379 | Redis Cache | Internal |
| **kafka** | 9092 | Apache Kafka Message Broker | Internal |
| **zookeeper** | 2181 | Kafka Zookeeper | Internal |
| **minio** | 9000, 9001 | Object Storage (S3-compatible) | http://localhost:9001 |

## 🔗 Access URLs

- **API Gateway**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/api/swagger-ui/index.html  
- **Health Check**: http://localhost:8080/api/actuator/health
- **MinIO Console**: http://localhost:9001 (admin/minioadmin123)

## 🗄️ Database Configuration

**Connection Details:**
- **Host**: localhost:5432
- **Database**: apex_idp
- **Username**: apex_user
- **Password**: apex_password

**Schemas:**
- `vendor_management` - Vendor-related entities
- `document_processing` - Document-related entities  
- `user_management` - User and authentication entities
- `audit` - Event sourcing and audit logs

## 🐛 Development & Debugging

### Remote Debugging
The API Gateway exposes port 5005 for remote debugging:
- **IntelliJ IDEA**: Run > Edit Configurations > Add Remote JVM Debug > Host: localhost, Port: 5005
- **VS Code**: Configure launch.json with attach configuration for port 5005

### Useful Commands

```bash
# View logs for all services
docker-compose logs -f

# View logs for specific service
docker-compose logs -f apex-api

# Restart just the API (after code changes)
docker-compose restart apex-api

# Rebuild and restart API
docker-compose up --build apex-api

# Execute commands in running containers
docker-compose exec postgres psql -U apex_user -d apex_idp
docker-compose exec redis redis-cli
docker-compose exec apex-api bash

# Check service health
docker-compose ps
curl http://localhost:8080/api/actuator/health
```

### Hot Reload Development

For active development with hot reload:
```bash
# Option 1: Run API outside Docker (requires local DB setup)
docker-compose up -d postgres redis kafka minio
mvn spring-boot:run -pl apex-api-gateway

# Option 2: Mount source code volume (modify docker-compose.yml)
# Add volume mapping for live code editing
```

## 📁 Directory Structure

```
docker/
├── services/
│   └── api/
│       └── Dockerfile.dev        # API development Dockerfile
├── postgres/
│   └── init-scripts/
│       ├── 01-init-database.sql   # Database schema initialization
│       └── 02-sample-data.sql     # Sample development data
docker-compose.yml                 # Main compose configuration
dev-start.ps1                     # Windows start script
dev-start.sh                      # Linux/macOS start script  
dev-stop.ps1                      # Windows stop script
```

## 🔧 Configuration

### Environment Variables

The API Gateway uses these environment variables in Docker:

```yaml
SPRING_PROFILES_ACTIVE: docker
SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/apex_idp
SPRING_DATASOURCE_USERNAME: apex_user
SPRING_DATASOURCE_PASSWORD: apex_password
SPRING_REDIS_HOST: redis
SPRING_REDIS_PORT: 6379
SPRING_REDIS_PASSWORD: apex_redis_password
APEX_STORAGE_MINIO_ENDPOINT: http://minio:9000
APEX_STORAGE_MINIO_ACCESS_KEY: minioadmin
APEX_STORAGE_MINIO_SECRET_KEY: minioadmin123
KAFKA_BOOTSTRAP_SERVERS: kafka:9092
```

### Spring Profiles

- `docker` - Used when running in Docker containers
- `dev` - Local development without Docker
- `prod` - Production configuration

## 🔍 Troubleshooting

### Common Issues

**1. Port Conflicts**
```bash
# Check if ports are in use
netstat -an | grep :8080
netstat -an | grep :5432

# Stop conflicting services or change ports in docker-compose.yml
```

**2. Database Connection Issues**
```bash
# Check if PostgreSQL is healthy
docker-compose exec postgres pg_isready -U apex_user

# Reset database
docker-compose down -v
docker-compose up -d postgres
```

**3. Application Won't Start**
```bash
# Check build errors
mvn clean package -DskipTests

# Check container logs  
docker-compose logs apex-api

# Check if all dependencies are healthy
docker-compose ps
```

**4. Out of Memory**
```bash
# Increase Docker memory in Docker Desktop settings
# Or modify JVM options in Dockerfile.dev
```

### Performance Optimization

**For Development:**
- Increase Docker memory allocation (4GB+ recommended)
- Use Docker volume caching for better I/O performance
- Consider using Docker BuildKit for faster builds

**For Testing:**
- Use test containers for integration tests
- Parallel test execution with isolated databases
- Mock external services when possible

## 📚 Sample Data

The development environment includes sample data:

**Users:**
- admin/password123 (ADMIN role)
- user1/password123 (USER role)  
- manager1/password123 (MANAGER role)

**Vendors:**
- Acme Corporation (ACME001)
- Global Tech Solutions (GTS002)
- Supply Chain Partners (SCP003)
- International Logistics (IL004) - Inactive

**Documents:**
- Sample invoices, purchase orders, and contracts
- Various processing statuses for testing workflows

## 🎯 Next Steps

1. **API Testing**: Use Swagger UI or Postman to test endpoints
2. **Frontend Integration**: Connect your React frontend to http://localhost:8080
3. **Custom Development**: Add new modules following the established patterns
4. **Production Setup**: Adapt configurations for production deployment
