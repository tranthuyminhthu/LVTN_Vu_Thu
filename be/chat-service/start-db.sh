#!/bin/bash

echo "Starting MongoDB for Chat Service..."

# Stop existing containers if any
docker-compose down

# Start MongoDB and Mongo Express
docker-compose up -d

echo "MongoDB is starting..."
echo "MongoDB will be available at: localhost:27017"
echo "Mongo Express (Web UI) will be available at: http://localhost:8081"
echo "Username: admin, Password: password"

# Wait for MongoDB to be ready
echo "Waiting for MongoDB to be ready..."
sleep 10

echo "MongoDB is ready!"
echo "You can now start your Chat Service application." 