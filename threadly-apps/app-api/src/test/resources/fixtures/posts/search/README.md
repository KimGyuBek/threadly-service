# /posts/search 설명

> 이 파일은 게시글 검색 API 테스트를 위한 데이터 설명 문서입니다.

---

## 구성 파일

- `private-user.json`
  → 비공개 계정 사용자 (usr69)
  → email: city_runner69@threadly.com
  → isActive=true, isEmailVerified=true
  → 비공개 계정 설정 필요

- `follow.json`
  → 팔로우 관계 데이터
  → usr1 (EMAIL_VERIFIED_USER_1) → usr69 (APPROVED)
  → 비공개 계정 게시글 접근 테스트용

---

## 실행 순서

### 기본 테스트
1. `user-fixture.json` → users, user_profiles 삽입 (100명)
2. `post-fixture.json` → 게시글 삽입 (100개)
3. 게시글 검색 API 테스트 실행

### 비공개 계정 팔로우 테스트
1. `private-user.json` → usr69 비공개 계정으로 로드
2. `follow.json` → 팔로우 관계 생성
3. 팔로우된 비공개 계정 게시글 검색 테스트

---

## 샘플 요약

### 비공개 사용자 (private-user.json)
- userId: `usr69`
- email: `city_runner69@threadly.com`
- userType: "USER"
- isActive: true
- isEmailVerified: true
- **비공개 계정으로 설정 필요**

### 팔로우 관계 (follow.json)
- followId: `follow_search_01`
- followerId: `usr1` (EMAIL_VERIFIED_USER_1: sunset_gazer1@threadly.com)
- followingId: `usr69`
- status: APPROVED

---

## 테스트용 상수 정의

```java
// 검색 키워드
public static final String SEARCH_KEYWORD_SINGLE = "퇴근";  // usr69 게시글
public static final String SEARCH_KEYWORD_MULTI = "좋";     // 복수 게시글

// 비공개 계정 사용자
public static final String PRIVATE_USER_ID = "usr69";
public static final String PRIVATE_USER_EMAIL = "city_runner69@threadly.com";

// 공개 계정 사용자
public static final String PUBLIC_USER_ID = "usr98";
public static final String PUBLIC_USER_EMAIL = "tea_addict98@threadly.com";
```

---

## 참고사항

- 기존 `user-fixture.json`과 `post-fixture.json`을 기본으로 사용
- usr69: "퇴근 후 걷는 이 길이 요즘 내 힐링 루트" (post1)
- usr98: "비 오는 날엔 감성 플레이리스트가 최고다" (post2)
- "좋" 키워드는 여러 게시글에 포함됨
