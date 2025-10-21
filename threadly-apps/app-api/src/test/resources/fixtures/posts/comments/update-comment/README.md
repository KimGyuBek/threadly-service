# /update-comment 설명

> 이 파일은 댓글 수정 API 테스트를 위한 데이터 설명 문서입니다.

## 구성 파일

- `user.json`  
  → 테스트 사용자 다수 포함  
  → 이메일 인증 및 활성화 사용자 포함

- `post.json`  
  → 테스트 게시글 포함 (status: ACTIVE)

- `post-comment.json`  
  → 댓글 데이터 포함  
  → 상태별 댓글 존재: ACTIVE,  BLOCKED, DELETED

## 테스트용 상수 정의

```java
// 게시글 ID
public static final String ACTIVE_POST_ID = "active_post_id";

// 댓글 상태별 댓글 ID
public static final String ACTIVE_COMMENT_ID = "cmt_active_001";
public static final String DELETED_COMMENT_ID = "cmt_deleted_001";
public static final String ARCHIVED_COMMENT_ID = "cmt_archive_001";
public static final String BLOCKED_COMMENT_ID = "cmt_blocked_001";

// 댓글 작성자와 비작성자 이메일
public static final String COMMENT_WRITER_EMAIL = "comment_writer@threadly.com";
public static final String COMMENT_NOT_WRITER_EMAIL = "comment_not_writer@threadly.com";
```