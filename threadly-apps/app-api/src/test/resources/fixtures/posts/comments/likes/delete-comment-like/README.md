# /delete-comment-like 설명

> 이 문서는 댓글 좋아요 삭제 API 테스트를 위한 데이터 구성을 설명합니다.

---

## 구성 파일

- `user.json`  
  → 테스트용 사용자 3명  
  → 작성자, 좋아요 누른 사용자, 누르지 않은 사용자 포함

- `post.json`  
  → 댓글 테스트용 게시글 1개  
  → 게시글 ID: active_post_id

- `post-comment.json`  
  → 게시글에 달린 댓글 2개  
  → 하나는 좋아요가 있고, 하나는 없음

- `post-comment-like.json`  
  → comment_liker가 댓글 하나에 좋아요를 누름

---

## 테스트 시나리오

- `cmt_active_001`: 좋아요 존재
- `cmt_active_002`: 좋아요 없음
- `comment_liker`: 좋아요를 누른 사용자
- `comment_not_liker`: 좋아요 누르지 않음
- `usr_writer`: 댓글 작성자 (삭제 대상 아님)

---

## 테스트용 상수 정의

```java
// 댓글 ID (상태별)
public static final String COMMENT_WITH_LIKES = "cmt_active_001";
public static final String COMMENT_WITHOUT_LIKES = "cmt_active_002";

// 게시글 ID
public static final String POST_ID = "active_post_id";

// 좋아요를 누른 사용자 이메일
public static final String COMMENT_LIKER_EMAIL = "commentliker@threadly.com";

// 좋아요를 누르지 않은 사용자 이메일
public static final String COMMENT_NOT_LIKER_EMAIL = "commentNotLiker@threadly.com";
```