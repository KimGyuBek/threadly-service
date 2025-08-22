# Spring Batch 사용자 하드 딜리트 성능 튜닝 & 트러블슈팅 리포트

작성일: 2025-08-16 07:26:03
대상 잡: **`userHardDeleteDeletedJob`** (Spring Batch / PostgreSQL)

---

## 1. 배경

- 목적: **소프트 삭제된(`DELETED`) 사용자 레코드**를 보존 기간(retention threshold) 이후 **하드 딜리트**.
- 구성:
    - Spring Batch (Job → Step → Chunk 기반)
    - 데이터베이스: PostgreSQL
    - ORM/접근: JPA(Hibernate), JDBC 혼용
    - 커넥션 풀: HikariCP
- 관련 잡: 이미지 하드 딜리트(게시글/프로필), 이미지 temporary 하드 딜리트, 사용자 하드 딜리트, 게시글 하드 딜리트
- 과제: 같은 데이터량 기준으로 **`userHardDeleteDeletedJob` 성능 급저하** 및 **데드락/락 경합** 발생 원인 파악 및 개선

---

## 2. 문제 증상 요약

1) **DELETE 지연 및 잡 지연/실패**
    - 동일 조건/동일 로직임에도 특정 시점부터 **사용자 하드 딜리트**만 **급격한 성능 저하**.
    - 일부 실행은 **데드락**(`deadlock detected`) 또는 **락 경합**으로 실패.
    - 스텝 내부 멀티스레딩 적용 시 **ResultSet 닫힘 에러**로 즉시 실패.

2) **동시 실행 충돌**
    - 서로 독립적인 배치들을 **동시에** 실행할 경우, **락 경합/데드락** 발생.

3) **Cascade 삭제의 부작용**
    - users에 CASCADE를 붙여 속도는 개선됐으나, **도메인별 다른 retention 전략**과 충돌(의도치 않은 연관 데이터의 조기 삭제).

---

## 3. 핵심 로그 & 재현 결과

아래는 주요 실행별 요약(사용자 제공 로그 기반).

### 3.1 베이스라인(싱글 스레드)

- **실행:** `----default`
- **결과:** 10,000,000건 / **00:05:40.770** / **29,345 rec/s** / **COMPLETED**
- 특이사항: chunk = 1000, commitCount ≈ 10,001

### 3.2 스텝 내부 멀티스레드 시도 → 실패

- **실행:** `----multi thread`
- **결과:** 1,000건 읽음 후 **FAILED**
- **에러:** `This ResultSet is closed.`
- 원인: **Cursor 기반 리더(JpaCursorItemReader)** 를 멀티스레드로 돌려 **커서 조기 종료**

### 3.3 (초기) 파티셔닝 적용 but 느려짐

- **실행:** 10,000,000건 / **00:07:26.153** / **22,413 rec/s** / **COMPLETED**
- 원인 후보:
    - **이중 병렬**(마스터 파티셔닝 + 워커 내부 taskExecutor 동시 사용)
    - **문자열 `BETWEEN` 범위 분할의 불균등(롱테일)**
    - **풀 크기/그리드 불일치**로 인한 대기/컨텍스트 스위칭 오버헤드

### 3.4 부분 데이터(2.504M) 파티셔닝(grid=4)

- **실행:** **00:00:41.190** / **2,504,000건** / **60,791 rec/s** / **COMPLETED**
- 시사점: **gridSize가 풀/CPU와 맞으면** 선형에 가까운 개선 가능

### 3.5 gridSize=20 → 풀 고갈로 실패 (Hikari)

- **실행:** `status: FAILED`
- **에러:**
  `Connection is not available, request timed out ... total=10, active=10, idle=0, waiting=13`
- 원인: **gridSize(20) > maxPoolSize(10)** → 커넥션 대기행렬/타임아웃

### 3.6 gridSize=20 → JPA EntityManager 오픈 실패

- **실행:** `status: FAILED`
- **에러:** `Could not open JPA EntityManager for transaction`
- 원인: 상동(풀/리소스 고갈)

### 3.7 최종: **해시(샤드) 파티셔닝** 성공(g=20)

- **실행:** `---shard partitioning`
- **결과:** **20,000,000건 / 00:01:01.701 / 324,144 rec/s / COMPLETED**
- 파티션별 균등도: 각 파티션 ≈ 500k, 커밋 ≈ 500 (chunk=1000) → **매우 균등**

