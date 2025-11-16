# threadly-adapters

인프라 계층 (Infrastructure Layer)

```
threadly-adapters/
├── adapter-persistence/   # 데이터베이스 영속성 (JPA, PostgreSQL)
├── adapter-redis/         # 캐시 및 세션 관리
├── adapter-storage/       # 파일 저장소 처리
└── adapter-kafka/         # 메시지 큐 이벤트 처리
```

## 설명

- **adapter-persistence**: JPA 기반 PostgreSQL 데이터 영속화, Flyway 마이그레이션
- **adapter-redis**: Redis 기반 캐싱 및 세션 스토리지
- **adapter-storage**: 이미지/파일 업로드 및 저장소 관리
- **adapter-kafka**: Kafka를 통한 비동기 이벤트 발행 및 구독
