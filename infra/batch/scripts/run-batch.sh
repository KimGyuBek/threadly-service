#!/bin/bash

# 배치 실행 스크립트
set -e

BATCH_HOME="/home/ubuntu/threadly-batch"
LOG_FILE="$BATCH_HOME/logs/batch-$(date +%Y%m%d-%H%M%S).log"

echo " 배치 작업 시작 - $(date)" | tee -a $LOG_FILE

cd $BATCH_HOME

# Docker compose 환경 로드
if [ -f .env ]; then
    source .env
    echo " 환경변수 로드 완료" | tee -a $LOG_FILE
else
    echo " .env 파일을 찾을 수 없습니다" | tee -a $LOG_FILE
    exit 1
fi

# Pre-run 체크
echo " Pre-run 체크..." | tee -a $LOG_FILE

# Docker 이미지 확인
if ! docker images | grep -q "${DOCKER_IMAGE_NAME_BATCH:-threadly-batch}"; then
    echo " Docker 이미지를 찾을 수 없습니다: ${DOCKER_IMAGE_NAME_BATCH}" | tee -a $LOG_FILE
    exit 1
fi

# 데이터베이스 연결 확인 (간단한 ping)
echo " 데이터베이스 연결 확인..." | tee -a $LOG_FILE

# 이전 컨테이너 정리
echo " 이전 컨테이너 정리..." | tee -a $LOG_FILE
docker compose down --remove-orphans 2>/dev/null || true

# 배치 실행
echo " 배치 작업 실행..." | tee -a $LOG_FILE
START_TIME=$(date +%s)

# Docker compose로 배치 실행 (detached 모드가 아닌 foreground)
if timeout 1800 docker compose up --abort-on-container-exit; then
    END_TIME=$(date +%s)
    DURATION=$((END_TIME - START_TIME))
    echo " 배치 작업 완료 - 소요시간: ${DURATION}초" | tee -a $LOG_FILE
    EXIT_CODE=0
else
    END_TIME=$(date +%s)
    DURATION=$((END_TIME - START_TIME))
    echo " 배치 작업 실패 또는 타임아웃 - 소요시간: ${DURATION}초" | tee -a $LOG_FILE
    EXIT_CODE=1
fi

# 컨테이너 정리
echo " 후처리 작업..." | tee -a $LOG_FILE
docker compose down 2>/dev/null || true

# 로그 보관 (최근 10개만)
find $BATCH_HOME/logs -name "batch-*.log" -type f | sort -r | tail -n +11 | xargs rm -f 2>/dev/null || true

echo " 배치 스크립트 종료 - $(date)" | tee -a $LOG_FILE
exit $EXIT_CODE