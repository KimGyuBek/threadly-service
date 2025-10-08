> 이 파일은 `/users/profiles/images/update`에 포함된 프로필 이미지 데이터에 대한 설명 문서입니다.

## 상태: `TEMPORARY`, `CONFIRMED`, `DELETED`

- 총 이미지 수: 3개
- 사용 목적: 프로필 이미지 상태별 처리 로직 테스트용
- 모든 이미지의 userId: `user_with_profile_test`

## 샘플 데이터 목록

| 상태        | imageId           | storedFileName          | imageUrl                        |
|-----------|-------------------|-------------------------|---------------------------------|
| TEMPORARY | temp-img-001      | temp_1234abcd.webp      | /images/temp_1234abcd.webp      |
| CONFIRMED | confirmed-img-001 | confirmed_abcd5678.webp | /images/confirmed_abcd5678.webp |
| DELETED   | deleted-img-001   | deleted_ijkl9012.webp   | /images/deleted_ijkl9012.webp   |

## 관련 상수

```java
/*프로필 이미지가 있는 userId*/
public static final String USER_WITH_PROFILE_IMAGE_ID = "user_with_profile_image";

/*프로필 이미지가 없는 userId*/
public static final String NO_PROFILE_IMAGE_USER_ID = "no_profile_image_user";

/*프로필 이미지가 있는 email*/
public static final String USER_WITH_PROFILE_EMAIL = "user_with_profile_image@threadly.com";

/*프로필 이미지가 없는 email*/
public static final String NO_PROFILE_IMAGE_USER_EMAIL = "no_profile_image_user@threadly.com";

/*TEMPORARY 이미지 데이터*/
public static final Map<String, String> TEMPORARY_IMAGE = Map.of(
    "userProfileImageId", "temp-img-001",
    "userId", "user_with_profile_image",
    "storedFileName", "temp_1234abcd.webp",
    "imageUrl", "/images/temp_1234abcd.webp",
    "followStatus", "TEMPORARY"
);

/*CONFIRMED 이미지 데이터*/
public static final Map<String, String> CONFIRMED_IMAGE = Map.of(
    "userProfileImageId", "confirmed-img-001",
    "userId", "user_with_profile_image",
    "storedFileName", "confirmed_abcd5678.webp",
    "imageUrl", "/images/confirmed_abcd5678.webp",
    "followStatus", "CONFIRMED"
);

/*DELETED 이미지 데이터*/
public static final Map<String, String> DELETED_IMAGE = Map.of(
    "userProfileImageId", "deleted-img-001",
    "userId", "user_with_profile_image",
    "storedFileName", "deleted_ijkl9012.webp",
    "imageUrl", "/images/deleted_ijkl9012.webp",
    "followStatus", "DELETED"
);
```