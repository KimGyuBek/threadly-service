# post-blocked.json 설명

> 이 파일은 `post-blocked.json`에 포함된 게시글 데이터에 대한 설명 문서입니다.

##  상태: `BLOCKED`

- 총 게시글 수: 2
- 사용 목적: 차단(Blocked) 상태의 게시글 테스트용

##  데이터 목록

| postId | userId | content                | viewCount | 설명              |
|--------|--------|------------------------|-----------|-----------------|
| post24 | usr8   | 바람이 차가워졌다. 가을이 오는구나    | 2732      | 계절 관련 감성 게시글 예시 |
| post41 | usr61  | 오늘도 별일 없던 하루, 그게 제일 좋다 | 1504      | 일상 공유 게시글 예시    |

## 관련 상수

```java
public static final Map<String, String> POST_BLOCKED_1 = Map.of(
    "postId", "post24",
    "userId", "usr8"
);
public static final Map<String, String> POST_BLOCKED_2 = Map.of(
    "postId", "post41",
    "userId", "usr61"
);
```