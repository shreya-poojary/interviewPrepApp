@echo off
echo ================================
echo Maven Installation Helper
echo ================================
echo.

echo Checking if Chocolatey is installed...
where choco >nul 2>&1
if %errorlevel% equ 0 (
    echo Chocolatey found! Installing Maven...
    choco install maven -y
    echo.
    echo Maven installed! Please restart your terminal.
) else (
    echo Chocolatey not found.
    echo.
    echo Please install Maven manually:
    echo 1. Download from: https://maven.apache.org/download.cgi
    echo 2. Extract to C:\Program Files\Apache\maven
    echo 3. Add C:\Program Files\Apache\maven\bin to PATH
    echo 4. Restart terminal
    echo.
    echo Or install Chocolatey first:
    echo    PowerShell: Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
)

pause

