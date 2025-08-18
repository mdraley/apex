# dev-start.ps1 - Start the Apex IDP development environment (PowerShell)
param(
    [switch]$SkipBuild,
    [switch]$Rebuild
)

# Function to write colored output
function Write-Status {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor Cyan
}

function Write-Success {
    param([string]$Message)
    Write-Host "[SUCCESS] $Message" -ForegroundColor Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARNING] $Message" -ForegroundColor Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor Red
}

Write-Host "🚀 Starting Apex IDP Development Environment..." -ForegroundColor Blue

# Check if Docker is running
try {
    docker info *>$null
    Write-Status "Docker is running ✓"
} catch {
    Write-Error "Docker is not running. Please start Docker Desktop and try again."
    exit 1
}

# Check if docker-compose is available
if (!(Get-Command docker-compose -ErrorAction SilentlyContinue)) {
    Write-Error "docker-compose is not installed or not in PATH"
    exit 1
}
Write-Status "docker-compose is available ✓"

# Build the application first (unless skipped)
if (-not $SkipBuild) {
    Write-Status "Building the Spring Boot application..."
    mvn clean package -DskipTests -q
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Application built successfully"
    } else {
        Write-Error "Failed to build the application"
        exit 1
    }
}

# Start the infrastructure services first
Write-Status "Starting infrastructure services (PostgreSQL, Redis, Kafka, MinIO)..."
docker-compose up -d postgres redis zookeeper kafka minio

# Wait for infrastructure services to be ready
Write-Status "Waiting for infrastructure services to be ready..."
Start-Sleep -Seconds 10

# Check service health
$services = @("postgres", "redis", "kafka", "minio")
foreach ($service in $services) {
    Write-Status "Checking $service health..."
    $maxAttempts = 30
    $attempt = 1
    
    while ($attempt -le $maxAttempts) {
        try {
            docker-compose exec -T $service echo "Service is running" *>$null
            Write-Success "$service is ready"
            break
        } catch {
            if ($attempt -eq $maxAttempts) {
                Write-Warning "$service might not be fully ready, but continuing..."
                break
            }
        }
        
        Start-Sleep -Seconds 2
        $attempt++
    }
}

# Start the API application
if ($Rebuild) {
    Write-Status "Rebuilding and starting Apex API Gateway..."
    docker-compose up --build -d apex-api
} else {
    Write-Status "Starting Apex API Gateway..."
    docker-compose up -d apex-api
}

# Wait for the API to be ready
Write-Status "Waiting for API Gateway to be ready..."
$maxAttempts = 60
$attempt = 1

while ($attempt -le $maxAttempts) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/api/actuator/health" -UseBasicParsing -TimeoutSec 3
        if ($response.StatusCode -eq 200) {
            Write-Success "API Gateway is ready and healthy!"
            break
        }
    } catch {
        if ($attempt -eq $maxAttempts) {
            Write-Warning "API Gateway might not be fully ready. Check logs with: docker-compose logs apex-api"
            break
        }
    }
    
    Start-Sleep -Seconds 3
    $attempt++
}

Write-Host ""
Write-Success "🎉 Apex IDP Development Environment is ready!"
Write-Host ""
Write-Host "📋 Service URLs:" -ForegroundColor Yellow
Write-Host "   API Gateway:    http://localhost:8080"
Write-Host "   Swagger UI:     http://localhost:8080/api/swagger-ui/index.html"
Write-Host "   Health Check:   http://localhost:8080/api/actuator/health"
Write-Host "   MinIO Console:  http://localhost:9001 (admin/minioadmin123)"
Write-Host ""
Write-Host "🗄️  Database Connection:" -ForegroundColor Yellow
Write-Host "   Host: localhost:5432"
Write-Host "   Database: apex_idp"
Write-Host "   Username: apex_user"
Write-Host "   Password: apex_password"
Write-Host ""
Write-Host "🔧 Useful Commands:" -ForegroundColor Yellow
Write-Host "   View logs:      docker-compose logs -f [service-name]"
Write-Host "   Stop all:       docker-compose down"
Write-Host "   Restart API:    docker-compose restart apex-api"
Write-Host "   Rebuild API:    docker-compose up --build apex-api"
Write-Host ""
Write-Host "🐛 Debug API (attach to port 5005 in your IDE)" -ForegroundColor Magenta

# Optionally open browser
$openBrowser = Read-Host "Open Swagger UI in browser? (y/N)"
if ($openBrowser -eq "y" -or $openBrowser -eq "Y") {
    Start-Process "http://localhost:8080/api/swagger-ui/index.html"
}
