# 배치 애플리케이션 EC2 배포 및 스케줄링 가이드

## 1. 전체 아키텍처 개요

### 배포 전략
- **기존 API와 완전 분리**: 별도 EC2 인스턴스에 배포
- **Docker 기반 배포**: 일관성 있는 실행 환경 보장  
- **스케줄링**: systemd timer로 30분마다 자동 실행
- **모니터링**: Prometheus + Grafana 연동

### 디렉토리 구조
```
/home/ubuntu/threadly-batch/
├── docker-compose.yml           # 배치 전용 compose
├── .env                        # 환경 변수
├── config/
│   ├── application-prod.yml    # 배치 설정
│   └── prometheus.yml          # 메트릭 설정
├── scripts/
│   ├── deploy.sh              # 배포 스크립트
│   ├── run-batch.sh           # 배치 실행 스크립트
│   └── health-check.sh        # 헬스체크
└── logs/                      # 실행 로그
```

## 2. GitHub Actions CI/CD 설정

### 2.1 배치 전용 워크플로우 생성

**`.github/workflows/batch-cd.yml`**
```yaml
name: Batch CD

on:
  push:
    branches: [ feature/TLY-97-batch ]
    paths:
      - 'threadly-apps/app-batch/**'
      - 'infra/batch/**'
  workflow_dispatch:
    inputs:
      environment:
        description: 'Target environment'
        required: true
        default: 'prod'
        type: choice
        options:
          - prod
          - dev

jobs:
  build-batch:
    runs-on: ubuntu-latest
    outputs:
      image_tag: ${{ steps.read_tag.outputs.IMAGE_TAG }}
    
    steps:
      - name: 체크아웃
        uses: actions/checkout@v4
      
      - name: JDK 21 설치
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '21'
      
      - name: Gradle 환경 세팅 및 캐싱
        uses: gradle/actions/setup-gradle@v4
      
      - name: 배치 모듈 빌드
        run: |
          ./gradlew :threadly-apps:app-batch:build --no-daemon
      
      - name: IMAGE_TAG 설정
        id: read_tag
        run: |
          echo "IMAGE_TAG=batch-$(date +%Y%m%d%H%M%S)-${GITHUB_SHA:0:7}" >> $GITHUB_OUTPUT
          echo "IMAGE_TAG=batch-$(date +%Y%m%d%H%M%S)-${GITHUB_SHA:0:7}" >> $GITHUB_ENV
      
      - name: Docker 이미지 빌드
        run: |
          docker build --no-cache \\
            -t ${{ secrets.DOCKER_IMAGE_NAME_BATCH }}:$IMAGE_TAG \\
            -f ./infra/docker/batch/Dockerfile .
      
      - name: Docker 로그인
        uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}
      
      - name: Docker 이미지 푸시
        run: |
          docker push ${{ secrets.DOCKER_IMAGE_NAME_BATCH }}:$IMAGE_TAG
      
      - name: 배포 파일 준비
        run: |
          mkdir -p deploy/batch
          cp -r ./infra/batch/* ./deploy/batch/
          cp -r ./infra/config/batch ./deploy/config
          cp -r ./infra/scripts/batch ./deploy/scripts
      
      - name: Artifact 업로드
        uses: actions/upload-artifact@v4
        with:
          name: batch-infra
          path: ./deploy

  deploy-batch:
    needs: build-batch
    runs-on: ubuntu-latest
    steps:
      - name: Artifact 다운로드
        uses: actions/download-artifact@v4
        with:
          name: batch-infra
          path: ./batch-infra
      
      - name: 배치 환경변수 생성
        run: |
          cat > .env << EOF
          # Spring Boot 설정
          SPRING_PROFILES_ACTIVE=${{ secrets.BATCH_SPRING_PROFILE_ACTIVE }}
          
          # 데이터베이스 설정
          SPRING_DATASOURCE_URL=${{ secrets.SPRING_DATASOURCE_URL }}
          SPRING_DATASOURCE_USERNAME=${{ secrets.SPRING_DATASOURCE_USERNAME }}
          SPRING_DATASOURCE_PASSWORD=${{ secrets.SPRING_DATASOURCE_PASSWORD }}
          
          # 배치 설정
          BATCH_GRID_SIZE=4
          
          # Prometheus 설정
          PROMETHEUS_PUSHGATEWAY_URL=${{ secrets.PROMETHEUS_PUSHGATEWAY_URL }}
          
          # Docker 이미지
          BATCH_IMAGE_TAG=${{ needs.build-batch.outputs.image_tag }}
          DOCKER_IMAGE_NAME_BATCH=${{ secrets.DOCKER_IMAGE_NAME_BATCH }}
          EOF
          cp .env ./batch-infra/
      
      - name: SCP 전송
        uses: appleboy/scp-action@v1.0.0
        with:
          host: ${{ secrets.BATCH_EC2_HOST }}
          username: ${{ secrets.BATCH_EC2_USERNAME }}
          key: ${{ secrets.BATCH_EC2_SSH_KEY }}
          port: ${{ secrets.BATCH_EC2_PORT }}
          source: "./batch-infra"
          target: "/home/ubuntu/threadly-batch"
      
      - name: 배치 애플리케이션 배포
        uses: appleboy/ssh-action@v1.0.0
        with:
          host: ${{ secrets.BATCH_EC2_HOST }}
          username: ${{ secrets.BATCH_EC2_USERNAME }}
          key: ${{ secrets.BATCH_EC2_SSH_KEY }}
          port: ${{ secrets.BATCH_EC2_PORT }}
          script: |
            set -e
            cd /home/ubuntu/threadly-batch
            
            # Docker 로그인
            echo "${{ secrets.DOCKER_PASSWORD }}" | docker login -u "${{ secrets.DOCKER_USERNAME }}" --password-stdin
            
            # 최신 이미지 pull
            docker pull ${{ secrets.DOCKER_IMAGE_NAME_BATCH }}:${{ needs.build-batch.outputs.image_tag }}
            
            # 배포 스크립트 실행
            chmod +x ./batch-infra/scripts/deploy.sh
            ./batch-infra/scripts/deploy.sh
            
            # 스케줄러 설정
            sudo systemctl daemon-reload
            sudo systemctl enable threadly-batch.timer
            sudo systemctl start threadly-batch.timer
```

