#!/bin/bash
# dev-start.sh - Start the Apex IDP development environment

set -e

echo "🚀 Starting Apex IDP Development Environment..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker Desktop and try again."
    exit 1
fi

print_status "Docker is running ✓"

# Check if docker-compose is available
if ! command -v docker-compose >/dev/null 2>&1; then
    print_error "docker-compose is not installed or not in PATH"
    exit 1
fi

print_status "docker-compose is available ✓"

# Build the application first
print_status "Building the Spring Boot application..."
if mvn clean package -DskipTests -q; then
    print_success "Application built successfully"
else
    print_error "Failed to build the application"
    exit 1
fi

# Start the infrastructure services first
print_status "Starting infrastructure services (PostgreSQL, Redis, Kafka, MinIO)..."
docker-compose up -d postgres redis zookeeper kafka minio

# Wait for infrastructure services to be healthy
print_status "Waiting for infrastructure services to be ready..."
sleep 10

# Check service health
services=("postgres" "redis" "kafka" "minio")
for service in "${services[@]}"; do
    print_status "Checking $service health..."
    max_attempts=30
    attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if docker-compose exec -T $service echo "Service is running" >/dev/null 2>&1; then
            print_success "$service is ready"
            break
        fi
        
        if [ $attempt -eq $max_attempts ]; then
            print_warning "$service might not be fully ready, but continuing..."
            break
        fi
        
        sleep 2
        ((attempt++))
    done
done

# Start the API application
print_status "Starting Apex API Gateway..."
docker-compose up -d apex-api

# Wait for the API to be ready
print_status "Waiting for API Gateway to be ready..."
max_attempts=60
attempt=1

while [ $attempt -le $max_attempts ]; do
    if curl -f http://localhost:8080/api/actuator/health >/dev/null 2>&1; then
        print_success "API Gateway is ready and healthy!"
        break
    fi
    
    if [ $attempt -eq $max_attempts ]; then
        print_warning "API Gateway might not be fully ready. Check logs with: docker-compose logs apex-api"
        break
    fi
    
    sleep 3
    ((attempt++))
done

echo ""
print_success "🎉 Apex IDP Development Environment is ready!"
echo ""
echo "📋 Service URLs:"
echo "   API Gateway:    http://localhost:8080"
echo "   Swagger UI:     http://localhost:8080/api/swagger-ui/index.html"
echo "   Health Check:   http://localhost:8080/api/actuator/health"
echo "   MinIO Console:  http://localhost:9001 (admin/minioadmin123)"
echo ""
echo "🗄️  Database Connection:"
echo "   Host: localhost:5432"
echo "   Database: apex_idp"
echo "   Username: apex_user"
echo "   Password: apex_password"
echo ""
echo "🔧 Useful Commands:"
echo "   View logs:      docker-compose logs -f [service-name]"
echo "   Stop all:       docker-compose down"
echo "   Restart API:    docker-compose restart apex-api"
echo "   Rebuild API:    docker-compose up --build apex-api"
echo ""
echo "🐛 Debug API (attach to port 5005 in your IDE)"
