# 팔로우/팔로잉 통계 테스트 데이터

## 개요
사용자의 팔로워 및 팔로잉 수 조회 API 테스트를 위한 데이터입니다.

## 데이터 구조

### 메인 사용자 (main_user)
- **userId**: `main_user`
- **팔로워 수**: 10명 (follower_01 ~ follower_10)
- **팔로잉 수**: 20명 (following_01 ~ following_20)

## 파일 설명

### users.json
총 31명의 사용자 데이터:
- **main_user**: 테스트 대상 메인 사용자
- **follower_01 ~ follower_10**: main_user를 팔로우하는 10명의 사용자
- **following_01 ~ following_20**: main_user가 팔로우하는 20명의 사용자

#### 사용자 데이터 특징
- `isPrivate` 필드 없음 (모든 계정은 공개 계정)
- `createdAt`, `modifiedAt` 필드 없음
- 모든 사용자는 `ACTIVE` 상태
- 이메일 인증 완료 상태 (`isEmailVerified: true`)

### follows.json
총 30개의 팔로우 관계 데이터:
- **팔로워 관계**: 10개 (follower_XX → main_user)
- **팔로잉 관계**: 20개 (main_user → following_XX)
- 모든 팔로우 관계는 `APPROVED` 상태

#### 팔로우 데이터 구조
```json
{
  "followId": "follow_stats_XX",
  "followerId": "팔로우하는_사용자_ID",
  "followingId": "팔로우받는_사용자_ID",
  "status": "APPROVED"
}
```

## 테스트 상수 정의

### 메인 사용자 정보
```java
// 팔로우 통계 테스트용 메인 사용자
public static final String MAIN_USER_ID = "main_user";
public static final String MAIN_USER_EMAIL = "main@threadly.com";
```

### 팔로워 사용자 정보
```java
// 팔로워 사용자 ID 목록 (main_user를 팔로우하는 사용자들)
public static final String FOLLOWER_01_ID = "follower_01";
public static final String FOLLOWER_02_ID = "follower_02";
public static final String FOLLOWER_03_ID = "follower_03";
public static final String FOLLOWER_04_ID = "follower_04";
public static final String FOLLOWER_05_ID = "follower_05";
public static final String FOLLOWER_06_ID = "follower_06";
public static final String FOLLOWER_07_ID = "follower_07";
public static final String FOLLOWER_08_ID = "follower_08";
public static final String FOLLOWER_09_ID = "follower_09";
public static final String FOLLOWER_10_ID = "follower_10";

// 팔로워 사용자 이메일 목록
public static final String FOLLOWER_01_EMAIL = "follower01@threadly.com";
public static final String FOLLOWER_02_EMAIL = "follower02@threadly.com";
public static final String FOLLOWER_03_EMAIL = "follower03@threadly.com";
public static final String FOLLOWER_04_EMAIL = "follower04@threadly.com";
public static final String FOLLOWER_05_EMAIL = "follower05@threadly.com";
public static final String FOLLOWER_06_EMAIL = "follower06@threadly.com";
public static final String FOLLOWER_07_EMAIL = "follower07@threadly.com";
public static final String FOLLOWER_08_EMAIL = "follower08@threadly.com";
public static final String FOLLOWER_09_EMAIL = "follower09@threadly.com";
public static final String FOLLOWER_10_EMAIL = "follower10@threadly.com";
```

### 팔로잉 사용자 정보
```java
// 팔로잉 사용자 ID 목록 (main_user가 팔로우하는 사용자들)
public static final String FOLLOWING_01_ID = "following_01";
public static final String FOLLOWING_02_ID = "following_02";
public static final String FOLLOWING_03_ID = "following_03";
public static final String FOLLOWING_04_ID = "following_04";
public static final String FOLLOWING_05_ID = "following_05";
public static final String FOLLOWING_06_ID = "following_06";
public static final String FOLLOWING_07_ID = "following_07";
public static final String FOLLOWING_08_ID = "following_08";
public static final String FOLLOWING_09_ID = "following_09";
public static final String FOLLOWING_10_ID = "following_10";
public static final String FOLLOWING_11_ID = "following_11";
public static final String FOLLOWING_12_ID = "following_12";
public static final String FOLLOWING_13_ID = "following_13";
public static final String FOLLOWING_14_ID = "following_14";
public static final String FOLLOWING_15_ID = "following_15";
public static final String FOLLOWING_16_ID = "following_16";
public static final String FOLLOWING_17_ID = "following_17";
public static final String FOLLOWING_18_ID = "following_18";
public static final String FOLLOWING_19_ID = "following_19";
public static final String FOLLOWING_20_ID = "following_20";
```

### 기대값 상수
```java
// 팔로우 통계 기대값
public static final int EXPECTED_FOLLOWER_COUNT = 10;
public static final int EXPECTED_FOLLOWING_COUNT = 20;
public static final int TOTAL_FOLLOW_RELATIONSHIPS = 30;
```

## 기대 결과
main_user에 대한 팔로우 통계 조회 시:
- **followerCount**: 10
- **followingCount**: 20

## 사용 예시
```java
// 테스트에서 fixture 로드
userFixtureLoader.load("/users/follow/follow-stats/users.json");
userFollowFixtureLoader.load("/users/follow/follow-stats/follows.json");

// API 호출 및 검증
GetUserFollowStatsApiResponse response = followQueryService.getUserFollowStats("main_user");
assertThat(response.followerCount()).isEqualTo(10);
assertThat(response.followingCount()).isEqualTo(20);
```

## 주의사항
- 이 데이터는 팔로우 통계 조회 전용이므로 다른 테스트와 격리하여 사용
- 모든 팔로우 관계가 승인된 상태이므로 PENDING이나 REJECTED 상태 테스트에는 부적합
- 대용량 데이터가 필요한 성능 테스트보다는 기능 검증 테스트에 적합