#!/bin/bash
# EC2 서버에서 실행할 명령어들

echo " EC2 배치 서버 초기 설정 시작"

# 1. 배치 디렉토리 생성
sudo mkdir -p /home/ubuntu/threadly-batch/{logs,config,scripts}
sudo chown -R ubuntu:ubuntu /home/ubuntu/threadly-batch

# 2. Docker 및 Docker Compose 설치 (필요한 경우)
sudo apt-get update
sudo apt-get install -y docker.io docker-compose-plugin
sudo usermod -aG docker ubuntu

# 3. 필요한 도구 설치
sudo apt-get install -y curl jq

# 4. 로그 로테이션 설정
sudo tee /etc/logrotate.d/threadly-batch << 'EOF'
/home/ubuntu/threadly-batch/logs/*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    create 644 ubuntu ubuntu
}
EOF

echo " EC2 서버 설정 완료!"
echo " 다음 단계: GitHub Actions에서 배포를 실행하세요"