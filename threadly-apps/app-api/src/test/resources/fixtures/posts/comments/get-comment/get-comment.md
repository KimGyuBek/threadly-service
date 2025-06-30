# /create-comment 설명

> 이 파일은 게시글 댓글 API 테스트를 위한 데이터 설명 문서입니다.

---

## 구성 파일

- `user-fixture.json`  
  → 테스트용 사용자 다수 포함  
  → 모든 사용자에 userProfile 포함됨  
  → isActive=true, isEmailVerified=true

- `post.json`  
  → 테스트 대상 게시글 4개  
  → 작성자: `usr_writer`  
  → status: ACTIVE, ARCHIVED, BLOCKED, DELETED 포함

- `post_comments_test_data.json`  
  → 댓글 데이터  
  → ACTIVE 게시글에는 모든 유저가 댓글 작성  
  → 나머지 상태 게시글에는 댓글 3개씩만 포함

---

## 실행 순서

1. `user-fixture.json` → users, user_profiles 삽입
2. `post.json` → 게시글 삽입
3. `post_comments_test_data.json` → 댓글 삽입
4. 댓글 API 테스트 실행

---

## 샘플 요약

### 사용자 (users + user_profile)

- userId: `usr_writer`, `usr1`, `usr2`, ...
- userProfileId: 각각 다름
- userType: "USER"
- isActive: true
- isEmailVerified: true

### 게시글 (posts)

- postId: `active_post_id`, `archived_post_id`, `blocked_post_id`, `deleted_post_id`
- userId: `usr_writer`
- status: ACTIVE, ARCHIVED, BLOCKED, DELETED

---

## 테스트용 상수 정의

```java
// 게시글 ID (status별 분류)
public static final String ACTIVE_POST_ID = "active_post_id";
public static final String ARCHIVED_POST_ID = "archived_post_id";
public static final String BLOCKED_POST_ID = "blocked_post_id";
public static final String DELETED_POST_ID = "deleted_post_id";

// ACTIVE 상태 게시글 댓글
public static final List<String> ACTIVE_COMMENT_IDS = List.of(
    "cmt_active_001",
    "cmt_active_002",
    "cmt_active_003"
);
public static final List<String> ACTIVE_COMMENT_USER_IDS = List.of(
    "usr1",
    "usr2",
    "usr3"
);

// ARCHIVED 상태 게시글 댓글
public static final List<String> ARCHIVED_COMMENT_IDS = List.of(
    "cmt_archived_post_id_001",
    "cmt_archived_post_id_002",
    "cmt_archived_post_id_003"
);
public static final List<String> ARCHIVED_COMMENT_USER_IDS = List.of(
    "usr1",
    "usr2",
    "usr3"
);

// BLOCKED 상태 게시글 댓글
public static final List<String> BLOCKED_COMMENT_IDS = List.of(
    "cmt_blocked_post_id_001",
    "cmt_blocked_post_id_002",
    "cmt_blocked_post_id_003"
);
public static final List<String> BLOCKED_COMMENT_USER_IDS = List.of(
    "usr1",
    "usr2",
    "usr3"
);

// DELETED 상태 게시글 댓글
public static final List<String> DELETED_COMMENT_IDS = List.of(
    "cmt_deleted_post_id_001",
    "cmt_deleted_post_id_002",
    "cmt_deleted_post_id_003"
);
public static final List<String> DELETED_COMMENT_USER_IDS = List.of(
    "usr1",
    "usr2",
    "usr3"
);

// ACTIVE 게시글에 작성된 댓글의 수 
public static final int ACTIVE_POST_COMMENT_COUNT = 101;

```java