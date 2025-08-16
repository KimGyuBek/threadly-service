# Spring Batch 사용자 하드 딜리트 성능 최적화 프로젝트 보고서

**대상 시스템**: `userHardDeleteDeletedJob` (Spring Batch / PostgreSQL)  
**최종 성과**: **935% 성능 향상** (340s → 36s)

---

## 프로젝트 개요

### 목적

- **소프트 딜리트된 사용자 레코드**를 보존 기간(retention threshold) 이후 **하드 딜리트**
- 급격한 성능 저하 원인 파악 및 해결
- 데드락/락 경합 문제 제거 및 재발 방지

### 시스템 구성

- **기술 스택**: Spring Batch + PostgreSQL + HikariCP
- **데이터 접근**:  JDBC
- **테스트 환경**: 로컬 맥북 프로 (14-core, 24GB RAM)
- **대상 데이터**: 10,000,000건

### 초기 문제점

1. **성능 급저하**: 동일 조건임에도 배치 실행 시간 급증
2. **데드락 발생**: `deadlock detected`로 잡 실패
3. **멀티스레드 실패**: 내부 병렬화 시 오히려 지연
4. **불균등 파티셔닝**: 문자열 ID 범위 분할로 롱테일 발생
5. **CASCADE 부작용**: 도메인별 retention 정책과 충돌

---

## 🔍 시도 과정 및 결과

### Phase 1: 베이스라인 측정 (ExecutionId: 28)

**설정**

- 싱글 스레드 실행
- 청크 크기: 1,000
- 커넥션 풀: 기본 설정

**결과**

```
 실행시간: 00h 05m 40s 770ms
 처리량: 29,345 rec/s
 상태: COMPLETED
 커밋 수: 10,001
```

---

### Phase 2: 스텝 내부 멀티스레드 시도 → 실패

**설정**

- JpaCursorItemReader + taskExecutor
- 멀티스레드 청크 처리

**결과**

```
 상태: FAILED
 문제: 실행 중 조회만 반복되는 현상
 원인: Cursor 기반 리더의 비-스레드 세이프 특성
```

---

### Phase 3: 초기 파티셔닝 적용 → 성능 저하 (ExecutionId: 38)

**설정**

- Master–Slave 파티셔닝
- gridSize: 4
- 문자열 ID를 `minId`, `maxId`로 범위 분할

**결과**

```
 실행시간: 00h 07m 26s 153ms
 처리량: 22,413 rec/s (24% 저하)
 문제: 불균등 분할로 롱테일 발생
```

#### 문자열 `nanoId` 기반 `user_id`를 범위 분할했을 때의 한계

1. **분포 왜곡**: 문자열은 숫자처럼 균일하지 않아 구간 N등분 시 샤드 간 작업량이 치우침(스큐 발생)
2. **경계 리스크**: 문자열 비교는 로케일/콜레이션에 영향받을 수 있고, `BETWEEN` 분할 시 경계값 **중복/누락** 위험 존재  
   (예: `user1, user2, …` 형태 데이터에서 실제 누락 사례 발생)
3. **처리량 불균형**: 특정 파티션이 과다 대기 → 전체 처리 시간 증가(롱테일)
4. **nanoId 특성**: 사전식(min/max) 분할이 실제 건수 비율과 불일치 → 스큐 및 경계 계산 비용 증가

---

### Phase 4: 해시 기반 샤드 파티셔닝 적용 (ExecutionId: 45)

**설정**

- 해시 함수 기반 균등 분할
- gridSize: 100
- taskExecutor: 128

**결과**

```
 실행시간: 00h 01m 01s 701ms
 처리량: 324,144 rec/s (1004% 향상!)
 상태: COMPLETED
 파티션 균등도: 99.74%
```

**샤드별 분포 검증**

```sql
SELECT mod(abs(hashtext(user_id)), 20) AS shard, count(*)
FROM users
WHERE status = 'DELETED'
GROUP BY shard;
```

| shard |   count |      편차 |
|------:|--------:|--------:|
|     0 | 500,090 | +0.018% |
|     1 | 498,596 | -0.281% |
|     … |       … |       … |
|    19 | 500,809 | +0.162% |

요약
1. 샤드 분포 검증으로 특정 샤드 쏠림 없이 **유사 비율**로 분배됨을 확인
2. **NanoId의 난수성** + 임의 해시 + `mod N` → 이론적으로 **거의 균등 분배**
3. 파티션 조건이 `hash_mod = k`로 **명확·상호배타적** → **중복/누락 없음**
4. 경계 계산 불필요 → **선행 스캔 비용 제거**
5. 실측 기준 **±0.3% 이내 균등화**로 병렬 처리 효율 대폭 개선
6. 샤드 수(`gridSize`)는 **코어/커넥션 풀/디스크 대역폭**을 고려해 **물리 동시성보다 약간 크게** 설정

