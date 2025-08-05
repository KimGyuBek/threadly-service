# Followers 테스트 데이터

이 문서는 `/users/follow/followings` 경로에서 사용되는 팔로잉 목록 조회 테스트 데이터를 설명합니다.

## 위치

- 경로: `/users/follow/follow-requests`
- 관련 JSON 파일: `user-follows.json`, `users.json`

## Follow 요청 개요

총 100개의 팔로우 요청이 존재하며, 모두 `"private_user"` 계정에 대한 `"APPROVED"` 상태 요청입니다. 이는 비공개 계정을 팔로우하려는 상황을 테스트하기
위한 구성입니다.

## 주요 테스트 상수

```java
/*팔로우 당하는 사용자 id*/
private static final String TARGET_USER_ID = "target_user";

/*팔로우 당하는 사용자 email*/
private static final String TARGET_USER_EMAIL = "target_user@threadly.com";

/*전체 팔로우 요청 수 */
private static final int FOLLOW_REQUESTS_SIZE = 100;

/*테스트 사용자 id*/
private static final String TEST_USER_ID = "test_user";

/*테스트 사용자 email*/
private static final String TEST_USER_EMAIL = "test_user@threadly.com";

```

이 상수들은 테스트 실행 시 인증 및 타겟 사용자 식별에 사용됩니다.

## 목적

이 데이터는 다음의 시나리오를 검증하는 데 사용됩니다:

- 비공개 계정에 팔로우 요청을 보냈을 때의 동작
- 다수의 사용자 요청 처리 성능 검증
- 팔로우 승인/거절 처리 흐름 검증

