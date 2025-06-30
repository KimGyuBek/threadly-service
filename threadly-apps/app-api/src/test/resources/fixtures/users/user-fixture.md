# user-fixture.json 설명

> 이 파일은 `user-fixture.json`에 포함된 사용자 데이터에 대한 설명 문서입니다.

## 상태: `EMAIL_VERIFIED`

- 총 사용자 수: 100명
- 사용 목적: 기본 사용자 목록 테스트용 (모두 이메일 인증 완료)
- ID 범위: `usr1` ~ `usr100`

## 샘플 데이터 목록 (5건 예시)

| userId | email                         | isEmailVerified | isActive |
|--------|-------------------------------|------------------|----------|
| usr1   | sunset_gazer1@threadly.com    | true             | true     |
| usr2   | sky_gazer2@threadly.com       | true             | true     |
| usr3   | book_worm3@threadly.com       | true             | true     |
| usr4   | beach_bum4@threadly.com       | true             | true     |

※ 전체 데이터는 `user-fixture.json` 파일 참조

## 관련 상수

```java
public static final Map<String, String> USER_FIXTURE_SAMPLE_1 = Map.of(
    "userId", "usr1",
    "email", "sunset_gazer1@threadly.com"
);
public static final Map<String, String> USER_FIXTURE_SAMPLE_2 = Map.of(
    "userId", "usr2",
    "email", "sky_gazer2@threadly.com"
);
public static final Map<String, String> USER_FIXTURE_SAMPLE_3 = Map.of(
    "userId", "usr3",
    "email", "book_worm3@threadly.com"
);
public static final Map<String, String> USER_FIXTURE_SAMPLE_4 = Map.of(
    "userId", "usr4",
    "email", "beach_bum4@threadly.com"
);
```