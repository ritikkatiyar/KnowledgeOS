# KnowledgeOS Development Management Script

param (
    [Parameter(Mandatory=$true)]
    [ValidateSet("start", "stop")]
    [string]$Action
)

function Start-Development {
    Write-Host "--- Starting KnowledgeOS Infrastructure ---" -ForegroundColor Cyan
    docker-compose up -d

    Write-Host "--- Starting Backend (Spring Boot 4.0.6) ---" -ForegroundColor Green
    $backendCmd = "cd backend; mvn spring-boot:run"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $backendCmd

    Write-Host "--- Starting Frontend (Next.js 16) ---" -ForegroundColor Blue
    $frontendCmd = "cd frontend; npm run dev"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $frontendCmd

    Write-Host "--- All systems are booting up! ---" -ForegroundColor Cyan
    Write-Host "Backend:  http://localhost:8081"
    Write-Host "Frontend: http://localhost:3000"
    Write-Host "Adminer:  http://localhost:8080"
}

function Stop-Development {
    Write-Host "--- Stopping KnowledgeOS Infrastructure ---" -ForegroundColor Red
    docker-compose down

    Write-Host "--- Killing Backend and Frontend processes ---" -ForegroundColor Red
    Get-Process -Name "java" -ErrorAction SilentlyContinue | Stop-Process -Force
    Get-Process -Name "node" -ErrorAction SilentlyContinue | Stop-Process -Force

    Write-Host "--- All systems stopped ---" -ForegroundColor Cyan
}

if ($Action -eq 'start') {
    Start-Development
}
if ($Action -eq 'stop') {
    Stop-Development
}
