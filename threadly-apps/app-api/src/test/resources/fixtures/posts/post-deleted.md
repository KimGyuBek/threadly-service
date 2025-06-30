# post-deleted.json 설명

> 이 파일은 `post-deleted.json`에 포함된 게시글 데이터에 대한 설명 문서입니다.

## 상태: `DELETED`

- 총 게시글 수: 2
- 사용 목적: 삭제(Deleted) 상태의 게시글 테스트용

## 데이터 목록

| postId | userId | content               | viewCount | 설명                |
|--------|--------|-----------------------|-----------|-------------------|
| post21 | usr50  | 소소한 행복이 참 큰 위로가 되는 요즘 | 285       | 삭제된 일상 공유 게시글 예시  |
| post48 | usr99  | 하늘색이 물든 저녁, 그냥 좋았다    | 1154      | 감성적인 게시글 삭제 상태 예시 |

## 관련 상수

```java
public static final Map<String, String> POST_DELETED_1 = Map.of(
    "postId", "post21",
    "userId", "usr50"
);
public static final Map<String, String> POST_DELETED_2 = Map.of(
    "postId", "post48",
    "userId", "usr99"
);
```