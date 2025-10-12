# /post-like 설명

> 이 파일은 게시글 좋아요 API 테스트를 위한 데이터 설명 문서입니다.

---

## 구성 파일

- `users.json`  
  → 테스트용 사용자 7명 (`usr_writer`, `usr1` ~ `usr6`)  
  → 모든 사용자에 userProfile 포함됨  
  → isActive=true, isEmailVerified=true

- `post.json`  
  → 테스트 대상 게시글 4개  
  → 작성자: `usr_writer`  
  → status: ACTIVE, ARCHIVED, BLOCKED, DELETED 포함

---

## 실행 순서

1. `users.json` → users, user_profiles 삽입
2. `post.json` → 게시글 삽입
3. 좋아요 API 테스트 실행 (users, posts 선행 필요)

---

## 샘플 요약

### 사용자 (users + user_profile)

- userId: `usr_writer`, `usr1`, `usr2`, `usr3`, `usr4`, `usr5`, `usr6`
- userProfileId: `up0` ~ `up6`
- userType: "USER"
- isActive: true
- isEmailVerified: true

### 게시글 (posts)

- postId: `active_post_id`, `archived_post_id`, `blocked_post_id`, `deleted_post_id`
- userId: `usr_writer`
- content: 각기 다름
- status: ACTIVE, ARCHIVED, BLOCKED, DELETED

---

## 테스트용 상수 정의

```java
// 게시글 ID (status별 분류)
public static final String ACTIVE_POST_ID = "active_post_id";
public static final String ARCHIVED_POST_ID = "archived_post_id";
public static final String BLOCKED_POST_ID = "blocked_post_id";
public static final String DELETED_POST_ID = "deleted_post_id";

// 게시글 작성자 이메일
public static final String POST_WRITER_EMAIL = "writer@threadly.com";

// 좋아요를 누를 사용자 이메일 목록
public static final List<String> POST_LIKE_USER_EMAILS = List.of(
    "sunset_gazer1@threadly.com",
    "sky_gazer2@threadly.com",
    "book_worm3@threadly.com",
    "beach_bum4@threadly.com",
    "early_bird5@threadly.com",
    "mountain_hiker6@threadly.com"
);