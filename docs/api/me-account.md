# My Account API

Base URL: `/api/me/account`

## Endpoints

### PATCH /api/me/account/password
비밀번호 변경

**Request:**
```json
{
  "currentPassword": "string",
  "newPassword": "string",
  "confirmPassword": "string"
}
```

**Response:**
- Status: 200 OK
- Body: None

### DELETE /api/me/account
계정 탈퇴

**Request Headers:**
- `Authorization`: Bearer token (required)

**Response:**
- Status: 200 OK
- Body: None

### PATCH /api/me/account/deactivate
계정 비활성화

**Request Headers:**
- `Authorization`: Bearer token (required)

**Response:**
- Status: 200 OK
- Body: None