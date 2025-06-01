# post-archived.json 설명

> 이 파일은 `/posts/likes/post-like-list`에 포함된 게시글 좋아요 관련 데이터에 대한 설명 문서입니다.

---

## 구성 파일

- `post-like-users.json`  
  → 테스트용 사용자 26명 (usr100~usr125 포함)  
  → 각 사용자에 userProfile 포함됨  
  → isActive=true, isEmailVerified=true

- `post.json`  
  → 테스트 대상 게시글 1개  
  → postId = "post_like_target", userId = "usr100" (게시글 작성자)  
  → status = ACTIVE

- `post-likes.json`  
  → usr101 ~ usr125 총 25명이 `post_like_target` 게시글에 좋아요

---

## 실행 순서

1. `post-like-users.json` → users, user_profiles 삽입
2. `post.json` → 게시글 삽입
3. `post-likes.json` → 좋아요 삽입 (users, posts 선행 필요)

---

## 샘플 요약

### 사용자 (users + user_profile)

- userId: `usr100` ~ `usr125`
- userProfileId: `up100` ~ `up125`
- userType: "USER"
- isActive: true
- isEmailVerified: true

### 게시글 (posts)

- postId: `post_like_target`
- userId: `usr100`
- content: "좋아요 테스트용 게시글입니다."
- status: ACTIVE

### 좋아요 (post_likes)

- 대상 postId: `post_like_target`
- 좋아요 누른 사용자: `usr101` ~ `usr125` (총 25명)
- createdAt: 2025-05-01T12:00:00 ~ 12:10:00 범위에서 10초 간격

## 테스트용 상수 정의

```java
public static final String POST_LIKE_TARGET_ID = "post_like_target";

public static final String POST_NO_LIKE_TARGET_ID = "post_no_like_target";

public static final String POST_LIKE_OWNER_ID = "usr100";

public static final int POST_LIKE_COUNT = 25;

public static final List<String> POST_LIKE_USER_IDS = List.of(
    "usr101", "usr102", "usr103", "usr104", "usr105",
    "usr106", "usr107", "usr108", "usr109", "usr110",
    "usr111", "usr112", "usr113", "usr114", "usr115",
    "usr116", "usr117", "usr118", "usr119", "usr120",
    "usr121", "usr122", "usr123", "usr124", "usr125"
);

public static final List<Map<String, String>> POST_LIKE_USER_POST_MAPPINGS = List.of(
    Map.of("userId", "usr101", "postId", "post_like_target"),
    Map.of("userId", "usr102", "postId", "post_like_target"),
    Map.of("userId", "usr103", "postId", "post_like_target"),
    Map.of("userId", "usr104", "postId", "post_like_target"),
    Map.of("userId", "usr105", "postId", "post_like_target"),
    Map.of("userId", "usr106", "postId", "post_like_target"),
    Map.of("userId", "usr107", "postId", "post_like_target"),
    Map.of("userId", "usr108", "postId", "post_like_target"),
    Map.of("userId", "usr109", "postId", "post_like_target"),
    Map.of("userId", "usr110", "postId", "post_like_target"),
    Map.of("userId", "usr111", "postId", "post_like_target"),
    Map.of("userId", "usr112", "postId", "post_like_target"),
    Map.of("userId", "usr113", "postId", "post_like_target"),
    Map.of("userId", "usr114", "postId", "post_like_target"),
    Map.of("userId", "usr115", "postId", "post_like_target"),
    Map.of("userId", "usr116", "postId", "post_like_target"),
    Map.of("userId", "usr117", "postId", "post_like_target"),
    Map.of("userId", "usr118", "postId", "post_like_target"),
    Map.of("userId", "usr119", "postId", "post_like_target"),
    Map.of("userId", "usr120", "postId", "post_like_target"),
    Map.of("userId", "usr121", "postId", "post_like_target"),
    Map.of("userId", "usr122", "postId", "post_like_target"),
    Map.of("userId", "usr123", "postId", "post_like_target"),
    Map.of("userId", "usr124", "postId", "post_like_target"),
    Map.of("userId", "usr125", "postId", "post_like_target")
);
```