> 참고: 로컬 맥북 프로 환경의 고성능(코어/메모리/스토리지) 특성이 기여. 운영에서는 네트워크/스토리지 차이로 TPS 하락 가능.

---

## 4. 원인 분석 (문제 → 원인 매핑)

| 문제             | 직접 원인                     | 근본 원인                         |
|----------------|---------------------------|-------------------------------|
| DELETE 지연/데드락  | FK/인덱스 경합, 동일 범위 중복 접근    | 동시성 제어 부재, 파티셔닝 불균형           |
| 스텝 내부 멀티스레드 실패 | `ResultSet is closed`     | Cursor 기반 리더의 비스레드세이프         |
| 파티셔닝했는데도 느림    | 롱테일 파티션, 이중 병렬, 풀 부족      | 범위 분할의 데이터 분포 미스매치, 동시성 설정 미스 |
| grid=20 실패     | Hikari 풀 고갈               | gridSize > maxPoolSize        |
| CASCADE 부작용    | 연쇄 삭제가 도메인별 retention과 충돌 | 삭제 순서/잡 설계의 정책 미스             |
| 문자열 ID 범위 분할   | 사전식 정렬로 불균등               | 키 분포 모를 때 범위 분할은 위험           |

---

## 5. 해결책 설계

### 5.1 동시성 모델 교정

- **병렬은 오직 마스터 파티셔닝에서만** 수행
- 워커 스텝에서는 **`taskExecutor` 제거**(이중 병렬 금지)
- `gridSize ≤ Hikari.maxPoolSize - 2` 유지

### 5.2 파티셔닝 전략 전환: **해시(샤드) 분할**

- 조건 집합(예: `status='DELETED' AND modified_at < :threshold`) 위에 **균등한 해시 분할** 추가
- PostgreSQL:
  ```sql
  ... WHERE user_status_type='DELETED'
        AND modified_at < :threshold
        AND mod(abs(hashtext(user_id)), :N) = :shard
  ORDER BY user_id
  ```
- 장점: **겹침 0, 균등 분할**, 롱테일 제거 → 락 경합/데드락 완화

### 5.3 리더/라이터 경량화

- **리더**: 가능하면 **JDBC Paging**으로 **ID만** 읽기 (`pageSize == chunk`)  
  JPA 필요 시 `transacted(false)` + projection
- **라이터**: **집합 DELETE**
    - `IN (:ids)` 또는 대량 시 **`user_id = ANY(?::text[])`** (배열 바인딩)

### 5.4 커밋/청크 튜닝

- 목표: **1 커밋 0.5~2초**
- 2초 초과 → chunk 줄이기 / 0.2초 미만 → chunk 키우기
- `pageSize == chunk` 유지

### 5.5 Hikari 풀 튜닝 (배치 기준)

- `maximumPoolSize = gridSize + 2~4`
- `minimumIdle = 0` (배치 특성상 유휴 최소화)
- `connectionTimeout = 3~10s`
- `validationTimeout = 1s`
- `idleTimeout = 5~10m`
- `maxLifetime` = (LB/DB idle timeout)보다 30~60초 짧게
- `keepaliveTime = 2~5m` (중간 장비가 idle 끊는 환경)

**YAML 예시**

```yaml
spring:
  datasource:
    url: jdbc:postgresql://db:5432/app?stringtype=unspecified&targetServerType=primary
    username: app
    password: ***
    hikari:
      maximum-pool-size: 24
      minimum-idle: 0
      connection-timeout: 5000
      validation-timeout: 1000
      idle-timeout: 300000
      max-lifetime: 1700000
      keepalive-time: 120000
      auto-commit: false
      pool-name: HikariPool-batch
```

### 5.6 잡 순서/정책

- **사용자 하드 딜리트는 최후**에 실행 (연관 도메인들이 먼저 정리되도록)
- CASCADE로 속도 올리기 대신, **잡 순서 + 조건**으로 의도 통제

### 5.7 대안 전략(선택)

멀티 스레드 사용시 시간이 너무 길어짐.
파티셔닝 적용 -> userId는 nanoId로 생성된 문자열임
gridSize로 minId, maxId로 분할하려고 했지만 균등하게 분할이 되지 않음 -> 작동시간이 너무 길어짐


---

## 6. 최종 구조 (샤드 파티셔닝; 요지)

밑에는 그냥 예시야 내 코드 참고해서 분석해

**Partitioner**

