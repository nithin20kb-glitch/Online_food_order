@echo off
@REM FoodExpress Maven Runner Wrapper

setlocal

if not defined JAVA_HOME (
    if exist "C:\Program Files\Java\jdk-21.0.11" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.11"
    ) else if exist "C:\Program Files\Java\latest" (
        set "JAVA_HOME=C:\Program Files\Java\latest"
    )
)

if defined JAVA_HOME (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
)

set MAVEN_CMD=mvn
where mvn >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    goto :RUN_MAVEN
)

set LOCAL_MVN="%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.10\a38810a491b03367137adfdfbe7d14c4\bin\mvn.cmd"
if exist %LOCAL_MVN% (
    set MAVEN_CMD=%LOCAL_MVN%
    goto :RUN_MAVEN
)

set LOCAL_MVN2="%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.10.0\2aaba761688b1a6ea5a46e7e5f4d5406ed7a75ee8da95e5e3079863a23461c9b\bin\mvn.cmd"
if exist %LOCAL_MVN2% (
    set MAVEN_CMD=%LOCAL_MVN2%
    goto :RUN_MAVEN
)

echo [ERROR] Maven not found. Please install Apache Maven or check PATH.
exit /b 1

:RUN_MAVEN
call %MAVEN_CMD% %*
exit /b %ERRORLEVEL%
