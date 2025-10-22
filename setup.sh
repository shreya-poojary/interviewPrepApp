#!/bin/bash

# Setup script for Linux/Mac

echo "================================"
echo "AI Mock Interview Prep Tool"
echo "Setup Script for Linux/Mac"
echo "================================"
echo

echo "Step 1: Installing Ollama..."
echo
echo "For Mac:"
echo "  brew install ollama"
echo
echo "For Linux:"
echo "  curl https://ollama.ai/install.sh | sh"
echo
echo "After installation, pull the model:"
echo "  ollama pull llama3.1:8b"
echo
read -p "Press enter to continue..."

echo
echo "Step 2: Building the application..."
echo
mvn clean package
echo

echo "Step 3: Creating directories..."
mkdir -p data recordings logs temp mcp_data
echo

echo
echo "================================"
echo "Setup Complete!"
echo "================================"
echo
echo "To run the application:"
echo "  java -jar target/ai-interview-prep-1.0.0.jar"
echo
echo "Or use: ./run.sh"
echo
echo "Make sure Ollama is running before starting the app!"
echo "  ollama serve"
echo

