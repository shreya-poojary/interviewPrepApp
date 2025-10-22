#!/bin/bash

# Run script for Linux/Mac

echo "Starting AI Mock Interview Prep Tool..."
echo

# Check if Ollama is running
if ! curl -s http://localhost:11434/api/tags >/dev/null 2>&1; then
    echo "WARNING: Ollama is not running!"
    echo "Please start Ollama first: ollama serve"
    echo
    exit 1
fi

# Run the application
java -Xmx2g -jar target/ai-interview-prep-1.0.0.jar

