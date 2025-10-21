# 📄 Follow 요청 테스트 개요

이 문서는 팔로우 요청 처리 (수락/거절) 기능 테스트 시 사용되는 JSON 데이터를 설명하고, 관련된 사용자 및 팔로우 ID를 상수로 정의하여 테스트 코드에서 재사용 가능하도록
정리한 문서이다.

---

## ✅ 테스트 시나리오 요약

### 🔹 1. follow.json

- `test_user`가 `target_user`에게 팔로우 요청을 보낸 상황
- 상태: `PENDING`

### 🔹 2. follows.json

- `users.json`에 포함된 `usr1`, `usr2`, `usr3` 간의 팔로우 관계 테스트
- 두 건의 팔로우 요청이 존재:
    - `approve_follow`: `usr1` → `usr3` (상태: APPROVED)
    - `pending_follow`: `usr2` → `usr3` (상태: PENDING)

---

## 🧷 테스트용 상수 정의

### 🔸 기본 사용자

```java
// test-user.json
public static final String TEST_USER_ID = "test_user";
public static final String TEST_USER_EMAIL = "test_user@threadly.com";

// target-user.json
public static final String TARGET_USER_ID = "target_user";
public static final String TARGET_USER_EMAIL = "target_user@threadly.com";
```

### 🔸 follows.json에 필요한 사용자들 (users.json 기준)

```java
// usr1 (follower of approve_follow)
public static final String APPROVE_FOLLOW_FOLLOWER_ID = "usr1";
public static final String APPROVE_FOLLOW_FOLLOWER_EMAIL = "sunset_gazer1@threadly.com";

// usr2 (follower of pending_follow)
public static final String PENDING_FOLLOW_FOLLOWER_ID = "usr2";
public static final String PENDING_FOLLOW_FOLLOWER_EMAIL = "sky_gazer2@threadly.com";

// usr3 (공통 following 대상)
public static final String FOLLOWING_USER_ID = "usr3";
public static final String FOLLOWING_USER_EMAIL = "book_worm3@threadly.com";
```

### 🔸 Follow ID

```java
// follow.json
public static final String TEST_PENDING_FOLLOW_ID = "follow_0";

// follows.json
public static final String APPROVED_FOLLOW_ID = "approve_follow";
public static final String PENDING_FOLLOW_ID = "pending_follow";
```

---

## 📂 데이터 간 관계 요약

| 파일                 | 설명                                          |
|--------------------|---------------------------------------------|
| `follow.json`      | `test_user` → `target_user` (PENDING)       |
| `follows.json`     | `usr1`/`usr2` → `usr3` (APPROVED / PENDING) |
| `users.json`       | `usr1`, `usr2`, `usr3` 정보 포함                |
| `test-user.json`   | `test_user` 단일 정보                           |
| `target-user.json` | `target_user` 단일 정보                         |