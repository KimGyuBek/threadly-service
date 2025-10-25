# user.json 설명

> 이 파일은 `user.json`에 포함된 사용자 데이터에 대한 설명 문서입니다.

## 상태: `EMAIL_VERIFIED`

- 총 사용자 수: 1명
- 사용 목적:  사용자 프로필 업데이트 테스트용
- ID 범위: `usr1` ~ `usr1`

## 샘플 데이터 목록 ()

| userId | email             | isEmailVerified | isActive |
|--------|-------------------|-----------------|----------|
| usr1   | usr1@threadly.com | true            | true     |

※ 전체 데이터는 `user-fixture.json` 파일 참조

## 관련 상수

```java

//userId
public static final String USER_ID = "user_with_profile_test";
public static final String USER2_ID = "user_with_profile_test2";

//user email
public static final String USER_EMAIL = "user_with_profile_test@threadly.com";
public static final String USER2_EMAIL = "user_with_profile_test2@threadly.com";

//user password
public static final String USER_PASSWORD = "1234";

//user profile
public static final Map<String, String> USER_PROFILE = Map.of(
    "userId", "user_with_profile_test",
    "nickname", "usr1_nickname",
    "statusMessage", "상태 메세지",
    "bio", "나는 사용자이다",
    "gender", "MALE",
    "profileType", "USER",
    "phone", "010-1111-1111"
);

public static final Map<String, String> USER2_PROFILE = Map.of(
    "userId", "user_with_profile_test2",
    "nickname", "usr1_nickname2",
    "statusMessage", "상태 메세지",
    "bio", "나는 사용자이다2",
    "gender", "MALE",
    "profileType", "USER",
    "phone", "010-2222-2222"
);
```