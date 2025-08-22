# Batch Performance Test Report

## 개요
- **테스트 목적**: Threadly 배치 Job 성능 최적화 및 벤치마킹
- **테스트 대상**: User/Post/Image 삭제 배치 Job
- **테스트 환경**: 
  - OS: macOS
  - Java: 21
  - Database: PostgreSQL
  - Memory: 
  - CPU: 

## 테스트 시나리오
1. **Baseline**: 현재 상태로 각 Job 실행
2. **Index Optimization**: 인덱스 추가 후 성능 측정
3. **Multi-threading**: 멀티스레드 적용 후 성능 측정

---

## 1. 데이터 준비

### 테스트 데이터 생성
```bash
# 데이터 삽입 배치 실행
./gradlew :threadly-apps:app-batch:bootRun --args="--spring.profiles.active=default,data-insert"
```

**생성된 데이터:**
- User: `XXX`개 (DELETED 상태)
- Post: `XXX`개 (DELETED 상태)  
- PostImage: `XXX`개 (DELETED/TEMPORARY 상태)
- ProfileImage: `XXX`개 (DELETED/TEMPORARY 상태)

---

## 2. Baseline 성능 테스트

### 2.1 User Hard Delete Job
```bash
./gradlew :threadly-apps:app-batch:bootRun --args="--spring.profiles.active=default --spring.batch.job.name=userHardDeleteDeletedJob"
```

**결과:**
- 실행 시간: `XX분 XX초`
- 처리된 아이템: `XXX`개
- 처리량 (items/sec): `XXX.XX`
- 메모리 사용량: `XXX MB`

### 2.2 Post Hard Delete Job
```bash
./gradlew :threadly-apps:app-batch:bootRun --args="--spring.profiles.active=default --spring.batch.job.name=postHardDeleteDeletedJob"
```

**결과:**
- 실행 시간: `XX분 XX초`
- 처리된 아이템: `XXX`개
- 처리량 (items/sec): `XXX.XX`
- 메모리 사용량: `XXX MB`

### 2.3 Image Hard Delete Jobs
```bash
# DELETED 이미지 삭제
./gradlew :threadly-apps:app-batch:bootRun --args="--spring.profiles.active=default --spring.batch.job.name=imageHardDeleteDeletedJob"

# TEMPORARY 이미지 삭제  
./gradlew :threadly-apps:app-batch:bootRun --args="--spring.profiles.active=default --spring.batch.job.name=imageHardDeleteTemporaryJob"
```

**Image Hard Delete Deleted Job 결과:**
- 실행 시간: `XX분 XX초`
- PostImage 처리: `XXX`개
- ProfileImage 처리: `XXX`개
- 총 처리량 (items/sec): `XXX.XX`

**Image Hard Delete Temporary Job 결과:**
- 실행 시간: `XX분 XX초`
- PostImage 처리: `XXX`개
- ProfileImage 처리: `XXX`개
- 총 처리량 (items/sec): `XXX.XX`

---

## 3. 인덱스 최적화 테스트

### 3.1 추가할 인덱스
```sql
-- User 테이블
CREATE INDEX idx_user_status_modified ON users(user_status_type, modified_at) WHERE user_status_type = 'DELETED';

-- Post 테이블  
CREATE INDEX idx_post_status_modified ON posts(status, modified_at) WHERE status = 'DELETED';

-- PostImage 테이블
CREATE INDEX idx_post_image_status_modified ON post_images(status, modified_at) WHERE status IN ('DELETED', 'TEMPORARY');

-- ProfileImage 테이블
CREATE INDEX idx_profile_image_status_modified ON user_profile_images(status, modified_at) WHERE status IN ('DELETED', 'TEMPORARY');
```

### 3.2 인덱스 적용 후 성능 테스트

#### User Hard Delete Job (with Index)
**결과:**
- 실행 시간: `XX분 XX초` (개선: `±XX%`)
- 처리된 아이템: `XXX`개
- 처리량 (items/sec): `XXX.XX` (개선: `±XX%`)

#### Post Hard Delete Job (with Index)
**결과:**
- 실행 시간: `XX분 XX초` (개선: `±XX%`)
- 처리된 아이템: `XXX`개
- 처리량 (items/sec): `XXX.XX` (개선: `±XX%`)

#### Image Hard Delete Jobs (with Index)
**결과:**
- DELETED Job 실행 시간: `XX분 XX초` (개선: `±XX%`)
- TEMPORARY Job 실행 시간: `XX분 XX초` (개선: `±XX%`)
- 전체 처리량 개선: `±XX%`

---

## 4. 멀티스레드 최적화 테스트

### 4.1 멀티스레드 설정
```java
// Step 설정에 taskExecutor 추가 예정
.taskExecutor(taskExecutor())
.throttleLimit(4) // 동시 스레드 수
```

**설정값:**
- Thread Pool Size: `4`
- Chunk Size: `1000` → `500` (조정 예정)

### 4.2 멀티스레드 적용 후 성능 테스트

#### User Hard Delete Job (Multi-thread)
**결과:**
- 실행 시간: `XX분 XX초` (개선: `±XX%`)
- 처리된 아이템: `XXX`개
- 처리량 (items/sec): `XXX.XX` (개선: `±XX%`)
- CPU 사용률: `XX%`