```java
public class HashPartitioner implements Partitioner {

  public Map<String, ExecutionContext> partition(int gridSize) {
    Map<String, ExecutionContext> map = new LinkedHashMap<>();
    for (int shard = 0; shard < gridSize; shard++) {
      ExecutionContext ctx = new ExecutionContext();
      ctx.putInt("shard", shard);
      ctx.putInt("gridSize", gridSize);
      map.put("part-" + shard, ctx);
    }
    return map;
  }
}
```

**Worker Step (싱글스레드)**

```java
return new StepBuilder("userShardWorkerStep",repo)
    .

<String, String> chunk(1000,tx)
    .

reader(userIdReaderByShard)   // JDBC/JPA projection
    .

writer(userDeleteWriterAny)   // 집합 삭제
    .

allowStartIfComplete(true)
    .

build(); // taskExecutor 없음
```

**Reader (JDBC Paging; 해시 조건)**

```sql
SELECT user_id
FROM users
WHERE user_status_type = :status
  AND modified_at < :threshold
  AND mod(abs(hashtext(user_id)), :N) = :shard
ORDER BY user_id LIMIT :page
OFFSET :offset
```

**Writer (배열 바인딩)**

```java
DELETE FROM
users
WHERE user_id = ANY( ?)
AND user_status_type = ?
AND modified_at < ?
```

---

## 7. 수학적 근거(왜 해시 분할이 균등한가)

- 모델: 키를 무작위 해시에 넣어 N개 샤드로 모듈러. 샤드별 건수 X_j는 Binomial(n, 1/N) 근사.
- 기대/분산: E[X_j]=n/N, Var(X_j)≈n/N.
- 체르노프 경계: `Pr[|X_j-μ|≥δμ] ≤ 2·exp(-μ·δ²/3)` ⇒ 샤드당 기대건수 μ가 클수록 상대오차 O(1/√μ).
- 실무: NanoID/UUID 등 랜덤 키 + 적절한 해시이면 **샤드당 거의 균등**.
- 쿼리

```sql
select mod(abs(hashtext(user_id)), :N) as shard, count(*)
from users
where status = 'DELETED'
group by shard
ORDER BY shard;
```
실행시
shard,count
0,500090
1,498596
2,499958
3,500512
4,499457
5,499781
6,498702
7,500049
8,499407
9,498886
10,500075
11,500141
12,500917
13,499972
14,501307
15,499978
16,500212
17,500832
18,500319
19,500809

균등하게 분포됨 그거를 증먕헤줘

-

---

## 8. 성능 결과 비교(핵심만)

- /docs/performance-testing/performance.md 분석

---

## 9. 체크리스트(운영 투입 전)

- [ ] `gridSize ≤ Hikari.maxPoolSize - 2` (ex. grid=20 → pool ≥ 22~24)
- [ ] 워커 스텝에 `taskExecutor` 제거(이중 병렬 금지)
- [ ] `pageSize = chunk` 설정, **커밋 0.5~2초** 튜닝
- [ ] 리더는 **ID 중심**(JDBC 권장), 라이터는 **집합 DELETE**
- [ ] 인덱스: `(user_status_type, modified_at)` + PK(`user_id`)
- [ ] 배치 세션: `SET LOCAL synchronous_commit=off;`
- [ ] 파티션별 처리량/시간, 풀 대기, `pg_stat_statements`, `pg_locks` 모니터링
- [ ] 배치 시간대와 다른 쓰기 워크로드 분리

---

## 10. 교훈(요약)

1) **병렬은 단순하게**: 마스터 파티셔닝만, 워커는 싱글스레드.
2) **문자열 키 분할은 해시로**: 범위(BETWEEN)는 롱테일을 부른다.
3) **풀/그리드 정렬이 생명**: grid > pool이면 성능이 아닌 장애다.
4) **ID만 읽고 집합 삭제**: JPA 전체 엔티티 로딩은 대량 작업에 독.
5) **정책 > CASCADE**: 잡 순서/조건으로 도메인 보존정책을 지켜라.
6) **숫자로 피드백 루프**: 커밋 시간, 파티션별 시간, 풀 대기, DB 잠금으로 즉시 조정.

---

## 부록 C. 파티션 균등도 점검 SQL

```sql
SELECT mod(abs(hashtext(user_id)), :N) AS shard, count(*)
FROM users
WHERE user_status_type = 'DELETED'
  AND modified_at < :th
GROUP BY shard
ORDER BY shard;
```

# 중요한거

/docs/performance-testing/performance.md 분석 후 최종적으로 성능이 몇프로, 얼마나 상승했는지 분석해줘