### 2.2 GitHub Secrets 설정

**Actions > Secrets and variables > Actions에서 추가**

```bash
# 배치 전용 EC2 정보
BATCH_EC2_HOST=your-batch-ec2-host
BATCH_EC2_USERNAME=ubuntu
BATCH_EC2_SSH_KEY=your-batch-ec2-private-key
BATCH_EC2_PORT=22

# 배치 전용 Docker 이미지
DOCKER_IMAGE_NAME_BATCH=your-dockerhub-username/threadly-batch

# 배치 환경 설정
BATCH_SPRING_PROFILE_ACTIVE=prod
PROMETHEUS_PUSHGATEWAY_URL=http://your-prometheus-pushgateway:9091
```

## 3. 인프라 파일 생성

### 3.1 배치 전용 Dockerfile

**`infra/docker/batch/Dockerfile`**
```dockerfile
# Multi-stage build for optimized image
FROM gradle:8.5-jdk21 AS build

WORKDIR /app
COPY . .

# Build only the batch module
RUN ./gradlew :threadly-apps:app-batch:bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

# Install required packages
RUN apk add --no-cache curl postgresql-client

WORKDIR /app

# Copy the built JAR
COPY --from=build /app/threadly-apps/app-batch/build/libs/app-batch-*.jar app.jar

# Create non-root user
RUN addgroup -g 1001 -S appuser && \\
    adduser -u 1001 -S appuser -G appuser

# Create directories
RUN mkdir -p /app/logs && \\
    chown -R appuser:appuser /app

USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \\
  CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

# JVM optimization for batch processing
ENV JAVA_OPTS="-Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+UseStringDeduplication"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### 3.2 배치 전용 Docker Compose

**`infra/batch/docker-compose.yml`**
```yaml
version: '3.8'

