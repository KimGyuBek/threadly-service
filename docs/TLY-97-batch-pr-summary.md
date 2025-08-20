## 개요
- Threadly 서비스의 대용량 데이터 정리를 위한 Spring Batch 시스템 구현
- DELETED/TEMPORARY 상태의 사용자, 게시글, 이미지 데이터 하드 삭제 자동화
- 해시 기반 파티셔닝과 멀티스레딩을 통한 대용량 처리 성능 최적화

## 주요 변경 사항
- **배치 Job 4개 구현**: 사용자/게시글 삭제(파티셔닝), 이미지 삭제(Flow 기반)
- **멀티스레드 처리**: HashPartitioner + TaskExecutor(20 threads)로 병렬 처리
- **성능 최적화**: PostgreSQL 배열 연산, 커서 기반 읽기, 도메인별 청크 사이즈 최적화
- **Flow 기반 구성**: 프로필/게시글 이미지를 순차적으로 처리하는 Flow Job 구현
- **팩토리 패턴 적용**: PostImageDeleteJobFactory, ProfileImageDeleteJobFactory로 코드 재사용
- **모니터링 시스템**: Prometheus 연동, 실시간 성능 메트릭 수집 및 Push Gateway 전송
- **테스트 데이터 유틸리티**: BatchTestDataInsert로 대용량 성능 테스트 데이터 생성

## 고려사항
- **리소스 요구사항**: 최소 2GB heap, 권장 4GB heap (대용량 처리시)
- **DB 인덱스**: status, modified_at 복합 인덱스 필수
- **운영 모니터링**: 메모리/CPU 사용률, 처리율(TPS), DB 연결 풀 상태 추적 필요
- **확장성**: gridSize 파라미터로 파티션 수 조정 가능 (성능 튜닝)