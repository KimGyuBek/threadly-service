# GetUserProfile 테스트 픽스처

> 이 디렉토리는 사용자 프로필 조회 (`getUserProfile`) 기능의 비공개 계정 테스트를 위한 픽스처 데이터를 포함합니다.

## 테스트 시나리오

이 픽스처는 다음과 같은 테스트 시나리오를 지원합니다:

### 성공 케이스
1. **공개 계정 - 팔로우 관계 없음**: 팔로우하지 않아도 프로필 조회 가능
2. **공개 계정 - 팔로우됨**: 팔로우 상태에서 프로필 조회
3. **비공개 계정 - 팔로우 승인됨**: 승인된 팔로우 관계에서 프로필 조회 가능
4. **본인 프로필**: 자신의 프로필 조회

### 실패 케이스
1. **비공개 계정 - 팔로우 안됨**: 팔로우하지 않은 비공개 계정 프로필 조회 시 403 Forbidden
2. **비공개 계정 - 팔로우 대기 중**: 팔로우 요청이 대기 중인 비공개 계정 프로필 조회 시 403 Forbidden

## 파일 구성

### 1. `public-users.json`
- **목적**: 공개 계정 사용자 데이터
- **사용자 수**: 2명
- **특징**: `isPrivate: false`로 설정된 공개 계정들

| userId | nickname | isPrivate | 용도 |
|--------|----------|-----------|------|
| public_user_1 | public_user_1 | false | 공개 계정 팔로우됨 테스트 |
| public_user_2 | public_user_2 | false | 공개 계정 팔로우 안됨 테스트 |

### 2. `private-users.json`
- **목적**: 비공개 계정 사용자 데이터
- **사용자 수**: 3명
- **특징**: `isPrivate: true`로 설정된 비공개 계정들

| userId | nickname | isPrivate | 용도 |
|--------|----------|-----------|------|
| private_user_1 | private_user_1 | true | 비공개 계정 팔로우 승인됨 테스트 |
| private_user_2 | private_user_2 | true | 비공개 계정 팔로우 대기중 테스트 |
| private_user_3 | private_user_3 | true | 비공개 계정 팔로우 안됨 테스트 |

### 3. `follow-relationships.json`
- **목적**: 테스트용 팔로우 관계 데이터
- **기준 사용자**: `user_with_profile_test` (기존 테스트 사용자)

| followerId | followingId | status | 설명 |
|------------|-------------|--------|------|
| user_with_profile_test | public_user_1 | APPROVED | 공개 계정 팔로우됨 |
| user_with_profile_test | private_user_1 | APPROVED | 비공개 계정 팔로우 승인됨 |
| user_with_profile_test | private_user_2 | PENDING | 비공개 계정 팔로우 대기중 |

## 테스트 상수

```java
// 공개 계정 사용자들
public static final String PUBLIC_USER_1_ID = "public_user_1";
public static final String PUBLIC_USER_2_ID = "public_user_2";
public static final String PUBLIC_USER_1_EMAIL = "public_user1@threadly.com";
public static final String PUBLIC_USER_2_EMAIL = "public_user2@threadly.com";

// 비공개 계정 사용자들  
public static final String PRIVATE_USER_1_ID = "private_user_1";
public static final String PRIVATE_USER_2_ID = "private_user_2";
public static final String PRIVATE_USER_3_ID = "private_user_3";
public static final String PRIVATE_USER_1_EMAIL = "private_user1@threadly.com";
public static final String PRIVATE_USER_2_EMAIL = "private_user2@threadly.com";
public static final String PRIVATE_USER_3_EMAIL = "private_user3@threadly.com";

// 프로필 데이터
public static final Map<String, String> PUBLIC_USER_1_PROFILE = Map.of(
    "userId", "public_user_1",
    "nickname", "public_user_1",
    "statusMessage", "공개 계정입니다",
    "bio", "누구나 볼 수 있는 프로필"
);

public static final Map<String, String> PRIVATE_USER_1_PROFILE = Map.of(
    "userId", "private_user_1", 
    "nickname", "private_user_1",
    "statusMessage", "비공개 계정입니다",
    "bio", "팔로우 승인 후 볼 수 있습니다"
);
```

## 픽스처 로딩 방법

```java
// 공개 계정 사용자 로딩
userFixtureLoader.load(
    "/users/profile/get-user-profile/public-users.json", 
    UserStatusType.ACTIVE
);

// 비공개 계정 사용자 로딩  
userFixtureLoader.load(
    "/users/profile/get-user-profile/private-users.json", 
    UserStatusType.ACTIVE
);

// 팔로우 관계 로딩
userFollowFixtureLoader.load(
    "/users/profile/get-user-profile/follow-relationships.json"
);
```

## 관련 테스트 클래스

- `GetUserProfileApiTest`: 기본 공개 계정 테스트
- 추가될 테스트: 비공개 계정 및 팔로우 관계 테스트들

## 예상 테스트 결과

### FollowStatusType 별 응답
- `NONE`: 팔로우 관계 없음
- `PENDING`: 팔로우 요청 대기 중  
- `APPROVED`: 팔로우 승인됨
- `SELF`: 본인 프로필

### HTTP 응답 코드
- `200 OK`: 프로필 조회 성공
- `403 Forbidden`: 비공개 계정 접근 거부 (`USER_PROFILE_PRIVATE`)
- `404 Not Found`: 사용자 없음 (`USER_NOT_FOUND`)