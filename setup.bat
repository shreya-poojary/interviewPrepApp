@echo off
REM Setup script for Windows

echo ================================
echo AI Mock Interview Prep Tool
echo Setup Script for Windows
echo ================================
echo.

echo Step 1: Installing Ollama...
echo.
echo Please visit: https://ollama.ai/download
echo Download and install Ollama for Windows
echo After installation, run: ollama pull llama3.1:8b
echo.
pause

echo Step 2: Installing Whisper...
echo.
echo Requirements: Python 3.8+ and pip
python --version
echo.
echo Installing OpenAI Whisper...
pip install -U openai-whisper
echo.

echo Step 3: Installing Piper TTS...
echo.
echo Download Piper from: https://github.com/rhasspy/piper/releases
echo Extract to a folder and add to PATH
echo.
pause

echo Step 4: Building the application...
echo.
call mvn clean package
echo.

echo Step 5: Creating directories...
mkdir data 2>nul
mkdir recordings 2>nul
mkdir logs 2>nul
mkdir temp 2>nul
mkdir mcp_data 2>nul

echo.
echo ================================
echo Setup Complete!
echo ================================
echo.
echo To run the application:
echo   java -jar target\ai-interview-prep-1.0.0.jar
echo.
echo Or use: run.bat
echo.
echo Make sure Ollama is running before starting the app!
echo   ollama serve
echo.
pause

