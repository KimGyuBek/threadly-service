# My Profile API

Base URL: `/api/me/profile`

## Endpoints

### GET /api/me/profile
내 프로필 수정용 정보 조회

**Response:**
```json
{
  "userId": "string",
  "email": "string",
  "nickname": "string",
  "bio": "string",
  "profileImageUrl": "string",
  "isPrivate": "boolean"
}
```

### POST /api/me/profile
프로필 초기 설정

**Request:**
```json
{
  "nickname": "string",
  "bio": "string",
  "profileImageId": "string",
  "isPrivate": "boolean"
}
```

**Response:**
```json
{
  "accessToken": "string",
  "refreshToken": "string"
}
```

### PATCH /api/me/profile
프로필 업데이트

**Request:**
```json
{
  "nickname": "string",
  "bio": "string",
  "profileImageId": "string"
}
```

**Response:**
- Status: 200 OK
- Body: None

### PATCH /api/me/profile/privacy
계정 공개/비공개 설정 변경

**Request:**
```json
{
  "isPrivate": "boolean"
}
```

**Response:**
- Status: 200 OK
- Body: None

### POST /api/me/profile/image
프로필 이미지 업로드

**Request:**
- Content-Type: multipart/form-data
- Form parameter: `image` (file, optional)

**Response:**
```json
{
  "imageId": "string",
  "imageUrl": "string"
}
```

### GET /api/me/profile/check
닉네임 중복 확인

**Query Parameters:**
- `nickname`: string

**Response:**
- Status: 200 OK (사용 가능)
- Status: 409 Conflict (중복됨)
- Body: None