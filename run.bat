@echo off
REM Run script for Windows

echo Starting AI Mock Interview Prep Tool...
echo.

REM Check if Ollama is running
curl -s http://localhost:11434/api/tags >nul 2>&1
if %errorlevel% neq 0 (
    echo WARNING: Ollama is not running!
    echo Please start Ollama first: ollama serve
    echo.
    pause
    exit /b 1
)

REM Run the application
java -Xmx2g -jar target\ai-interview-prep-1.0.0.jar

pause

