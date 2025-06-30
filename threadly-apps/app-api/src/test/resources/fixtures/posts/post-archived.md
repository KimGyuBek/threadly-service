# post-archived.json 설명

> 이 파일은 `post-archived.json`에 포함된 게시글 데이터에 대한 설명 문서입니다.

##  상태: `ARCHIVED`

- 총 게시글 수: 2
- 사용 목적: 보관(Archive)된 게시글 상태 테스트용

##  데이터 목록

| postId | userId | content               | viewCount | 설명            |
|--------|--------|-----------------------|-----------|---------------|
| post69 | usr92  | 카페 창가에 앉아 멍하니 바깥을 봄   | 2654      | 보관된 감성 게시글 예시 |
| post82 | usr93  | 좋아하는 향기 나는 방에 누워있기  | 4851      | 보관 상태 기능 테스트용 |

## 관련 상수

```java
public static final Map<String, String> POST_ARCHIVED_1 = Map.of(
    "postId", "post69",
    "userId", "usr92"
);
public static final Map<String, String> POST_ARCHIVED_2 = Map.of(
    "postId", "post82",
    "userId", "usr93"
);
```