services:
  threadly-batch:
    image: ${DOCKER_IMAGE_NAME_BATCH}:${BATCH_IMAGE_TAG}
    container_name: threadly-batch
    restart: "no"  # 배치는 일회성 실행
    environment:
      - SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
      - SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL}
      - SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME}
      - SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD}
      - PROMETHEUS_PUSHGATEWAY_URL=${PROMETHEUS_PUSHGATEWAY_URL}
    volumes:
      - ./logs:/app/logs
      - ./config:/app/config
    networks:
      - threadly-batch-network
    logging:
      driver: "json-file"
      options:
        max-size: "100m"
        max-file: "5"
    deploy:
      resources:
        limits:
          cpus: '4.0'
          memory: 8G
        reservations:
          cpus: '2.0'
          memory: 4G

  prometheus-pushgateway:
    image: prom/pushgateway:v1.6.2
    container_name: batch-pushgateway
    restart: unless-stopped
    ports:
      - "9091:9091"
    networks:
      - threadly-batch-network
    volumes:
      - pushgateway_data:/var/lib/pushgateway

volumes:
  pushgateway_data:

networks:
  threadly-batch-network:
    driver: bridge
```

### 3.3 배치 설정 파일

**`infra/config/batch/application-prod.yml`**
```yaml
spring:
  batch:
    job:
      enabled: false  # 스케줄러에서만 실행
    jdbc:
      initialize-schema: always
  
  # 데이터베이스 최적화
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 60000

# 배치 성능 설정
properties:
  retention:
    image:
      deleted: P30D    # 30일
      temporary: P7D   # 7일
    post:
      deleted: P30D    # 30일
    user:
      deleted: P30D    # 30일

# Prometheus 메트릭 설정
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus,info
  metrics:
    export:
      prometheus:
        enabled: true
        pushgateway:
          enabled: true
          base-url: ${PROMETHEUS_PUSHGATEWAY_URL:http://localhost:9091}
          job: threadly-batch
          push-rate: 5s
          push-on-shutdown: true
    tags:
      application: threadly-batch
      environment: prod

# 로깅 최적화
logging:
  level:
    com.threadly.batch: DEBUG
    org.springframework.batch: INFO
    org.springframework.jdbc: INFO
  file:
    name: /app/logs/batch-application.log
  logback:
    rollingpolicy:
      max-file-size: 100MB
      max-history: 30
      total-size-cap: 2GB
```

## 4. 스케줄링 설정 (systemd timer)

### 4.1 배치 실행 스크립트

**`infra/scripts/batch/run-batch.sh`**
```bash
#!/bin/bash

# 배치 실행 스크립트
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BATCH_HOME="/home/ubuntu/threadly-batch"

# 로그 설정
LOG_DIR="$BATCH_HOME/logs"
LOG_FILE="$LOG_DIR/batch-execution-$(date +%Y%m%d).log"

mkdir -p "$LOG_DIR"

log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" | tee -a "$LOG_FILE"
}

# 배치 실행 함수
run_batch() {
    local job_name=$1
    local grid_size=${2:-4}
    
    log "Starting batch job: $job_name with gridSize=$grid_size"
    
    cd "$BATCH_HOME"
    
    # Docker Compose로 배치 실행
    timeout 3600s docker-compose run --rm \\
        -e SPRING_BATCH_JOB_NAME="$job_name" \\
        threadly-batch \\
        java $JAVA_OPTS -jar app.jar \\
        --spring.batch.job.name="$job_name" \\
        --gridSize="$grid_size" \\
        >> "$LOG_FILE" 2>&1
    
    local exit_code=$?
    
    if [ $exit_code -eq 0 ]; then
        log "Batch job $job_name completed successfully"
    else
        log "Batch job $job_name failed with exit code: $exit_code"
        
        # Slack 알림 (선택사항)
        if [[ -n "${SLACK_WEBHOOK_URL:-}" ]]; then
            curl -X POST -H 'Content-type: application/json' \\
                --data "{\\"text\\":\\" Batch job $job_name failed with exit code: $exit_code\\"}" \\
                "$SLACK_WEBHOOK_URL" || true
        fi
    fi
    
    return $exit_code
}

