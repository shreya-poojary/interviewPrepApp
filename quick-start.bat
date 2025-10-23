@echo off
setlocal enabledelayedexpansion

echo ================================
echo AI Interview Prep - Quick Start
echo ================================
echo.

REM Check Java
echo [1/6] Checking Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo    X Java not found! Please install Java 17+
    echo      Download: https://adoptium.net/
    pause
    exit /b 1
)
echo    √ Java is installed

REM Check Maven
echo [2/6] Checking Maven...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo    X Maven not found! 
    echo      Run: install-maven.bat
    echo      Or download from: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)
echo    √ Maven is installed

REM Check if Ollama is installed
echo [3/6] Checking Ollama...
where ollama >nul 2>&1
if %errorlevel% neq 0 (
    echo    X Ollama not found!
    echo      Download from: https://ollama.ai/download
    echo      Then run: ollama pull llama3.1:8b
    pause
    exit /b 1
)
echo    √ Ollama is installed

REM Check if Ollama is running
echo [4/6] Checking if Ollama is running...
curl -s http://localhost:11434/api/tags >nul 2>&1
if %errorlevel% neq 0 (
    echo    ! Ollama is not running
    echo      Starting Ollama in background...
    start "Ollama Server" /MIN ollama serve
    timeout /t 3 >nul
    echo    √ Ollama started
) else (
    echo    √ Ollama is running
)

REM Build if JAR doesn't exist
echo [5/6] Checking application...
if not exist "target\ai-interview-prep-1.0.0.jar" (
    echo    ! JAR not found, building project...
    echo      This may take 2-5 minutes on first run...
    call mvn clean package -q
    if %errorlevel% neq 0 (
        echo    X Build failed! Check output above
        pause
        exit /b 1
    )
    echo    √ Build complete
) else (
    echo    √ Application ready
)

REM Run application
echo [6/6] Starting application...
echo.
echo ================================
echo Application is starting!
echo ================================
echo.
echo Tips:
echo   - Upload your resume first
echo   - Add job description
echo   - Get AI analysis
echo   - Start practicing!
echo.
echo Press Ctrl+C to stop the application
echo ================================
echo.

java -jar target\ai-interview-prep-1.0.0.jar

echo.
echo Application closed.
pause

