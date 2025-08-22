# Flyway CONCURRENTLY 인덱스 생성 문제 해결 가이드

## 문제 상황

### 증상
- Flyway 마이그레이션이 CONCURRENTLY 인덱스 생성 중에 무한 대기
- `[non-transactional]` 로그 표시 후 응답 없음
- PostgreSQL 연결이 활성 상태로 유지되나 진행되지 않음

### 오류 메시지 예시
```
2025-08-22 03:12:46.723 [main] INFO  o.f.core.internal.command.DbMigrate - Migrating schema "public" to version "23 - add indexes for user foreign keys" [non-transactional]
```

## 근본 원인

### PostgreSQL CONCURRENTLY 인덱스의 특성
CONCURRENTLY 인덱스 생성은 다음 단계로 진행됩니다:
1. 인덱스 카탈로그 등록
2. 기존 데이터 스캔 및 인덱스 구축 (시간 소요)
3. **모든 활성 트랜잭션 완료 대기** (무한 대기 가능)

### 대기 발생 원인
- 오래된 `idle in transaction` 세션 존재
- 긴 실행 시간의 분석 쿼리 실행 중
- 애플리케이션 연결 풀의 트랜잭션 누수
- 높은 디스크 I/O 부하

## 진단 방법

### 1. 현재 진행 상황 확인
```sql
-- 인덱스 생성 프로세스 확인
SELECT 
    pid,
    now() - query_start AS duration,
    state,
    query
FROM pg_stat_activity 
WHERE query LIKE '%CREATE INDEX CONCURRENTLY%';
```

### 2. 활성 트랜잭션 확인
```sql
-- 오래된 활성 트랜잭션 조회
SELECT 
    pid,
    usename,
    state,
    now() - xact_start AS xact_duration,
    query
FROM pg_stat_activity 
WHERE state IN ('active', 'idle in transaction')
  AND xact_start IS NOT NULL
ORDER BY xact_start;
```

## 해결 방법

### 즉시 대응
```sql
-- 오래된 idle 트랜잭션 종료
SELECT pg_terminate_backend(pid) 
FROM pg_stat_activity 
WHERE state = 'idle in transaction' 
  AND now() - state_change > interval '30 minutes';

-- 인덱스 생성 중단
SELECT pg_cancel_backend(pid)
FROM pg_stat_activity 
WHERE query LIKE '%CREATE INDEX CONCURRENTLY%';

-- 실패한 인덱스 정리
DROP INDEX CONCURRENTLY IF EXISTS idx_posts_user_id;
```

## 결론: 인덱스 별도 관리

### 실무 권장 방안
CONCURRENTLY 인덱스는 Flyway와 분리하여 별도 SQL 스크립트로 수동 관리합니다.

### 구현 방법

**1. Flyway에서 인덱스 제거**
- V23, V25 파일에서 인덱스 관련 SQL 제거
- 순수 스키마 변경만 Flyway로 관리

**2. 별도 인덱스 스크립트 생성**
```
scripts/
└── indexes/
    ├── v23_user_foreign_key_indexes.sql
    ├── v25_batch_performance_indexes.sql
    └── run_indexes.sh
```

**3. 수동 실행 절차**
```bash
# 1. Flyway 마이그레이션 (스키마 변경)
./gradlew flywayMigrate

# 2. 인덱스 생성 (수동 실행)
psql -h $DB_HOST -U $DB_USER -d $DB_NAME -f scripts/sql/indexes/user_indexes.sql
```

### 장점
- Flyway 마이그레이션 안정성 확보
- 인덱스 생성 중 문제 발생 시 독립적 재시도 가능
- 트래픽 저조 시간대에 선택적 실행
- 대용량 테이블에서 타임아웃 없이 처리

### 실무에서의 일반적 관행
- 스키마 변경: Flyway 자동화
- 인덱스/파티셔닝: DBA 수동 관리
- 성능 크리티컬 작업: 점검시간 활용

이 방식은 많은 기업에서 채택하고 있는 안정적이고 검증된 접근 방법입니다.