# 메인 실행 로직
main() {
    log "=== Starting Threadly Batch Execution ==="
    
    # 시스템 리소스 체크
    free -h >> "$LOG_FILE"
    df -h >> "$LOG_FILE"
    
    local overall_success=0
    
    # 순서대로 배치 Job 실행 (병렬 실행 가능하지만 안전을 위해 순차 실행)
    
    # 1. 이미지 정리 (용량이 많으므로 먼저)
    if run_batch "imageHardDeleteDeletedJob" 4; then
        log " imageHardDeleteDeletedJob completed"
    else
        log " imageHardDeleteDeletedJob failed"
        ((overall_success++))
    fi
    
    if run_batch "imageHardDeleteTemporaryJob" 4; then
        log " imageHardDeleteTemporaryJob completed"
    else
        log " imageHardDeleteTemporaryJob failed"
        ((overall_success++))
    fi
    
    # 2. 게시글 정리
    if run_batch "postHardDeleteDeletedJob" 4; then
        log " postHardDeleteDeletedJob completed"
    else
        log " postHardDeleteDeletedJob failed"
        ((overall_success++))
    fi
    
    # 3. 사용자 정리 (마지막에 실행)
    if run_batch "userHardDeleteDeletedJob" 4; then
        log " userHardDeleteDeletedJob completed"
    else
        log " userHardDeleteDeletedJob failed"
        ((overall_success++))
    fi
    
    # 정리 작업
    docker system prune -f >> "$LOG_FILE" 2>&1 || true
    
    log "=== Batch execution completed. Failed jobs: $overall_success ==="
    
    # 성공 알림
    if [[ $overall_success -eq 0 ]] && [[ -n "${SLACK_WEBHOOK_URL:-}" ]]; then
        curl -X POST -H 'Content-type: application/json' \\
            --data "{\\"text\\":\\" All Threadly batch jobs completed successfully\\"}" \\
            "$SLACK_WEBHOOK_URL" || true
    fi
    
    exit $overall_success
}

# 스크립트 실행
main "$@"
```

### 4.2 systemd 서비스 설정

**`/etc/systemd/system/threadly-batch.service`**
```ini
[Unit]
Description=Threadly Batch Jobs
After=docker.service
Requires=docker.service

[Service]
Type=oneshot
User=ubuntu
Group=ubuntu
WorkingDirectory=/home/ubuntu/threadly-batch
Environment=JAVA_OPTS="-Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
ExecStart=/home/ubuntu/threadly-batch/batch-infra/scripts/run-batch.sh
TimeoutStartSec=7200
StandardOutput=journal
StandardError=journal

# 리소스 제한
MemoryMax=8G
CPUQuota=400%

# 보안 설정
PrivateTmp=true
ProtectSystem=strict
ProtectHome=false
ReadWritePaths=/home/ubuntu/threadly-batch

[Install]
WantedBy=multi-user.target
```

**`/etc/systemd/system/threadly-batch.timer`**
```ini
[Unit]
Description=Run Threadly Batch Jobs every 30 minutes
Requires=threadly-batch.service

[Timer]
OnCalendar=*:00/30  # 매 30분마다 (정시와 30분)
# OnCalendar=*:00     # 매시간 정시
# OnCalendar=*-*-* 02:00:00  # 매일 새벽 2시
Persistent=true
RandomizedDelaySec=300  # 최대 5분 랜덤 지연

[Install]
WantedBy=timers.target
```

### 4.3 배포 스크립트

**`infra/scripts/batch/deploy.sh`**
```bash
#!/bin/bash

set -euo pipefail

BATCH_HOME="/home/ubuntu/threadly-batch"
SCRIPT_DIR="$BATCH_HOME/batch-infra"

echo " Starting Threadly Batch deployment..."

# 디렉토리 설정
cd "$BATCH_HOME"