#### Post Hard Delete Job (Multi-thread)
**결과:**
- 실행 시간: `XX분 XX초` (개선: `±XX%`)
- 처리된 아이템: `XXX`개
- 처리량 (items/sec): `XXX.XX` (개선: `±XX%`)
- CPU 사용률: `XX%`

#### Image Hard Delete Jobs (Multi-thread)
**결과:**
- DELETED Job 실행 시간: `XX분 XX초` (개선: `±XX%`)
- TEMPORARY Job 실행 시간: `XX분 XX초` (개선: `±XX%`)
- 전체 처리량 개선: `±XX%`

---

## 5. 성능 비교 요약

| Job | Baseline | + Index | + Multi-thread | 최종 개선율 |
|-----|----------|---------|----------------|-------------|
| User Delete | XX.XX items/sec | XX.XX items/sec | XX.XX items/sec | ±XX% |
| Post Delete | XX.XX items/sec | XX.XX items/sec | XX.XX items/sec | ±XX% |
| Image Delete (DELETED) | XX.XX items/sec | XX.XX items/sec | XX.XX items/sec | ±XX% |
| Image Delete (TEMPORARY) | XX.XX items/sec | XX.XX items/sec | XX.XX items/sec | ±XX% |

---

## 6. 리소스 사용량 분석

### 6.1 메모리 사용량
| 최적화 단계 | Heap Memory | Off-Heap Memory | GC 빈도 |
|-------------|-------------|-----------------|---------|
| Baseline | XXX MB | XXX MB | XX회/분 |
| + Index | XXX MB | XXX MB | XX회/분 |
| + Multi-thread | XXX MB | XXX MB | XX회/분 |

### 6.2 CPU 사용률
| 최적화 단계 | Average CPU | Peak CPU | Load Average |
|-------------|-------------|----------|--------------|
| Baseline | XX% | XX% | X.XX |
| + Index | XX% | XX% | X.XX |
| + Multi-thread | XX% | XX% | X.XX |

### 6.3 Database Connection
| 최적화 단계 | Active Connections | Max Connections | Connection Pool |
|-------------|-------------------|-----------------|-----------------|
| Baseline | XX | XX | XX |
| + Index | XX | XX | XX |
| + Multi-thread | XX | XX | XX |

---

## 7. 결론 및 권장사항

### 7.1 성능 개선 효과
1. **인덱스 추가**: 전체 `XX%` 성능 향상
2. **멀티스레드**: 전체 `XX%` 성능 향상
3. **종합 개선율**: `XX%` 성능 향상

### 7.2 병목 지점 분석
- **Database I/O**: 
- **Memory Usage**: 
- **CPU Utilization**: 

### 7.3 추가 최적화 방안
- [ ] Chunk Size 튜닝 (현재: 1000 → 권장: XXX)
- [ ] Connection Pool 설정 최적화
- [ ] JVM Heap Size 조정
- [ ] Batch Size 조정 (JPA)

### 7.4 운영 환경 적용 고려사항
- **Peak Time 회피**: 새벽 시간대 실행 권장
- **모니터링**: Grafana 대시보드로 실시간 모니터링
- **알람 설정**: 처리량 저하 시 알람
- **백업 정책**: 삭제 전 백업 고려

---

## 8. 테스트 로그 샘플

### Baseline 로그
```json
{"jobName":"userHardDeleteDeletedJob","executionTime":"00h 05m 23s 456ms","totalItemsDeleted":15000,"throughputItemsPerSec":46.23}
```

### 최적화 후 로그
```json
{"jobName":"userHardDeleteDeletedJob","executionTime":"00h 02m 15s 123ms","totalItemsDeleted":15000,"throughputItemsPerSec":111.11}
```

---

## 9. 참고사항

### 테스트 명령어 모음
```bash
# 데이터 삽입
./gradlew :threadly-apps:app-batch:bootRun --args="--spring.profiles.active=default,data-insert"

# User 삭제 Job
./gradlew :threadly-apps:app-batch:bootRun --args="--spring.profiles.active=default --spring.batch.job.name=userHardDeleteDeletedJob"

# Post 삭제 Job  
./gradlew :threadly-apps:app-batch:bootRun --args="--spring.profiles.active=default --spring.batch.job.name=postHardDeleteDeletedJob"

# Image 삭제 Jobs
./gradlew :threadly-apps:app-batch:bootRun --args="--spring.profiles.active=default --spring.batch.job.name=imageHardDeleteDeletedJob"
./gradlew :threadly-apps:app-batch:bootRun --args="--spring.profiles.active=default --spring.batch.job.name=imageHardDeleteTemporaryJob"
```

### 모니터링 포인트
- 로그 파일: `logs/batch/batch-jobs.log`
- 메트릭: `throughputItemsPerSec`, `executionTime`, `totalItemsDeleted`
- 시스템 리소스: CPU, Memory, Disk I/O, Network

**테스트 실행일**: `YYYY-MM-DD`  
**테스트 환경**: `개발/로컬`  
**담당자**: `XXX`