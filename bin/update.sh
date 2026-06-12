#!/usr/bin/env bash

# Exit immediately if a command exits with a non-zero status
set -e

# Get the absolute path of the repository root (bin/../)
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo "=== Updating dependencies in API (Maven) ==="
cd "$REPO_ROOT/api"
if [ -f "./mvnw" ]; then
    chmod +x ./mvnw
    echo "Updating Maven parent POM, dependencies, and properties..."
    # -DgenerateBackupPoms=false prevents leaving pom.xml.versionsBackup files
    ./mvnw versions:update-parent versions:use-latest-releases versions:update-properties -DgenerateBackupPoms=false
    
    echo "Resolving dependencies..."
    ./mvnw dependency:resolve
else
    echo "Warning: ./mvnw not found in $REPO_ROOT/api"
fi

echo ""
echo "=== Updating dependencies in Web (Bun/NPM) ==="
cd "$REPO_ROOT/web"
if command -v bun >/dev/null 2>&1; then
    echo "Bun detected. Updating dependencies with Bun..."
    bun update
    echo "Running bun install to ensure lockfile is up to date..."
    bun install
elif command -v npm >/dev/null 2>&1; then
    echo "Bun not detected, but npm found. Updating dependencies with NPM..."
    npm update
    echo "Running npm install to ensure package-lock.json is up to date..."
    npm install
else
    echo "Warning: Neither Bun nor NPM found. Skipping Web dependency updates."
fi

echo ""
echo "=== All updates completed successfully! ==="
