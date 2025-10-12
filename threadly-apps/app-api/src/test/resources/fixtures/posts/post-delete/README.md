# post-delete 테스트 데이터 설명

> 이 파일은 `/post-delete`에 포함된 데이터에 대한 설명 문서입니다.

## 목적

게시글 삭제 기능에 대한 상태별 테스트를 검증하기 위한 데이터 세트

---

## 구성 파일

- `post-users.json`
    - 테스트용 사용자 2명 (`usr1`, `usr2`)
    - 모든 사용자: `isActive = true`, `isEmailVerified = true`
    - 각 사용자마다 userProfile 포함 (`up1`, `up2`)

- `post.json`
    - 다양한 상태를 가진 게시글 4개
        - ACTIVE
        - DELETED
        - BLOCKED
        - ARCHIVE
    - 모든 게시글은 `usr1`이 작성

---

## 실행 순서

1. `post-users.json` → users 및 user_profile 삽입
2. `post.json` → posts 삽입

---

## 샘플 요약

### 사용자 (users + user_profile)

- userId: `usr1`, `usr2`
- userProfileId: `up1`, `up2`
- userType: "USER"
- isActive: true
- isEmailVerified: true

### 게시글 (posts)

| postId       | 상태      | 작성자  | 설명           |
|--------------|---------|------|--------------|
| post_ACTIVE  | ACTIVE  | usr1 | 삭제 가능 상태     |
| post_DELETED | DELETED | usr1 | 이미 삭제된 상태    |
| post_BLOCKED | BLOCKED | usr1 | 차단된 게시글      |
| post_ARCHIVE | ARCHIVE | usr1 | 아카이브 처리된 게시글 |

---

## 테스트용 상수 정의

```java
public static final String POST_OWNER_EMAIL = "sunset_gazer1@threadly.com";
public static final String POST_NON_OWNER_EMAIL = "sky_gazer2@threadly.com";

public static final String POST_ACTIVE_ID = "post_ACTIVE";
public static final String POST_DELETED_ID = "post_DELETED";
public static final String POST_BLOCKED_ID = "post_BLOCKED";
public static final String POST_ARCHIVE_ID = "post_ARCHIVE";

public static final List<String> POST_IDS = List.of(
    POST_ACTIVE_ID,
    POST_DELETED_ID,
    POST_BLOCKED_ID,
    POST_ARCHIVE_ID
);
```