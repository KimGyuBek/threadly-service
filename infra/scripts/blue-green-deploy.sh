#!/bin/bash
set -e

# 로깅
LOG_FILE="/home/ubuntu/threadly/logs/scripts/blue-green-deploy.log"
mkdir -p "$(dirname "$LOG_FILE")"
exec >> "$LOG_FILE" 2>&1

log() {
  echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1"
}

NGINX_CONF="/etc/nginx/sites-available/default"


log "======Deploy start======"

#현재 포트 확인
CURRENT_PORT=$(grep "proxy_pass" "$NGINX_CONF" | grep -o '[0-9]\+')

if [ "$CURRENT_PORT" = "8080" ]; then
  CURRENT="blue"
  NEXT="green"
  CURRENT_PORT=8080
  NEXT_PORT=8081
else
  CURRENT="green"
  NEXT="blue"
  CURRENT_PORT"8081"
  NEXT_PORT"8080"
fi

log "현재 버전: $CURRENT ($CURRENT_PORT), 배포할 버전: $NEXT ($NEXT_PORT)"

# 다음 버전 실행
log "새로운 버전($NEXT) 실행 중..."
docker compose -f /home/ubuntu/threadly/infra/app/docker-compose.$NEXT.yml up -d --build
sleep 5

# Health Check
log "Health Check 시작..."
sleep 5

if ! curl -fs http://localhost:$NEXT_PORT/actuator/health > /dev/null; then
  log "Health Check 실패. 롤백 수행..."
  docker compose -f /home/ubuntu/threadly/infra/app/docker-compose.$NEXT.yml down
fi

log "Health Check 성공!"

# Nginx 포트 전환
log "Nginx 포트 전환 중..."
sed -i "s/$CURRENT_PORT/$NEXT_PORT" "$NGINX_CONF"
nginx -s reload
log "Nginx Reload 완료"

# 기존 버전 종료
log "기존 버전($CURRENT) 종료 중..."
docker compose -f /home/ubuntu/threadly/infra/app/docker-compose.$CURRENT.yml down

log "무중단 배포 완료"
