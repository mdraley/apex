# dev-stop.ps1 - Stop the Apex IDP development environment
param(
    [switch]$RemoveVolumes,
    [switch]$RemoveImages
)

function Write-Status {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor Cyan
}

function Write-Success {
    param([string]$Message)
    Write-Host "[SUCCESS] $Message" -ForegroundColor Green
}

Write-Host "🛑 Stopping Apex IDP Development Environment..." -ForegroundColor Blue

# Stop all services
Write-Status "Stopping all Docker services..."
docker-compose down

if ($RemoveVolumes) {
    Write-Status "Removing Docker volumes..."
    docker-compose down -v
    Write-Success "Volumes removed"
}

if ($RemoveImages) {
    Write-Status "Removing built images..."
    docker rmi apex-idp-api_apex-api -f 2>$null
    Write-Success "Images removed"
}

Write-Success "🎉 Apex IDP Development Environment stopped!"

if ($RemoveVolumes) {
    Write-Host ""
    Write-Host "⚠️  All data has been removed. Next startup will recreate sample data." -ForegroundColor Yellow
}
