@echo off
echo ========================================
echo AI Mock Interview Prep Tool - Test Runner
echo ========================================
echo.

REM Check if Maven is available
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven and add it to your PATH
    pause
    exit /b 1
)

echo [1/4] Cleaning previous build...
call mvn clean
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Clean failed
    pause
    exit /b 1
)

echo.
echo [2/4] Compiling source code...
call mvn compile
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Compilation failed
    pause
    exit /b 1
)

echo.
echo [3/4] Compiling test code...
call mvn test-compile
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Test compilation failed
    pause
    exit /b 1
)

echo.
echo [4/4] Running tests...
call mvn test
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Some tests failed
    echo Check the output above for details
    pause
    exit /b 1
)

echo.
echo ========================================
echo All tests passed successfully!
echo ========================================
echo.
echo Test reports are available in:
echo - target/surefire-reports/
echo - target/site/surefire-report.html
echo.
pause
