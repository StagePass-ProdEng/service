#!/bin/bash
set -x

mkdir -p /workspaces/jenkins_config
./build.sh
docker compose --profile mongo --profile prod-eng-service up -d
