#!/bin/bash

source "scripts/.env"

bash ./scripts/sync.sh;

ssh $SERVER "docker compose -f $PROJECT_PATH/docker/prod/docker-compose.prod.yml up --build -d";
ssh $SERVER "docker container prune -f; docker image prune -f; docker network prune -f;";
echo "App restarted on server";




