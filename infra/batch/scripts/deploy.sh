#!/bin/bash

# 배치 시스템 배포 스크립트
set -e

IMAGE_TAG=${1:-latest}
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BATCH_HOME="/home/ubuntu/threadly-batch"

echo " 배치 시스템 배포 시작 - Image Tag: $IMAGE_TAG"

# 환경변수 업데이트
cd $BATCH_HOME
echo "IMAGE_TAG=$IMAGE_TAG" >> .env

# systemd 서비스 파일 복사
echo " systemd 서비스 파일 설정..."
sudo cp $SCRIPT_DIR/threadly-batch.service /etc/systemd/system/
sudo cp $SCRIPT_DIR/threadly-batch.timer /etc/systemd/system/

# 권한 설정
sudo chmod 644 /etc/systemd/system/threadly-batch.service
sudo chmod 644 /etc/systemd/system/threadly-batch.timer

# systemd 리로드
echo " systemd 설정 리로드..."
sudo systemctl daemon-reload

# 배치 실행 스크립트 권한 설정
chmod +x $SCRIPT_DIR/run-batch.sh
chmod +x $SCRIPT_DIR/health-check.sh

# 서비스 활성화
echo " 배치 타이머 활성화..."
sudo systemctl enable threadly-batch.timer
sudo systemctl start threadly-batch.timer

# 상태 확인
echo " 배치 시스템 상태 확인..."
sudo systemctl status threadly-batch.timer --no-pager
echo ""
echo " 다음 실행 예정 시간:"
sudo systemctl list-timers threadly-batch.timer --no-pager

echo " 배치 시스템 배포 완료!"
echo " 로그 확인: journalctl -u threadly-batch.service -f"
echo " 수동 실행: sudo systemctl start threadly-batch.service"