# 환경 파일 복사
if [[ -f "$SCRIPT_DIR/.env" ]]; then
    cp "$SCRIPT_DIR/.env" .
    echo " Environment file copied"
fi

# 설정 파일 복사
if [[ -d "$SCRIPT_DIR/config" ]]; then
    cp -r "$SCRIPT_DIR/config" .
    echo " Configuration files copied"
fi

# 스크립트 실행 권한 부여
chmod +x "$SCRIPT_DIR/scripts/"*.sh
echo " Script permissions updated"

# Docker Compose 업데이트
if [[ -f "$SCRIPT_DIR/docker-compose.yml" ]]; then
    cp "$SCRIPT_DIR/docker-compose.yml" .
    echo " Docker Compose file updated"
fi

# systemd 설정 업데이트
echo "🔧 Updating systemd configuration..."

sudo cp "$SCRIPT_DIR/systemd/threadly-batch.service" /etc/systemd/system/
sudo cp "$SCRIPT_DIR/systemd/threadly-batch.timer" /etc/systemd/system/

sudo systemctl daemon-reload
echo " systemd configuration updated"

# 타이머 상태 확인 및 활성화
if systemctl is-active --quiet threadly-batch.timer; then
    sudo systemctl stop threadly-batch.timer
fi

sudo systemctl enable threadly-batch.timer
sudo systemctl start threadly-batch.timer

echo " Batch scheduler activated"

# 상태 확인
systemctl status threadly-batch.timer --no-pager
systemctl list-timers threadly-batch.timer --no-pager

echo "🎉 Threadly Batch deployment completed!"
echo ""
echo "📋 Useful commands:"
echo "  View timer status: systemctl status threadly-batch.timer"
echo "  View logs: journalctl -u threadly-batch.service -f"
echo "  Manual run: sudo systemctl start threadly-batch.service"
echo "  Stop scheduler: sudo systemctl stop threadly-batch.timer"
```

### 4.4 systemd 파일들 추가

**`infra/scripts/batch/systemd/threadly-batch.service`**
```ini
[Unit]
Description=Threadly Batch Jobs
After=docker.service
Requires=docker.service

[Service]
Type=oneshot
User=ubuntu
Group=ubuntu
WorkingDirectory=/home/ubuntu/threadly-batch
Environment=JAVA_OPTS="-Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
ExecStart=/home/ubuntu/threadly-batch/batch-infra/scripts/run-batch.sh
TimeoutStartSec=7200
StandardOutput=journal
StandardError=journal

# 리소스 제한
MemoryMax=8G
CPUQuota=400%

# 보안 설정
PrivateTmp=true
ProtectSystem=strict
ProtectHome=false
ReadWritePaths=/home/ubuntu/threadly-batch

[Install]
WantedBy=multi-user.target
```

**`infra/scripts/batch/systemd/threadly-batch.timer`**
```ini
[Unit]
Description=Run Threadly Batch Jobs every 30 minutes
Requires=threadly-batch.service

[Timer]
OnCalendar=*:00/30
Persistent=true
RandomizedDelaySec=300

[Install]
WantedBy=timers.target
```

## 5. EC2 서버 설정

### 5.1 EC2 인스턴스 사양 권장

```bash
# 최소 사양
Instance Type: t3.large (2 vCPU, 8GB RAM)
Storage: 50GB GP3 SSD
OS: Ubuntu 22.04 LTS

# 권장 사양 (대용량 처리시)
Instance Type: c5.2xlarge (8 vCPU, 16GB RAM)
Storage: 100GB GP3 SSD
```

### 5.2 초기 서버 설정

```bash
# EC2에 SSH 접속 후 실행
ssh -i your-key.pem ubuntu@your-ec2-host

# 시스템 업데이트
sudo apt update && sudo apt upgrade -y

# Docker 설치
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh
sudo usermod -aG docker ubuntu
newgrp docker

# Docker Compose 설치
sudo curl -L "https://github.com/docker/compose/releases/download/v2.21.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 필수 도구 설치
sudo apt install -y curl wget jq htop postgresql-client

