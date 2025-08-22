# Follow API

Base URL: `/api/follows`

## Endpoints

### POST /api/follows
사용자 팔로우 요청

**Request:**
```json
{
  "targetUserId": "string"
}
```

**Response:**
```json
{
  "followId": "string",
  "status": "PENDING|APPROVED",
  "message": "string"
}
```

### PATCH /api/follows/{followId}/approve
팔로우 요청 수락

**Path Parameters:**
- `followId`: string

**Response:**
- Status: 200 OK
- Body: None

### DELETE /api/follows/{followId}
팔로우 요청 거절

**Path Parameters:**
- `followId`: string

**Response:**
- Status: 200 OK
- Body: None

### GET /api/follows/requests
팔로우 요청 목록 조회 (커서 기반)

**Query Parameters:**
- `cursor_timestamp`: datetime (optional)
- `cursor_id`: string (optional)
- `limit`: number (default: 10)

**Response:**
```json
{
  "content": [
    {
      "followId": "string",
      "requester": {
        "userId": "string",
        "nickname": "string",
        "profileImageUrl": "string"
      },
      "requestedAt": "datetime"
    }
  ],
  "nextCursor": {
    "cursorTimestamp": "datetime",
    "cursorId": "string"
  }
}
```

### GET /api/follows/followers
팔로워 목록 조회 (커서 기반)

**Query Parameters:**
- `user_id`: string (optional, 없으면 본인 팔로워 조회)
- `cursor_timestamp`: datetime (optional)
- `cursor_id`: string (optional)
- `limit`: number (default: 10)

**Response:**
```json
{
  "content": [
    {
      "userId": "string",
      "nickname": "string",
      "profileImageUrl": "string",
      "followedAt": "datetime"
    }
  ],
  "nextCursor": {
    "cursorTimestamp": "datetime",
    "cursorId": "string"
  }
}
```

### GET /api/follows/followings
팔로잉 목록 조회 (커서 기반)

**Query Parameters:**
- `user_id`: string (optional, 없으면 본인 팔로잉 조회)
- `cursor_timestamp`: datetime (optional)
- `cursor_id`: string (optional)
- `limit`: number (default: 10)

**Response:**
```json
{
  "content": [
    {
      "userId": "string",
      "nickname": "string",
      "profileImageUrl": "string",
      "followedAt": "datetime"
    }
  ],
  "nextCursor": {
    "cursorTimestamp": "datetime",
    "cursorId": "string"
  }
}
```

### DELETE /api/follows/requests/{targetUserId}
팔로우 요청 취소

**Path Parameters:**
- `targetUserId`: string

**Response:**
- Status: 200 OK
- Body: None

### DELETE /api/follows/following/{followingUserId}
언팔로우

**Path Parameters:**
- `followingUserId`: string

**Response:**
- Status: 200 OK
- Body: None

### DELETE /api/follows/followers/{followerId}
팔로워 삭제

**Path Parameters:**
- `followerId`: string

**Response:**
- Status: 200 OK
- Body: None