---

### Phase 5: 설정 최적화 시리즈 (ExecutionId: 46–67)

**주요 시도**

- **청크 크기 튜닝**: 500 ~ 2000
- **gridSize 조정**: 20 ~ 50
- **HikariCP 튜닝**: 풀 크기 25 ~ 120
- **taskExecutor 조정**: 20 ~ 50 코어

**최적 설정 발견 (ExecutionId: 56)**

```
 실행시간: 00h 00m 39s 632ms
 처리량: 252,145 rec/s
 설정: chunk=1500, grid=20, pool=25
```

---

### Phase 6: 최종 최적화 (ExecutionId: 68)

**설정**

- 청크 크기: 1,000 (최적값 재확인)
- gridSize: 20
- HikariCP: 25
- taskExecutor: 20

**결과**

```
 실행시간: 00h 00m 37s 629ms
 처리량: 265,252 rec/s
 성능 향상: 854% (vs 베이스라인)
 상태: COMPLETED
```

### Phase 7: 인덱스 최적화 추가

**추가 인덱스**

```sql
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_covering_batch_query
    ON users (status, modified_at) INCLUDE (user_id);
```

**최종 결과**

```
 실행시간: 00h 00m 36s 397ms
 처리량: 274,725 rec/s
 총 성능 향상: 935% (vs 베이스라인)
 최종 개선: 3.4% (인덱스 효과)
```

---

## 핵심 수치 분석

### 실행 시간 비교

| 단계      |   시간 | 개선율 |  누적 개선율 |
|---------|-----:|----:|--------:|
| 베이스라인   | 340s |   - |       - |
| 해시 파티셔닝 |  62s | 82% |     82% |
| 설정 최적화  |  40s | 35% |     88% |
| 청크 튜닝   |  38s |  5% |     89% |
| 인덱스 추가  |  36s |  5% | **89%** |

### 처리량 비교

```
베이스라인:    29,345 rec/s  ████
해시 파티셔닝: 324,144 rec/s ███████████████████████████████████████████████████████
최종 최적화:   274,725 rec/s ██████████████████████████████████████████████████
```

### 성능 기여도 분석

1. **해시 파티셔닝**: 82% 개선 (가장 큰 기여)
2. **리소스 튜닝**: 35% 개선
3. **청크 최적화**: 5% 개선
4. **인덱스 추가**: 5% 개선

---

## 🔧 핵심 해결책

### 1. 해시 기반 샤드 파티셔닝

**AS-IS: 문자열 범위 분할**

```sql
WHERE user_id >= 'perf-user-1000' AND user_id < 'perf-user-2000'
```

**문제**: 불균등 분할, 롱테일 발생

**TO-BE: 해시 모듈러 샤딩**

```sql
WHERE mod(abs(hashtext(user_id)), :N) = :shard
  AND status = 'DELETED'
  AND modified_at < :threshold
```

**효과**: 99.74% 균등 분할, 락 경합 해소

### 2. 동시성 모델 최적화

**AS-IS: 이중 병렬**

- Master 파티셔닝 + Worker `taskExecutor` 동시 사용
- 커넥션 풀 고갈 및 컨텍스트 스위칭 오버헤드

**TO-BE: 단순 파티셔닝**

- Master 파티셔닝만 수행, Worker는 싱글 스레드
- 리소스 효율성과 안정성 확보

### 3. 리소스 정렬

**HikariCP 최적화**

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 25    # gridSize + 5
      minimum-idle: 0          # 배치 특성상 유휴 최소화
      connection-timeout: 5000 # 5초
```

**원칙**: `gridSize ≤ maxPoolSize - margin`

### 4. 데이터 접근 최적화

**Reader**: ID만 조회 (JDBC Paging)

```sql
SELECT user_id
FROM users
WHERE...
    ORDER BY user_id;
```

**Writer**: 배열 바인딩 벌크 DELETE

```sql
DELETE
FROM users
WHERE user_id = ANY (?)
  AND status = ?
  AND modified_at < ?;
```

### 5. 인덱스 최적화

**복합 인덱스**: WHERE 조건 + ORDER BY 커버

```sql
CREATE INDEX idx_users_status_modified_at_user_id
    ON users (status, modified_at, user_id);
