# post-fixture.json 설명

> 이 파일은 `post-fixture.json`에 포함된 게시글 데이터에 대한 설명 문서입니다.

## 상태: `ACTIVE`

- 총 게시글 수: 95
- 사용 목적: 활성(Active) 상태의 게시글 테스트용

## 샘플 데이터 목록 (5건 예시)

| postId | userId | content                 | viewCount |
|--------|--------|-------------------------|-----------|
| post1  | usr69  | 퇴근 후 걷는 이 길이 요즘 내 힐링 루트 | 1175      |
| post3  | usr95  | 아침 공기 너무 상쾌하다. 하루가 기대돼  | 151       |
| post11 | usr83  | 벚꽃 잎이 흩날리는 거리 너무 예뻤다    | 3824      |
| post26 | usr62  | 오랜만에 엄마 목소리 들으니 마음이 놓여  | 4716      |
| post54 | usr40  | 아침 공기 너무 상쾌하다. 하루가 기대돼  | 1232      |

※ 전체 데이터는 `post-fixture.json` 파일 참조

## 관련 상수

```java
public static final int POST_COUNT_ACTIVE = 95;
public static final int VIEW_COUNT_MIN = 0;
public static final int VIEW_COUNT_MAX = 500;

public static final Map<String, String> POST_ACTIVE_1 = Map.of(
    "postId", "post1",
    "userId", "usr69"
);
public static final Map<String, String> POST_ACTIVE_2 = Map.of(
    "postId", "post3",
    "userId", "usr95"
);
public static final Map<String, String> POST_ACTIVE_3 = Map.of(
    "postId", "post11",
    "userId", "usr83"
);
public static final Map<String, String> POST_ACTIVE_4 = Map.of(
    "postId", "post26",
    "userId", "usr62"
);
public static final Map<String, String> POST_ACTIVE_5 = Map.of(
    "postId", "post54",
    "userId", "usr40"
);
```
