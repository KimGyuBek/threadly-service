# Threadly API Documentation

이 문서는 Threadly 애플리케이션의 REST API에 대한 상세한 문서입니다.

## 기본 정보

- **Base URL**: `https://api.threadly.com`
- **Authentication**: JWT Bearer Token
- **Content-Type**: `application/json` (파일 업로드 제외)

## 인증 방식

대부분의 API는 JWT 토큰을 사용한 인증이 필요합니다.

**Request Header:**
```
Authorization: Bearer <access_token>
```

## 커서 기반 페이지네이션

목록 조회 API들은 커서 기반 페이지네이션을 사용합니다.

**공통 Query Parameters:**
- `cursor_timestamp`: 이전 페이지의 마지막 아이템의 타임스탬프
- `cursor_id`: 이전 페이지의 마지막 아이템의 ID
- `limit`: 조회할 아이템 수 (기본값: 10)

**공통 Response 구조:**
```json
{
  "content": [...],
  "nextCursor": {
    "cursorTimestamp": "2024-01-01T00:00:00",
    "cursorId": "string"
  }
}
```

## API 그룹

### [인증 (Authentication)](./auth.md)
- 로그인, 로그아웃, 토큰 재발급, 이메일 인증

### [사용자 (Users)](./users.md)
- 회원가입, 사용자 프로필 조회

### [내 계정 (My Account)](./me-account.md)
- 비밀번호 변경, 계정 탈퇴/비활성화

### [내 프로필 (My Profile)](./me-profile.md)
- 프로필 설정/수정, 프로필 이미지 업로드, 닉네임 중복 확인

### [팔로우 (Follows)](./follows.md)
- 팔로우 요청/수락/거절, 팔로워/팔로잉 목록, 언팔로우

### [게시글 (Posts)](./posts.md)
- 게시글 작성/조회/수정/삭제, 게시글 목록

### [게시글 댓글 (Post Comments)](./post-comments.md)
- 댓글 작성/조회/삭제

### [게시글 좋아요 (Post Likes)](./post-likes.md)
- 게시글 좋아요/취소, 좋아요 목록, 활동 요약

### [댓글 좋아요 (Post Comment Likes)](./post-comment-likes.md)
- 댓글 좋아요/취소, 좋아요 목록

### [게시글 이미지 (Post Images)](./post-images.md)
- 게시글 이미지 업로드

## 공통 응답 코드

- `200 OK`: 성공
- `201 Created`: 생성 성공
- `204 No Content`: 성공 (응답 데이터 없음)
- `400 Bad Request`: 잘못된 요청
- `401 Unauthorized`: 인증 실패
- `403 Forbidden`: 권한 없음
- `404 Not Found`: 리소스 없음
- `409 Conflict`: 중복/충돌
- `500 Internal Server Error`: 서버 오류

## 공통 에러 응답

```json
{
  "code": "ERROR_CODE",
  "message": "Error message",
  "timestamp": "2024-01-01T00:00:00"
}
```