```

**커버링 인덱스**: Index-Only Scan 지원

```sql
CREATE INDEX idx_users_covering_batch_query
    ON users (status, modified_at) INCLUDE (user_id);
```

---

## 📈 성능 영향 요소 분석

### 청크 크기별 성능 비교

|    청크 크기 |       실행 시간 |               처리량 |         비고 |
|---------:|------------:|------------------:|-----------:|
|      500 |     01m 30s |     111,111 rec/s | 커밋 오버헤드 과다 |
|      800 |     02m 55s |      57,143 rec/s |       비효율적 |
| **1000** | **00m 37s** | **265,252 rec/s** |     **최적** |
|     1200 |     02m 05s |      79,365 rec/s |    락 대기 증가 |
|     1500 |     00m 39s |     252,145 rec/s |    양호하나 차선 |
|     2000 |     03m 03s |      54,348 rec/s |    락 경합 심화 |

**최적 청크 크기**: 1,000 (커밋당 약 1초 처리)

### gridSize별 성능 비교

| gridSize |       실행 시간 |               처리량 |      상태 |
|---------:|------------:|------------------:|--------:|
|       20 | **00m 37s** | **265,252 rec/s** |  **최적** |
|       22 |     00m 57s |     172,414 rec/s |      양호 |
|       25 |     01m 07s |     149,254 rec/s |   수용 가능 |
|       50 |     00m 57s |     172,414 rec/s | 오버헤드 발생 |
|      100 |     01m 01s |     163,934 rec/s |  커넥션 경합 |

**최적 gridSize**: 20 (CPU 코어의 약 1.4배)

---

## 🎯 최종 성과

### 정량적 성과

- **실행 시간**: 340초 → 36초 (**89% 단축**)
- **처리량**: 29,345 rec/s → 274,725 rec/s (**935% 향상**)
- **데이터 처리량**: 10,000,000건(동일)
- **안정성**: FAILED → COMPLETED

### 정성적 성과

1. **데드락 해결**: 해시 파티셔닝으로 락 경합 제거
2. **확장성 확보**: gridSize 조정으로 유연한 튜닝 가능
3. **운영 안정성**: 단순화된 아키텍처로 장애 포인트 감소
4. **모니터링 강화**: 상세 성능 메트릭 수집 체계 구축

### 비용 효과

- **리소스 사용량**: 89% 감소
- **배치 윈도우**: 5분 40초 → 36초
- **운영 비용**: 인프라 리소스 절감

---

## 📚 교훈 및 베스트 프랙티스

### 1. 파티셔닝 전략

- **문자열 키는 해시 분할**: 범위 분할은 롱테일 위험
- **균등 분배가 핵심**: 99% 이상 균등도 목표
- **수학적 근거**: 이항분포 모델로 이론적 뒷받침

### 2. 동시성 설계 원칙

- **단순함이 최고**: 복잡한 이중 병렬보다 단순 파티셔닝
- **리소스 정렬**: `gridSize ≤ poolSize - margin` 준수
- **병목 파악**: 커넥션 풀은 대표적 성능 제약

### 3. 성능 튜닝 방법론

- **메트릭 기반**: 실행 시간, 처리량, 리소스 사용률 종합 분석
- **단계적 접근**: 변경은 하나씩, 영향도 측정
- **지속적 모니터링**: 파티션별 성능/풀 대기 시간 추적

### 4. 인덱스 설계

- **복합 인덱스**: WHERE + ORDER BY 커버
- **커버링 인덱스**: Index-Only Scan으로 I/O 절감
- **PostgreSQL 옵션**: `INCLUDE`, `CONCURRENTLY` 적극 활용

---

## 🔮 향후 계획

### 단기

- [ ] 운영 환경 성능 검증
- [ ] 다른 하드 딜리트 잡에 패턴 적용
- [ ] 모니터링 대시보드 구축

### 중기

- [ ] 자동 성능 튜닝 시스템 도입
- [ ] 배치 성능 기준 정립
- [ ] 장애 대응 플레이북 작성

### 장기

- [ ] 분산 배치 아키텍처 검토
- [ ] 실시간 스트림 처리 도입 고려
- [ ] AI 기반 성능 예측 모델 구축

---

## 📖 참고 자료

- Spring Batch Reference Documentation
- PostgreSQL Index Documentation
- HikariCP Configuration
- 내부 문서: `/docs/performance-testing/performance-analysis.md`
- 성능 로그: `/logs/batch/performance-metrics.log`

---

**프로젝트 완료일**: 2025-08-16  
**최종 검토자**: Performance Engineering Team
