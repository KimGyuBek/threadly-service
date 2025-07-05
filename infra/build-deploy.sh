#!/bin/bash
set -e

DATE=$(date +%Y%m%d)
COMMIT=$(git rev-parse --short HEAD)
IMAGE_TAG="${DATE}-${COMMIT}"

echo "Building image: kimgyubek/threadly-app:$IMAGE_TAG"

IMAGE_TAG=$IMAGE_TAG docker compose build

echo "Pushing to Docker hub.."
echo "IMAGE_TAG=$IMAGE_TAG"
