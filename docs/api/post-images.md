# Post Images API

Base URL: `/api/post-images`

## Endpoints

### POST /api/post-images
게시글 이미지 업로드

**Request:**
- Content-Type: multipart/form-data
- Form parameter: `images` (array of files, optional)

**Response:**
```json
{
  "images": [
    {
      "imageId": "string",
      "imageUrl": "string",
      "uploadedAt": "datetime"
    }
  ]
}
```