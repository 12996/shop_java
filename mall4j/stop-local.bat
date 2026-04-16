@echo off
setlocal

cd /d "%~dp0"

echo [mall4j] Stopping local backend services...

powershell -NoProfile -Command ^
  "$ports = @(18085, 18086); foreach ($port in $ports) { " ^
  "  $conns = Get-NetTCPConnection -State Listen -LocalPort $port -ErrorAction SilentlyContinue; " ^
  "  if (-not $conns) { Write-Host ('[mall4j] No listening process on port ' + $port + '.'); continue } " ^
  "  $targetPids = $conns | Select-Object -ExpandProperty OwningProcess -Unique; " ^
  "  foreach ($targetPid in $targetPids) { " ^
  "    Write-Host ('[mall4j] Stopping PID ' + $targetPid + ' on port ' + $port + '...'); " ^
  "    Stop-Process -Id $targetPid -Force -ErrorAction SilentlyContinue; " ^
  "    if ($?) { Write-Host '[OK] stopped.' } else { Write-Host '[ERROR] stop failed.'; exit 1 } " ^
  "  } " ^
  "}"

if errorlevel 1 (
    echo [ERROR] Failed to stop one or more services.
    if not defined MALL4J_NO_PAUSE pause
    exit /b 1
)

echo.
echo [mall4j] Stop command completed.
if not defined MALL4J_NO_PAUSE pause
