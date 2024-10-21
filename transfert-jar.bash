#!/bin/bash

# Define variables
PROJECT_DIR="/home/m2ipint/abdellatif.kebraoui.etu/Documents/NDD/Spark/mysparkproject"
TARGET_DIR="$PROJECT_DIR/target/scala-2.12"
REMOTE_USER="abdellatif.kebraoui.etu"
REMOTE_HOST="172.28.1.7"
REMOTE_DIR="/home/$REMOTE_USER"

# Navigate to the project directory
cd "$PROJECT_DIR" || { echo "Project directory not found"; exit 1; }

# Compile and package the project
sbt clean package

# Check if the package was successful
if [ $? -ne 0 ]; then
    echo "Compilation and packaging failed"
    exit 1
fi

# Find the JAR file
JAR_FILE=$(ls $TARGET_DIR/*.jar 2>/dev/null)

# Check if JAR file exists
if [ -z "$JAR_FILE" ]; then
    echo "No .jar file found in $TARGET_DIR"
    exit 1
fi

# Transfer the JAR file using scp
scp "$JAR_FILE" "$REMOTE_USER@$REMOTE_HOST:$REMOTE_DIR"

# Check if the transfer was successful
if [ $? -eq 0 ]; then
    echo "Transfer successful"
else
    echo "Transfer failed"
    exit 1
fi