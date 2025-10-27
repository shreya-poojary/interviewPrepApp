#!/bin/bash

echo "========================================"
echo "AI Mock Interview Prep Tool - Test Runner"
echo "========================================"
echo

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed or not in PATH"
    echo "Please install Maven and add it to your PATH"
    exit 1
fi

echo "[1/4] Cleaning previous build..."
mvn clean
if [ $? -ne 0 ]; then
    echo "ERROR: Clean failed"
    exit 1
fi

echo
echo "[2/4] Compiling source code..."
mvn compile
if [ $? -ne 0 ]; then
    echo "ERROR: Compilation failed"
    exit 1
fi

echo
echo "[3/4] Compiling test code..."
mvn test-compile
if [ $? -ne 0 ]; then
    echo "ERROR: Test compilation failed"
    exit 1
fi

echo
echo "[4/4] Running tests..."
mvn test
if [ $? -ne 0 ]; then
    echo
    echo "ERROR: Some tests failed"
    echo "Check the output above for details"
    exit 1
fi

echo
echo "========================================"
echo "All tests passed successfully!"
echo "========================================"
echo
echo "Test reports are available in:"
echo "- target/surefire-reports/"
echo "- target/site/surefire-report.html"
echo
