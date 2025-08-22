# Post Comment Likes API

Base URL: `/api/posts/{postId}/comments/{commentId}/likes`

## Endpoints

### GET /api/posts/{postId}/comments/{commentId}/likes
댓글 좋아요 누른 사용자 목록 조회 (커서 기반)

**Path Parameters:**
- `postId`: string
- `commentId`: string

**Query Parameters:**
- `cursor_liked_at`: datetime (optional)
- `cursor_liker_id`: string (optional)
- `limit`: number (default: 10)

**Response:**
```json
{
  "content": [
    {
      "userId": "string",
      "nickname": "string",
      "profileImageUrl": "string",
      "likedAt": "datetime"
    }
  ],
  "nextCursor": {
    "cursorTimestamp": "datetime",
    "cursorId": "string"
  }
}
```

### POST /api/posts/{postId}/comments/{commentId}/likes
댓글 좋아요

**Path Parameters:**
- `postId`: string
- `commentId`: string

**Response:**
```json
{
  "liked": "boolean",
  "likeCount": "number"
}
```

### DELETE /api/posts/{postId}/comments/{commentId}/likes
댓글 좋아요 취소

**Path Parameters:**
- `postId`: string
- `commentId`: string

**Response:**
```json
{
  "liked": "boolean",
  "likeCount": "number"
}
```