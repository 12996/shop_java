@echo off
setlocal

cd /d "%~dp0"

echo [mall4j] Working directory: %CD%
echo.

where mvn >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Maven command not found. Please make sure Maven is installed and added to PATH.
    if not defined MALL4J_NO_PAUSE pause
    exit /b 1
)

echo [mall4j] Checking required local ports...
powershell -NoProfile -Command "$mysql = (Test-NetConnection 127.0.0.1 -Port 3307 -WarningAction SilentlyContinue).TcpTestSucceeded; $redis = (Test-NetConnection 127.0.0.1 -Port 6380 -WarningAction SilentlyContinue).TcpTestSucceeded; if (-not $mysql) { Write-Host '[ERROR] MySQL is not reachable on 127.0.0.1:3307'; exit 1 }; if (-not $redis) { Write-Host '[ERROR] Redis is not reachable on 127.0.0.1:6380'; exit 1 }; Write-Host '[OK] MySQL 3307 and Redis 6380 are reachable.'"
if errorlevel 1 (
    echo.
    echo [ERROR] Dependency check failed. Please start MySQL and Redis first.
    if not defined MALL4J_NO_PAUSE pause
    exit /b 1
)

echo.
echo [mall4j] Building and installing project modules to local Maven repository...
call mvn -DskipTests install
if errorlevel 1 (
    echo.
    echo [ERROR] Maven install failed. Fix the errors above and run the script again.
    if not defined MALL4J_NO_PAUSE pause
    exit /b 1
)

echo.
echo [mall4j] Starting yami-shop-admin on port 18085...
start "mall4j-admin" cmd /k "cd /d %CD% && mvn -f yami-shop-admin/pom.xml spring-boot:run -DskipTests"

echo [mall4j] Starting yami-shop-api on port 18086...
start "mall4j-api" cmd /k "cd /d %CD% && mvn -f yami-shop-api/pom.xml spring-boot:run -DskipTests"

echo.
echo [mall4j] Startup commands have been sent.
echo [mall4j] Admin: http://localhost:18085/doc.html
echo [mall4j] API  : http://localhost:18086/doc.html
echo.
echo [mall4j] You can close this window now.
if not defined MALL4J_NO_PAUSE pause
