# GetUserPosts 테스트 픽스처

> 이 디렉토리는 특정 사용자의 게시글 목록 조회 (`getUserPosts`) 기능 테스트를 위한 픽스처 데이터를 포함합니다.

## 테스트 시나리오

이 픽스처는 팔로우 관계와 계정 공개/비공개 설정에 따른 게시글 접근 제어를 테스트합니다.

### 성공 케이스
1. **본인 게시글 조회**: 자신의 게시글은 계정 상태 관계없이 조회 가능
2. **공개 계정 게시글 조회**: 팔로우하지 않아도 공개 계정의 게시글 조회 가능
3. **팔로우 중인 비공개 계정 게시글 조회**: APPROVED 상태 팔로우 관계에서 조회 가능
4. **커서 기반 페이징**: 다음 페이지 커서(nextCursor) 검증
5. **전체 페이지 순회**: 모든 게시글을 커서 페이징으로 순회

### 실패 케이스
1. **팔로우하지 않은 비공개 계정**: 403 Forbidden (`USER_PROFILE_PRIVATE`)
2. **존재하지 않는 사용자**: 404 Not Found (TODO: 현재 구현 버그로 500 에러 발생)

## 파일 구성

### 1. `public-user.json`
- **목적**: 공개 계정 사용자 데이터
- **사용자 수**: 1명
- **특징**: `isPrivate: false`로 설정된 공개 계정

| userId | email | nickname | isPrivate | 게시글 수 |
|--------|-------|----------|-----------|----------|
| target_public_user | public@threadly.com | 공개계정 | false | 5개 |

### 2. `private-user.json`
- **목적**: 비공개 계정 사용자 데이터
- **사용자 수**: 1명
- **특징**: `isPrivate: true`로 설정된 비공개 계정

| userId | email | nickname | isPrivate | 게시글 수 |
|--------|-------|----------|-----------|----------|
| target_private_user | private@threadly.com | 비공개계정 | true | 3개 |

### 3. `viewer-user.json`
- **목적**: 조회자 사용자 데이터
- **사용자 수**: 1명
- **특징**: 게시글을 조회하는 테스트 사용자

| userId | email | nickname | isPrivate | 게시글 수 |
|--------|-------|----------|-----------|----------|
| test_viewer | viewer@threadly.com | 조회자 | false | 1개 |

### 4. `posts.json`
- **목적**: 테스트용 게시글 데이터
- **게시글 수**: 9개 (공개 계정 5개 + 비공개 계정 3개 + 조회자 1개)

| postId | userId | content | viewCount |
|--------|--------|---------|-----------|
| public_post_1 | target_public_user | 공개 계정의 첫 번째 게시글입니다 | 100 |
| public_post_2 | target_public_user | 공개 계정의 두 번째 게시글입니다 | 200 |
| public_post_3 | target_public_user | 공개 계정의 세 번째 게시글입니다 | 150 |
| public_post_4 | target_public_user | 공개 계정의 네 번째 게시글입니다 | 180 |
| public_post_5 | target_public_user | 공개 계정의 다섯 번째 게시글입니다 | 120 |
| private_post_1 | target_private_user | 비공개 계정의 첫 번째 게시글입니다 | 50 |
| private_post_2 | target_private_user | 비공개 계정의 두 번째 게시글입니다 | 75 |
| private_post_3 | target_private_user | 비공개 계정의 세 번째 게시글입니다 | 60 |
| viewer_post_1 | test_viewer | 조회자의 게시글입니다 | 30 |

### 5. `follows.json`
- **목적**: 테스트용 팔로우 관계 데이터
- **팔로우 수**: 1개

| followId | followerId | followingId | status |
|----------|------------|-------------|--------|
| follow_viewer_to_private | test_viewer | target_private_user | APPROVED |

## 테스트 상수

```java
// 사용자 ID
public static final String TARGET_PUBLIC_USER = "target_public_user";
public static final String TARGET_PRIVATE_USER = "target_private_user";
public static final String TEST_VIEWER = "test_viewer";

// 사용자 이메일
public static final String TARGET_PUBLIC_USER_EMAIL = "public@threadly.com";
public static final String TARGET_PRIVATE_USER_EMAIL = "private@threadly.com";
public static final String TEST_VIEWER_EMAIL = "viewer@threadly.com";

// 게시글 수
public static final int PUBLIC_USER_POST_COUNT = 5;
public static final int PRIVATE_USER_POST_COUNT = 3;
public static final int VIEWER_POST_COUNT = 1;

// 게시글 ID
public static final String PUBLIC_POST_1 = "public_post_1";
public static final String PRIVATE_POST_1 = "private_post_1";
public static final String VIEWER_POST_1 = "viewer_post_1";
```

## 픽스처 로딩 방법

```java
@BeforeEach
void setUp() {
    // 사용자 개별 로드 (공개/비공개 설정)
    userFixtureLoader.load("/posts/user-posts/public-user.json", UserStatus.ACTIVE, false);  // 공개 계정
    userFixtureLoader.load("/posts/user-posts/private-user.json", UserStatus.ACTIVE, true);  // 비공개 계정
    userFixtureLoader.load("/posts/user-posts/viewer-user.json", UserStatus.ACTIVE, false);  // 조회자

    // 게시글 로드
    postFixtureLoader.load("/posts/user-posts/posts.json", 9);

    // 팔로우 관계 로드 (test_viewer는 target_private_user를 팔로우 중)
    userFollowFixtureLoader.load("/users/user-fixture.json", "/posts/user-posts/follows.json");
}
```

## 관련 테스트 클래스

- `GetUserPostsApiTest`: 특정 사용자 게시글 목록 조회 API 테스트

## 예상 테스트 결과

### HTTP 응답 코드별 시나리오

| 시나리오 | HTTP 코드 | ErrorCode | 설명 |
|---------|-----------|-----------|------|
| 본인 게시글 조회 | 200 OK | - | 자신의 게시글은 항상 조회 가능 |
| 공개 계정 게시글 조회 | 200 OK | - | 팔로우 여부 무관하게 조회 가능 |
| 팔로우된 비공개 계정 게시글 | 200 OK | - | APPROVED 팔로우 관계 필요 |
| 팔로우하지 않은 비공개 계정 | 403 Forbidden | USER_PROFILE_PRIVATE | 접근 거부 |
| 존재하지 않는 사용자 | 404 Not Found | USER_NOT_FOUND | 사용자 없음 (TODO: 구현 버그) |

### 커서 페이징 동작

- **첫 페이지 요청**: `cursorTimestamp=null`, `cursorId=null`
- **다음 페이지**: `nextCursor.cursorTimestamp`, `nextCursor.cursorId` 사용
- **마지막 페이지**: `nextCursor.cursorTimestamp=null`

## 참고사항

- 모든 게시글은 `ACTIVE` 상태로 생성됨
- 게시글은 `postedAt` 기준 내림차순 정렬
- 비공개 계정 접근 제어는 `FollowAccessValidator`에서 처리
- TODO: 존재하지 않는 사용자 조회 시 `isUserPrivate()` null 반환 버그 수정 필요
