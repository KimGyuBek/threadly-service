# /get-comment-like 설명

> 이 문서는 댓글 좋아요 API 테스트를 위한 데이터 구성을 설명합니다.

---

## 구성 파일

- `user.json`  
  → 테스트용 사용자 101명  
  → userId, email, profile 포함  
  → 모든 사용자 isActive=true, isEmailVerified=true

- `post-comment.json`  
  → 댓글 4개  
  → 상태: ACTIVE 2개, DELETED 1개, BLOCKED 1개

- `post-comment-like.json`  
  → comment-like 테이블용 데이터  
  → commentId + userId 조합으로 구성

---

## 실행 순서

1. `user.json` → 사용자 데이터 삽입
2. `post-comment.json` → 댓글 데이터 삽입
3. `post-comment-like.json` → 댓글 좋아요 데이터 삽입
4. API 테스트 실행

---

## 테스트용 상수 정의

```java
  // 게시글 ID
public static final String ACTIVE_POST_ID = "active_post_id";

// 댓글 ID (상태별)
//좋아요가 있는 댓글
public static final String ACTIVE_COMMENT_WITH_LIKES_ID = "cmt_active_001";

//좋아요가 없는 댓글
public static final String ACTIVE_COMMENT_WITHOUT_LIKES_ID = "cmt_active_002";

public static final String DELETED_COMMENT_ID = "cmt_deleted_001";
public static final String BLOCKED_COMMENT_ID = "cmt_blocked_001";

// 전체 좋아요 사용자 수
public static final int COMMENT_LIKE_USER_COUNT = 101;
```
