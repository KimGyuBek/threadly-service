# Authentication API

Base URL: `/api/auth`

## Endpoints

### POST /api/auth/login
사용자 로그인

**Request:**
```json
{
  "email": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "accessToken": "string",
  "refreshToken": "string"
}
```

### POST /api/auth/logout
사용자 로그아웃

**Request Headers:**
- `Authorization`: Bearer token (optional)

**Response:** 
- Status: 200 OK
- Body: None

### POST /api/auth/reissue
AccessToken 재발급

**Request Headers:**
- `X-refresh-token`: Refresh token (optional)

**Response:**
```json
{
  "accessToken": "string",
  "refreshToken": "string"
}
```

### GET /api/auth/verify-email
이메일 인증

**Query Parameters:**
- `code`: string (인증 코드)

**Response:**
- Status: 200 OK
- Body: None

### POST /api/auth/verify-password
비밀번호 재인증 (사용자 정보 수정용)

**Request:**
```json
{
  "password": "string"
}
```

**Response:**
```json
{
  "token": "string"
}
```