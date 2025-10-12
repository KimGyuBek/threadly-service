# user-email-not-verified.json 설명

> 이 파일은 `user-email-not-verified.json`에 포함된 사용자 데이터에 대한 설명 문서입니다.

## 상태: `EMAIL_NOT_VERIFIED`

- 총 사용자 수: 1

- 사용 목적: 이메일 인증이 되지 않은 사용자로 인한 로그인 실패 테스트용

## 데이터 목록

| userId                  | email                           | isEmailVerified | isActive |
|-------------------------|---------------------------------|-----------------|----------|
| user_email_not_verified | email_not_verified@threadly.com | false           | true     |

## 관련 상수

```java
public static final Map<String, Object> USER_EMAIL_NOT_VERIFIED = Map.of(
    "userId", "user_email_not_verified",
    "email", "email_not_verified@threadly.com"
);
```
