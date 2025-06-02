# /create-comment-like 설명

> 이 파일은 게시글 댓글 좋아요 API 테스트를 위한 데이터 설명 문서입니다.

---

## 구성 파일

- `user.json`  
  → 테스트용 사용자 다수 포함  
  → 모든 사용자에 userProfile 포함됨  
  → isActive=true, isEmailVerified=true

- `post.json`  
  → 테스트 대상 게시글 1개  
  → 작성자: `usr_writer`  
  → status: ACTIVE

- `post-comment.json`  
  → 댓글 데이터  
  → ACTIVE 게시글에 상태별 댓글 존재  
  → 상태: ACTIVE, DELETED, BLOCKED

---

## 실행 순서

1. `user.json` → users, user_profiles 삽입
2. `post.json` → 게시글 삽입
3. `post-comment.json` → 댓글 삽입
4. 댓글 좋아요 API 테스트 실행

---

## 샘플 요약

### 사용자 (users + user_profile)

- userId: `usr_writer`, `usr1`, `usr2`, ..., `usrN`
- userProfileId: 각각 다름
- userType: "USER"
- isActive: true
- isEmailVerified: true

### 게시글 (posts)

- postId: `active_post_id`
- userId: `usr_writer`
- status: ACTIVE

---

## 테스트용 상수 정의

```java
// 게시글 ID
public static final String ACTIVE_POST_ID = "active_post_id";

// 댓글 ID (상태별 분류)
public static final String ACTIVE_COMMENT_ID = "cmt_active_001";
public static final String DELETED_COMMENT_ID = "cmt_deleted_001";
public static final String BLOCKED_COMMENT_ID = "cmt_blocked_001";

// 전체 사용자 수
public static final int USER_COUNT = 101;

// 사용자 이메일 목록
public static final List<String> USER_EMAILS = List.of(
    "writer@threadly.com",
    "sunset_gazer1@threadly.com",
    "sky_gazer2@threadly.com",
    "book_worm3@threadly.com",
    "beach_bum4@threadly.com",
    "early_bird5@threadly.com",
    "mountain_hiker6@threadly.com",
    "dream_chaser7@threadly.com",
    "night_owl8@threadly.com",
    "mountain_hiker9@threadly.com",
    "mountain_hiker10@threadly.com",
    "dream_chaser11@threadly.com",
    "breeze_seeker12@threadly.com",
    "early_bird13@threadly.com",
    "silent_walker14@threadly.com",
    "beach_bum15@threadly.com",
    "sky_gazer16@threadly.com",
    "city_runner17@threadly.com",
    "beach_bum18@threadly.com",
    "wave_rider19@threadly.com",
    "tea_addict20@threadly.com",
    "sunset_gazer21@threadly.com",
    "night_owl22@threadly.com",
    "lazy_sunday23@threadly.com",
    "tea_addict24@threadly.com",
    "coffee_lover25@threadly.com",
    "sky_gazer26@threadly.com",
    "rainy_day27@threadly.com",
    "breeze_seeker28@threadly.com",
    "sunset_gazer29@threadly.com",
    "silent_walker30@threadly.com",
    "lazy_sunday31@threadly.com",
    "tea_addict32@threadly.com",
    "lazy_sunday33@threadly.com",
    "rainy_day34@threadly.com",
    "book_worm35@threadly.com",
    "bike_rider36@threadly.com",
    "sunset_gazer37@threadly.com",
    "art_junkie38@threadly.com",
    "mountain_hiker39@threadly.com",
    "beach_bum40@threadly.com",
    "silent_walker41@threadly.com",
    "wave_rider42@threadly.com",
    "coffee_lover43@threadly.com",
    "coffee_lover44@threadly.com",
    "dream_chaser45@threadly.com",
    "dream_chaser46@threadly.com",
    "book_worm47@threadly.com",
    "dream_chaser48@threadly.com",
    "art_junkie49@threadly.com",
    "lazy_sunday50@threadly.com",
    "tea_addict51@threadly.com",
    "dream_chaser52@threadly.com",
    "art_junkie53@threadly.com",
    "night_owl54@threadly.com",
    "night_owl55@threadly.com",
    "coffee_lover56@threadly.com",
    "forest_soul57@threadly.com",
    "rainy_day58@threadly.com",
    "gallery_goer59@threadly.com",
    "rainy_day60@threadly.com",
    "early_bird61@threadly.com",
    "rainy_day62@threadly.com",
    "dream_chaser63@threadly.com",
    "breeze_seeker64@threadly.com",
    "forest_soul65@threadly.com",
    "city_runner66@threadly.com",
    "city_runner67@threadly.com",
    "dream_chaser68@threadly.com",
    "city_runner69@threadly.com",
    "early_bird70@threadly.com",
    "coffee_lover71@threadly.com",
    "gallery_goer72@threadly.com",
    "breeze_seeker73@threadly.com",
    "forest_soul74@threadly.com",
    "breeze_seeker75@threadly.com",
    "city_runner76@threadly.com",
    "wave_rider77@threadly.com",
    "dream_chaser78@threadly.com",
    "early_bird79@threadly.com",
    "night_owl80@threadly.com",
    "sunset_gazer81@threadly.com",
    "dream_chaser82@threadly.com",
    "silent_walker83@threadly.com",
    "forest_soul84@threadly.com",
    "gallery_goer85@threadly.com",
    "forest_soul86@threadly.com",
    "night_owl87@threadly.com",
    "beach_bum88@threadly.com",
    "art_junkie89@threadly.com",
    "city_runner90@threadly.com",
    "lazy_sunday91@threadly.com",
    "bike_rider92@threadly.com",
    "coffee_lover93@threadly.com",
    "early_bird94@threadly.com",
    "lazy_sunday95@threadly.com",
    "mountain_hiker96@threadly.com",
    "rainy_day97@threadly.com",
    "tea_addict98@threadly.com",
    "early_bird99@threadly.com",
    "breeze_seeker100@threadly.com"
);
```