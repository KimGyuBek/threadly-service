# Users API

Base URL: `/api/users`

## Endpoints

### POST /api/users
회원 가입

**Request:**
```json
{
  "email": "string",
  "password": "string",
  "name": "string"
}
```

**Response:**
```json
{
  "userId": "string",
  "email": "string",
  "message": "string"
}
```

---

# User Profile API

Base URL: `/api/users/profile`

## Endpoints

### GET /api/users/profile/{userId}
사용자 프로필 조회

**Path Parameters:**
- `userId`: string

**Response:**
```json
{
  "userId": "string",
  "nickname": "string",
  "profileImageUrl": "string",
  "bio": "string",
  "followersCount": "number",
  "followingsCount": "number",
  "postsCount": "number",
  "isPrivate": "boolean",
  "isFollowing": "boolean",
  "isFollowRequested": "boolean"
}
```