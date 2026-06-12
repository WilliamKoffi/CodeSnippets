#!/usr/bin/env bash

# Exit immediately if a command exits with a non-zero status
set -e

# Get the absolute path of the repository root
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# Function to stop background processes when the script is terminated
cleanup() {
    echo ""
    echo "Stopping development servers..."
    # Kill all child processes of this script
    pkill -P $$ 2>/dev/null || true
    exit 0
}

# Trap SIGINT, SIGTERM, and EXIT to run cleanup
trap cleanup SIGINT SIGTERM EXIT

# Start Docker db service if docker compose is available
if docker compose version >/dev/null 2>&1; then
    echo "=== Stopping any running API/Web Docker containers ==="
    docker compose stop api web 2>/dev/null || true
    echo "=== Starting database container via Docker Compose ==="
    docker compose up -d db
elif command -v docker-compose >/dev/null 2>&1; then
    echo "=== Stopping any running API/Web Docker containers ==="
    docker-compose stop api web 2>/dev/null || true
    echo "=== Starting database container via docker-compose ==="
    docker-compose up -d db
else
    echo "=== Skipping database container startup (Docker Compose not found) ==="
    echo "Please ensure a PostgreSQL instance is running on localhost:5432/snippets_db"
fi

echo ""
echo "=== Starting API Dev Server (Spring Boot) ==="
cd "$REPO_ROOT/api"
if [ -f "./mvnw" ]; then
    chmod +x ./mvnw
    # Start Maven spring-boot:run in the background
    ./mvnw spring-boot:run &
    API_PID=$!
else
    echo "Error: ./mvnw not found in $REPO_ROOT/api"
    exit 1
fi

echo ""
echo "=== Starting Web Dev Server (Bun/NPM) ==="
cd "$REPO_ROOT/web"
if command -v bun >/dev/null 2>&1; then
    bun start &
    WEB_PID=$!
elif command -v npm >/dev/null 2>&1; then
    npm start &
    WEB_PID=$!
else
    echo "Error: Neither bun nor npm found in path."
    exit 1
fi

echo ""
echo "=== Both development servers are now starting! ==="
echo "API PID: $API_PID"
echo "Web PID: $WEB_PID"
echo "Press Ctrl+C to stop both servers."

# Wait for both background processes
wait $API_PID $WEB_PID
