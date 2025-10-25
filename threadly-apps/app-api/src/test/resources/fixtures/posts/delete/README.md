# post-delete 테스트 데이터 설명

> 이 파일은 `/post-delete`에 포함된 테스트 데이터에 대한 설명 문서입니다.  
> 게시글 삭제 시 관련된 좋아요, 댓글, 댓글 좋아요의 동작을 검증하기 위한 상태 구성입니다.

---

## 구성 파일

- `user.json`  
    - 테스트 사용자 총 11명 (작성자 1명 + 일반 사용자 10명)  
    - 모든 사용자: `isActive = true`, `isEmailVerified = true`  
    - 각 사용자마다 `userProfile` 포함

- `post.json`  
    - 상태가 `ACTIVE`인 게시글 1건  
    - 작성자는 `sunset_gazer@threadly.com`

- `post_like.json`  
    - 위 게시글에 대해 10명의 사용자가 좋아요를 누름

- `post_comment.json`  
    - 게시글에 작성된 댓글 5개  
    - 다양한 사용자가 작성

- `comment_like.json`  
    - 2개의 댓글에 대해 좋아요 존재

---

## 테스트 시나리오 요약

- 게시글 1개 삭제 시 다음을 함께 검증:
  - 해당 게시글의 상태가 `INACTIVE`로 변경됨
  - 연결된 `post_like`는 삭제됨
  - 연결된 `post_comment`는 `INACTIVE`로 변경됨
  - 연결된 `comment_like`는 삭제됨

---

## 테스트용 상수 정의

```java
// 게시글 정보
public static final String POST_ACTIVE_ID = "post1";
public static final String POST_OWNER_EMAIL = "author@threadly.com";

// 게시글 좋아요 수
public static final int POST_LIKE_COUNT = 10;

// 댓글 정보
public static final int POST_COMMENT_COUNT = 5;
public static final Map<String, Integer> COMMENT_LIKE_COUNT = Map.of(
    "comment_1", 3,
    "comment_3", 1
);
```
