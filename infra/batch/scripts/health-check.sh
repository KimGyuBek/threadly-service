!/bin/bash

# 배치 시스템 헬스체크 스크립트
set -e

BATCH_HOME="/home/ubuntu/threadly-batch"

echo " Threadly Batch 시스템 헬스체크"
echo "=================================="

# systemd 타이머 상태 확인
echo " Timer 상태:"
sudo systemctl is-active threadly-batch.timer && echo " 활성화됨" || echo " 비활성화됨"

echo ""
echo " 다음 실행 예정:"
sudo systemctl list-timers threadly-batch.timer --no-pager | grep threadly-batch || echo "스케줄 없음"

echo ""
echo " 최근 실행 로그 (최근 5개):"
sudo journalctl -u threadly-batch.service --no-pager -n 10 | grep -E "(Started|Finished|failed)" | tail -5 || echo "로그 없음"

echo ""
echo " 디스크 사용량:"
df -h $BATCH_HOME

echo ""
echo "📁 로그 파일 상태:"
if [ -d "$BATCH_HOME/logs" ]; then
    ls -la $BATCH_HOME/logs/ | tail -5
    echo "로그 파일 개수: $(ls $BATCH_HOME/logs/batch-*.log 2>/dev/null | wc -l)"
else
    echo "로그 디렉토리 없음"
fi

echo ""
echo " Docker 이미지 상태:"
if [ -f "$BATCH_HOME/.env" ]; then
    source $BATCH_HOME/.env
    if docker images | grep -q "${DOCKER_IMAGE_NAME_BATCH:-threadly-batch}"; then
        echo " 배치 이미지 존재: ${DOCKER_IMAGE_NAME_BATCH}:${IMAGE_TAG:-latest}"
    else
        echo " 배치 이미지 없음"
    fi
else
    echo " .env 파일 없음"
fi

echo ""
echo "🔧 수동 명령어:"
echo "  타이머 시작: sudo systemctl start threadly-batch.timer"
echo "  타이머 중지: sudo systemctl stop threadly-batch.timer"  
echo "  수동 실행: sudo systemctl start threadly-batch.service"
echo "  실시간 로그: sudo journalctl -u threadly-batch.service -f"

echo ""
echo "헬스체크 완료! "