# 배치 디렉토리 생성
mkdir -p /home/ubuntu/threadly-batch/{logs,config}
cd /home/ubuntu/threadly-batch

# 권한 설정
sudo chown -R ubuntu:ubuntu /home/ubuntu/threadly-batch
```

### 5.3 보안 그룹 설정

```bash
# Inbound Rules
- SSH (22) from your IP only
- Custom TCP (9091) from Prometheus server (Push Gateway)

# Outbound Rules  
- All traffic (배치가 외부 서비스 호출할 수 있도록)
```

## 6. 운영 명령어

### 6.1 배포 관련

```bash
# 수동 배포 (GitHub Actions 없이)
cd /home/ubuntu/threadly-batch
./batch-infra/scripts/deploy.sh

# 최신 이미지로 업데이트
docker pull your-dockerhub-username/threadly-batch:latest
docker-compose down
docker-compose up -d
```

### 6.2 스케줄러 관리

```bash
# 타이머 상태 확인
systemctl status threadly-batch.timer

# 다음 실행 시간 확인
systemctl list-timers threadly-batch.timer

# 수동 배치 실행
sudo systemctl start threadly-batch.service

# 실행 로그 확인
journalctl -u threadly-batch.service -f

# 스케줄러 중지
sudo systemctl stop threadly-batch.timer

# 스케줄러 재시작
sudo systemctl restart threadly-batch.timer
```

### 6.3 모니터링 명령어

```bash
# 실행 로그 확인
tail -f /home/ubuntu/threadly-batch/logs/batch-execution-$(date +%Y%m%d).log

# 리소스 사용량 확인
docker stats

# 디스크 사용량 확인
df -h
du -sh /home/ubuntu/threadly-batch/logs/

# 메모리 사용량 확인
free -h
```

## 7. 모니터링 및 알림

### 7.1 Prometheus 메트릭 확인

```bash
# Push Gateway 메트릭 확인
curl http://localhost:9091/metrics | grep threadly_batch

# 주요 메트릭
- batch_job_execution_time_seconds
- batch_job_items_processed_total
- batch_job_items_deleted_total
- jvm_memory_used_bytes
- system_cpu_usage
```

### 7.2 Slack 알림 설정

```bash
# .env 파일에 추가
echo "SLACK_WEBHOOK_URL=https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK" >> .env
```

### 7.3 로그 로테이션 설정

```bash
# /etc/logrotate.d/threadly-batch 생성
sudo tee /etc/logrotate.d/threadly-batch << EOF
/home/ubuntu/threadly-batch/logs/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    sharedscripts
    postrotate
        systemctl reload rsyslog > /dev/null 2>&1 || true
    endscript
}
EOF
```

## 8. 트러블슈팅

### 8.1 일반적인 문제들

**메모리 부족**
```bash
# JVM 힙 사이즈 조정
echo 'JAVA_OPTS="-Xms1g -Xmx2g"' >> .env

# Swap 메모리 추가
sudo fallocate -l 4G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
```

**Docker 용량 부족**
```bash
# 사용하지 않는 이미지/컨테이너 정리
docker system prune -af --volumes

# 크론탭으로 정기 정리
(crontab -l ; echo "0 2 * * * docker system prune -f") | crontab -
```

**데이터베이스 연결 문제**
```bash
# 연결 테스트
docker run --rm --env-file .env your-dockerhub-username/threadly-batch:latest \\
  java -jar app.jar --spring.batch.job.enabled=false --logging.level.org.springframework.jdbc=DEBUG

# 연결 풀 설정 확인
grep -r hikari /home/ubuntu/threadly-batch/config/
```

### 8.2 성능 최적화

**느린 실행 시 확인사항**
```bash
# CPU 사용률 확인
htop

# I/O 대기 확인  
iostat -x 1

# 네트워크 확인
iftop

# gridSize 조정 (더 많은 병렬 처리)
# .env 파일에서 BATCH_GRID_SIZE=8 로 변경
```
