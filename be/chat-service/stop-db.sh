#!/bin/bash

echo "Stopping MongoDB for Chat Service..."

# Stop and remove containers
docker-compose down

# Remove volumes (optional - uncomment if you want to clear data)
# docker-compose down -v

echo "MongoDB stopped successfully!" 