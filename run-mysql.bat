@echo off
setlocal EnableDelayedExpansion
title FoodExpress - Starting MySQL Application...

cd /d "%~dp0"
set "PROJECT_DIR=%~dp0"

echo =====================================================================
echo    Starting FoodExpress Online Food Ordering System (MySQL Mode)...
echo =====================================================================
echo.
echo Website URL:    http://localhost:8080
echo Admin Portal:   http://localhost:8080/admin/dashboard
echo.
echo Ensure MySQL is running on localhost:3306 and database 'foodexpress_db' exists.
echo ---------------------------------------------------------------------
echo.

:: Find a working Java 21+ executable
set "JAVA_EXE="

if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        "%JAVA_HOME%\bin\java.exe" -version >nul 2>&1
        if !ERRORLEVEL! EQU 0 set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
    )
)

if not defined JAVA_EXE (
    if exist "C:\Program Files\Java\jdk-21.0.11\bin\java.exe" (
        set "JAVA_EXE=C:\Program Files\Java\jdk-21.0.11\bin\java.exe"
    ) else if exist "C:\Program Files\Java\latest\bin\java.exe" (
        set "JAVA_EXE=C:\Program Files\Java\latest\bin\java.exe"
    ) else (
        for /d %%D in ("C:\Program Files\Java\jdk-21*") do (
            if exist "%%D\bin\java.exe" set "JAVA_EXE=%%D\bin\java.exe"
        )
        if not defined JAVA_EXE (
            for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-21*") do (
                if exist "%%D\bin\java.exe" set "JAVA_EXE=%%D\bin\java.exe"
            )
        )
        if not defined JAVA_EXE (
            for /d %%D in ("C:\Program Files\Java\jdk*") do (
                if exist "%%D\bin\java.exe" set "JAVA_EXE=%%D\bin\java.exe"
            )
        )
    )
)

if not defined JAVA_EXE (
    where java >nul 2>&1
    if !ERRORLEVEL! EQU 0 (
        java -version >nul 2>&1
        if !ERRORLEVEL! EQU 0 set "JAVA_EXE=java"
    )
)

if not defined JAVA_EXE (
    color 0C
    echo [ERROR] Working Java 21 was NOT found or not properly configured!
    echo.
    echo FoodExpress requires Java 21 to run.
    pause
    exit /b 1
)

set "JAR_FILE=%PROJECT_DIR%foodexpress.jar"
if not exist "%JAR_FILE%" (
    set "JAR_FILE=%PROJECT_DIR%target\foodexpress-online-food-ordering-1.0.0.jar"
)

if not exist "%JAR_FILE%" (
    color 0C
    echo [ERROR] Could not find foodexpress.jar in "%PROJECT_DIR%"
    pause
    exit /b 1
)

"%JAVA_EXE%" -jar "%JAR_FILE%"

pause
