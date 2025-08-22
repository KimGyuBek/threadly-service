# Posts API

Base URL: `/api/posts`

## Endpoints

### GET /api/posts/{postId}
게시글 조회

**Path Parameters:**
- `postId`: string

**Response:**
```json
{
  "postId": "string",
  "author": {
    "userId": "string",
    "nickname": "string",
    "profileImageUrl": "string"
  },
  "content": "string",
  "images": [
    {
      "imageId": "string",
      "imageUrl": "string",
      "imageOrder": "number"
    }
  ],
  "viewCount": "number",
  "postedAt": "datetime",
  "likeCount": "number",
  "commentCount": "number",
  "liked": "boolean"
}
```

### GET /api/posts
게시글 목록 조회 (커서 기반)

**Query Parameters:**
- `cursor_posted_at`: datetime (optional)
- `cursor_post_id`: string (optional)
- `limit`: number (default: 10)

**Response:**
```json
{
  "content": [
    {
      "postId": "string",
      "author": {
        "userId": "string",
        "nickname": "string",
        "profileImageUrl": "string"
      },
      "content": "string",
      "images": [
        {
          "imageId": "string",
          "imageUrl": "string",
          "imageOrder": "number"
        }
      ],
      "viewCount": "number",
      "postedAt": "datetime",
      "likeCount": "number",
      "commentCount": "number",
      "liked": "boolean"
    }
  ],
  "nextCursor": {
    "cursorTimestamp": "datetime",
    "cursorId": "string"
  }
}
```

### POST /api/posts
게시글 생성

**Request:**
```json
{
  "content": "string",
  "images": [
    {
      "imageId": "string",
      "imageOrder": "number"
    }
  ]
}
```

**Response:**
```json
{
  "postId": "string",
  "content": "string",
  "postedAt": "datetime"
}
```

### PATCH /api/posts/{postId}
게시글 수정

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
  "postId": "string",
  "content": "string",
  "updatedAt": "datetime"
}
```

### DELETE /api/posts/{postId}
게시글 삭제

**Path Parameters:**
- `postId`: string

**Response:**
- Status: 200 OK
- Body: None