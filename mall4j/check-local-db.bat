@echo off
setlocal

cd /d "%~dp0"

set "MYSQL_EXE="
if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" set "MYSQL_EXE=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
if not defined MYSQL_EXE if exist "C:\Program Files\MySQL\MySQL Server 5.7\bin\mysql.exe" set "MYSQL_EXE=C:\Program Files\MySQL\MySQL Server 5.7\bin\mysql.exe"
if not defined MYSQL_EXE for %%I in (mysql.exe) do set "MYSQL_EXE=%%~$PATH:I"

if not defined MYSQL_EXE (
    echo [ERROR] mysql.exe not found.
    echo [ERROR] Please install MySQL client or add mysql.exe to PATH.
    pause
    exit /b 1
)

set "MYSQL_HOST=%MYSQL_HOST%"
if not defined MYSQL_HOST set "MYSQL_HOST=127.0.0.1"
set "MYSQL_PORT=%MYSQL_PORT%"
if not defined MYSQL_PORT set "MYSQL_PORT=3307"
set "MYSQL_USERNAME=%MYSQL_USERNAME%"
if not defined MYSQL_USERNAME set "MYSQL_USERNAME=root"
set "MYSQL_PASSWORD=%MYSQL_PASSWORD%"
if not defined MYSQL_PASSWORD set "MYSQL_PASSWORD=root"
set "MYSQL_DATABASE=%MYSQL_DATABASE%"
if not defined MYSQL_DATABASE set "MYSQL_DATABASE=yami_shops"

echo [mall4j] Checking MySQL connection...
"%MYSQL_EXE%" --host=%MYSQL_HOST% --port=%MYSQL_PORT% --user=%MYSQL_USERNAME% --password=%MYSQL_PASSWORD% -e "SHOW DATABASES LIKE '%MYSQL_DATABASE%';"
if errorlevel 1 (
    echo.
    echo [ERROR] Failed to connect to MySQL or query database list.
    echo [ERROR] Expected: %MYSQL_HOST%:%MYSQL_PORT% / %MYSQL_DATABASE%
    pause
    exit /b 1
)

echo.
echo [mall4j] Checking tables in %MYSQL_DATABASE%...
"%MYSQL_EXE%" --host=%MYSQL_HOST% --port=%MYSQL_PORT% --user=%MYSQL_USERNAME% --password=%MYSQL_PASSWORD% -e "USE %MYSQL_DATABASE%; SHOW TABLES;"
if errorlevel 1 (
    echo.
    echo [ERROR] Database exists check passed, but USE %MYSQL_DATABASE% failed.
    echo [ERROR] Your IDEA connection is probably pointing to a different MySQL instance or wrong port.
    pause
    exit /b 1
)

echo.
echo [mall4j] Database check passed.
echo [mall4j] Current connection: %MYSQL_HOST%:%MYSQL_PORT% / %MYSQL_DATABASE%
pause
