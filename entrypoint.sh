#!/bin/bash

set -e  # Exit on error

app_env=${1:-development}
PORT=8080

# Function to check and kill process on port
check_and_free_port() {
    local port=$1
    echo "Checking port $port..."
    
    # Try to find process using the port
    local pid=$(netstat -tlnp 2>/dev/null | grep ":$port " | awk '{print $7}' | cut -d'/' -f1 | head -1)
    
    if [ -z "$pid" ]; then
        # Try alternative method
        pid=$(ss -tlnp 2>/dev/null | grep ":$port " | grep -oP 'pid=\K\d+' | head -1)
    fi
    
    if [ -n "$pid" ] && [ "$pid" != "-" ]; then
        echo "Port $port is in use by process $pid. Stopping it..."
        kill $pid 2>/dev/null || kill -9 $pid 2>/dev/null
        sleep 1
        echo "Port $port has been freed."
    else
        echo "Port $port is available."
    fi
}

# Check if required tools are available
check_requirements() {
    if ! command -v mvn &> /dev/null; then
        echo "Error: Maven (mvn) is not installed or not in PATH"
        exit 1
    fi
    
    if ! command -v java &> /dev/null; then
        echo "Error: Java is not installed or not in PATH"
        exit 1
    fi
}

# Development environment commands
dev_commands() {
    echo "Running development environment commands..."
    check_requirements
    check_and_free_port $PORT
    mvn spring-boot:run
}

# Production environment commands
prod_commands() {
    echo "Running production environment commands..."
    check_requirements
    check_and_free_port $PORT
    
    # Build the application
    echo "Building application..."
    mvn clean install -DskipTests
    
    # Find the built JAR file
    JAR_FILE=$(find target -name "*.jar" -not -name "*sources.jar" -not -name "*javadoc.jar" | head -1)
    
    if [ -z "$JAR_FILE" ]; then
        echo "Error: JAR file not found in target directory"
        exit 1
    fi
    
    echo "Starting application with JAR: $JAR_FILE"
    java -jar "$JAR_FILE"
}

# Check environment variables to determine the running environment
if [ "$app_env" = "production" ] || [ "$app_env" = "prod" ]; then
    echo "Production environment detected"
    prod_commands
else
    echo "Development environment detected"
    dev_commands
fi
