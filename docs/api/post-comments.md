# Post Comments API

Base URL: `/api/posts/{postId}/comments`

## Endpoints

### GET /api/posts/{postId}/comments
게시글 댓글 목록 조회 (커서 기반)

**Path Parameters:**
- `postId`: string

**Query Parameters:**
- `cursor_commented_at`: datetime (optional)
- `cursor_comment_id`: string (optional)
- `limit`: number (default: 10)

**Response:**
```json
{
  "content": [
    {
      "commentId": "string",
      "author": {
        "userId": "string",
        "nickname": "string",
        "profileImageUrl": "string"
      },
      "content": "string",
      "commentedAt": "datetime",
      "likeCount": "number",
      "liked": "boolean"
    }
  ],
  "nextCursor": {
    "cursorTimestamp": "datetime",
    "cursorId": "string"
  }
}
```

### POST /api/posts/{postId}/comments
게시글 댓글 생성

**Path Parameters:**
- `postId`: string

**Request:**
```json
{
  "content": "string"
}
```

**Response:**
```json
{
  "commentId": "string",
  "content": "string",
  "commentedAt": "datetime"
}
```

### DELETE /api/posts/{postId}/comments/{commentId}
게시글 댓글 삭제

**Path Parameters:**
- `postId`: string
- `commentId`: string

**Response:**
- Status: 204 No Content
- Body: None