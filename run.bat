@echo off
setlocal EnableDelayedExpansion
title FoodExpress - Starting Application...
color 0A

cd /d "%~dp0"
set "PROJECT_DIR=%~dp0"

echo =====================================================================
echo           FoodExpress Online Food Ordering System
echo =====================================================================
echo.

:: 1. Check if running from inside an unextracted ZIP folder
echo "%PROJECT_DIR%" | findstr /I "AppData\Local\Temp" >nul 2>&1
if !ERRORLEVEL! EQU 0 (
    color 0C
    echo [ERROR] YOU ARE RUNNING THIS FROM INSIDE A ZIP FILE!
    echo.
    echo Please EXTRACT [Unzip] the entire folder first:
    echo 1. Right-click the downloaded ZIP file.
    echo 2. Choose "Extract All...".
    echo 3. Open the newly extracted folder and run run.bat again.
    echo.
    pause
    exit /b 1
)

:: 2. Find a working Java 21+ executable
set "JAVA_EXE="

:: Option A: Check JAVA_HOME
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        "%JAVA_HOME%\bin\java.exe" -version >nul 2>&1
        if !ERRORLEVEL! EQU 0 set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
    )
)

:: Option B: Check standard JDK 21 installation folders
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

:: Option C: Check PATH java
if not defined JAVA_EXE (
    where java >nul 2>&1
    if !ERRORLEVEL! EQU 0 (
        java -version >nul 2>&1
        if !ERRORLEVEL! EQU 0 set "JAVA_EXE=java"
    )
)

:: If Java is still not found, show error
if not defined JAVA_EXE (
    color 0C
    echo [ERROR] Working Java 21 was NOT found or not properly configured!
    echo.
    echo FoodExpress requires Java 21 to run.
    echo Please download and install free Java 21 from:
    echo https://adoptium.net/temurin/releases/?version=21
    echo.
    pause
    exit /b 1
)

echo [OK] Using Java runtime:
"%JAVA_EXE%" -version
echo.

:: 3. Check if foodexpress.jar exists
set "JAR_FILE=%PROJECT_DIR%foodexpress.jar"
if not exist "%JAR_FILE%" (
    set "JAR_FILE=%PROJECT_DIR%target\foodexpress-online-food-ordering-1.0.0.jar"
)

if not exist "%JAR_FILE%" (
    color 0C
    echo [ERROR] Could not find foodexpress.jar in:
    echo "%PROJECT_DIR%"
    echo.
    echo Please make sure foodexpress.jar was copied with the folder.
    echo.
    pause
    exit /b 1
)

echo [OK] Found application package:
echo "%JAR_FILE%"
echo.
echo =====================================================================
echo   Starting Spring Boot Server...
echo   Once you see "Started FoodExpressApplication", open in Edge/Chrome:
echo.
echo   WEBSITE: http://localhost:8080
echo =====================================================================
echo.

"%JAVA_EXE%" -jar "%JAR_FILE%" --spring.profiles.active=dev

if !ERRORLEVEL! NEQ 0 (
    color 0C
    echo.
    echo [ERROR] Application stopped unexpectedly with error code !ERRORLEVEL!.
    pause
)
