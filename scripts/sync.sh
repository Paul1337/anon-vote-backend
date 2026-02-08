#!/bin/bash

source "scripts/.env"
DOCKER_PATH="$PROJECT_PATH/docker/prod"

echo "Sync start";

ssh $SERVER "mkdir -p $DOCKER_PATH"

rsync pom.xml $SERVER:$PROJECT_PATH/pom.xml
rsync -az --delete ./src $SERVER:$PROJECT_PATH/
rsync -az --exclude .env --delete ./docker/prod/ $SERVER:$DOCKER_PATH

echo "Rsync files complete";


