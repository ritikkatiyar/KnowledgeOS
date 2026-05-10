@echo off
if "%1"=="start" (
    powershell -ExecutionPolicy Bypass -File .\dev.ps1 start
) else if "%1"=="stop" (
    powershell -ExecutionPolicy Bypass -File .\dev.ps1 stop
) else (
    echo Usage: run start ^| run stop
)
