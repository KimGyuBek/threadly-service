#!/bin/bash

set -e

LOG_FILE="../logs/clean-old-images.log"

mkdir -p "$(dirname "$LOG_FILE")"
exec > >(tee -a "$LOG_FILE") 2>&1

IMAGE_NAME=$1
IMAGE_TAG=$2

log() {
  echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1"
}

log "======Cleaning old Docker images start======"
log "-Target Repository=${IMAGE_NAME}"
log "-Target Tag=${IMAGE_TAG}"

DELETE_TARGETS=$(docker images $IMAGE_NAME --format '{{.Repository}}:{{.Tag}}' | grep -v -e "${IMAGE_NAME}:${IMAGE_TAG}" || true)

if [[ -z "$DELETE_TARGETS" ]]; then
  log "-No old images to delete."
else
  echo "$DELETE_TARGETS" | xargs -r docker rmi
  log "-Deleted Images:"
  while IFS= read -r line; do
    log "  - $line"
  done <<< $"DELETE_TARGET"
fi
log "======Cleaning finished======"