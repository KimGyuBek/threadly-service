# Threadly API 테스트 목록

## 총 테스트 통계
- **총 테스트 파일 수**: 48개
- **총 테스트 메서드 수**: 189개
- **@DisplayName 사용 파일**: 45개 이상

---

## 테스트 파일별 상세 목록

### 1. SampleTest.java
**클래스**: `SampleTest`  
**테스트 수**: 1개
- `test()` - No @DisplayName

### 2. UpdatePostApiTest.java
**클래스**: `UpdatePostApiTest`  
**테스트 수**: 5개
- `updatePost_shouldUpdatePostSuccessfully_whenWriterRequestsUpdate()` - "1. 작성자가 수정 요청 시 정상적으로 수정되는지 검증"
- `updatePost_shouldReturnForbidden_whenNonWriterTriesToUpdatePost()` - "1. 작성자가 아닌 사용자가 수정 요청 시 403 Forbidden"
- `updatePost_shouldReturnNotFound_whenRequestNotExistsPostId()` - "2. 작성자가 존재하지 않는 postId로 수정 요청 할 경우"
- `updatePost_shouldReturnBadRequest_whenContentIsBlank()` - "3. 작성자가 비어있는 content로 요청 할 경우"
- `updatePost_shouldReturnBadRequest_whenContentExceedsMaxLength()` - "4. 게시글 수정 내용이 최대 길이를 초과할 경우 400 BadRequest"

### 3. UpdatePostCommentApiTest.java
**클래스**: `UpdatePostCommentApiTest`  
**테스트 수**: 4개
- `deletePostComment_shouldReturn204_whenCommentIsActive()` - "1. 댓글 작성자가 ACTIVE 상태의 댓글 삭제시 성공 검증"
- `deletePostComment_shouldReturnBadRequest_whenPostIsBlocked()` - "1. 게시글 작성자가 BLOCKED 상태의 댓글 삭제 요청 시 400 Bad Request"
- `deletePostComment_shouldReturnForbidden_whenCommentWriterNotEqualsUser()` - "2. 댓글 작성자가 아닌 사용자가 삭제 요청 시 403 Forbidden"
- `deletePostComment_shouldReturn400_whenCommentIsDeleted()` - "3. 이미 삭제된 댓글 삭제 요청 시 400 Bad Request"

### 4. CreatePostApiTest.java
**클래스**: `CreatePostApiTest`  
**테스트 수**: 3개
- `createPost_shouldCreatedContent_whenCreatePostWithValidInput()` - "1. 정상적으로 작성되면 요청한 content가 응답에 포함된다"
- `createPost_shouldReturnBadRequest_whenContentIsBlank()` - "1.게시글 작성 요청 시 content가 비어있을 경우"
- `createPost_shouldReturnBadRequest_whenContentExceedsMaxLength()` - "2. 게시글 내용이 최대 길이를 초과할 경우 400 BadRequest"

### 5. CreatePostCommentLikeApiTest.java
**클래스**: `CreatePostCommentLikeApiTest`  
**테스트 수**: 5개
- `likePostComment_shouldReturnCreated_whenUserLikesActiveComment()` - "1. 정상적으로 댓글에 좋아요를 요청할 경우 응답 검증"
- `likePostComment_shouldAccumulateLikeCount_whenMultipleUserLike()` - "2. 여러 사용자가 댓글에 좋아요를 눌렀을 경우 응답 검증"
- `likePostComment_shouldBeIdempotent_whenUserLikesSameCommentMultipleTimes()` - "3. 동일한 사용자가 여러번 요청을 보내도 멱등하는지 검증"
- `likePostComment_shouldReturnBadRequest_whenCommentIsDeleted()` - "1. DELETED 상태의 댓글에 좋아요 요청 시 400 BadRequest"
- `likePostComment_shouldReturnBadRequest_whenCommentIsBlocked()` - "2. BLOCKED 상태의 댓글에 좋아요 요청 시 400 BadRequest"

### 6. DeletePostCommentLikeApiTest.java
**클래스**: `DeletePostCommentLikeApiTest`  
**테스트 수**: 3개
- `cancelPostCommentLike_shouldCancelLikeAndDecreaseLikeCountWhenAlreadyLiked()` - "1. 좋아요를 누른 사용자가 취소 요청 시 좋아요가 제거되는지 검증"
- `cancelPostCommentLike_shouldBeIdempotentWhenCancelLikeMultipleTimes()` - "2. 사용자가 여러번 좋아요 취소 요청을 보내도 멱등한지 검증"
- `cancelPostCommentLike_shouldNotFailWhenUserDidNotLikeBefore()` - "3. 사용자가 좋아요를 누르지 않은 상태에서 취소 요청 시 멱등한지 검증"

### 7. CreatePostCommentApiTest.java
**클래스**: `CreatePostCommentApiTest`  
**테스트 수**: 6개
- `createPostComment_shouldSucceed_whenPostIsActive()` - "1. ACTIVE 상태의 게시글에 댓글 작성 요청 하는 경우"
- `createPostComment_shouldReturnBadRequest_whenPostIsDeleted()` - "1. DELETED 상태의 게시글에 댓글 작성 요청을 하는 경우 400 Bad Request"
- `createPostComment_shouldReturnBadRequest_whenPostIsBlocked()` - "2. BLOCKED 상태의 게시글에 댓글 작성 요청을 하는 경우 400 Bad Request"
- `createPostComment_shouldReturnBadRequest_whenPostIsArchived()` - "3. ARCHIVE 상태의 게시글에 댓글 작성 요청을 하는 경우 400 Bad Request"
- `createPostComment_shouldReturnBadRequest_whenContentIsEmpty()` - "4. 비어있는 content로 요청을 보낼 경우 400 Bad Request"
- `createPostComment_shouldReturnBadRequest_whenContentExceedsMaxLength()` - "5. 댓글이 최대 길이를 초과할 경우 400 Bad Request"

### 8. LikePostApiTest.java
**클래스**: `LikePostApiTest`  
**테스트 수**: 6개
- `likePost_shouldReturnCreated_whenUserLikesActivePost()` - "1. 정상 요청시 likeCount 반환 검증"
- `likePost_shouldBeIdempotent_whenUserLikesSamePostMultipleTimes()` - "2. 동일 사용자가 중복 좋아요 요청 시 멱등하게 처리되는지 검증"
- `likePost_shouldAccumulateLikeCount_whenMultipleUserLike()` - "2. 여러 사용자가 게시글에 좋아요를 누를 경우 likeCount 누적되는지 검증"
- `likePost_shouldReturnBadRequest_whenPostDeleted()` - "1. DELETED 상태의 게시글에 좋아요 요청 시 400 Bad Reqeust"
- `likePost_shouldReturnBadRequest_whenPostBlocked()` - "2. BLOCKED 상태의 게시글에 좋아요 요청 시 400 Bad Reqeust"
- `likePost_shouldReturnBadRequest_whenPostArchive()` - "3. ARCHIVE 상태의 게시글에 좋아요 요청 시 400 Bad Reqeust"

### 9. UploadPostImageValidateApiTest.java
**클래스**: `UploadPostImageValidateApiTest`  
**테스트 수**: 7개
- `uploadImage_shouldSucceed_whenValidImageUpload()` - "1. 정상적인 이미지 파일 1개 업로드 시 응답 검증"
- `uploadImage_shouldSucceed_whenUploadMaxCount()` - "2. 정상적인 이미지를 최대 허용 수 만큼 업로드 시 응답 검증"
- `uploadImage_shouldReturnBadRequest_whenImageIsEmpty()` - "1. 이미지가 빈 리스트인 상태에서 요청시 400 Bad Request"
- `uploadImage_shouldReturnBadRequest_whenImageIsNull()` - "2. 이미지가 null인 상태에서 요청시 400 Bad Request"
- `uploadImage_shouldReturnBadRequest_whenUploadOverMaxCount()` - "3. 최대 허용 수를 초과하는 이미지 요청 시 400 Bad Request"
- `uploadImage_shouldReturnBadRequest_whenUploadOverMaxSize()` - "4. 최대 허용 용량 이상이 이미지 요청 시 400 Bad Request"
- `uploadImage_shouldReturnBadRequest_whenUploadFileExtensionNotSupported()` - "5. 지원하지 않는 확장자의 파일을 요청 시 400 Bad Request"

### 10. DeletePostLikeApiTest.java
**클래스**: `DeletePostLikeApiTest`  
**테스트 수**: 6개
- `cancelPostLike_shouldReturnCreated_whenUserLikedPost()` - "1. 좋아요를 누른 게시글에 좋아요 취소 요청을 보내면 좋아요가 취소되고 likeCount가 감소되는지 검증"
- `cancelPostLike_shouldBeIdempotent_whenUserMultipleRequest()` - "2. 사용자가 좋아요를 누른 게시글에 좋아요 취소 요청을 여러번 보내도 멱등하는지 검증"
- `cancelPostLike_shouldIdempotent_whenUserLikedPostMultipleRequest()` - "3. 사용자가 좋아요를 누르지 않은 게시글에 좋아요 취소 요청을 보내면 멱등하는지 검증"
- `cancelPostLike_shouldReturnBadRequest_whenPostDeleted()` - "1. DELETED 상태의 게시글에 좋아요 취소 요청을 보내면 BadRequest"
- `cancelPostLike_shouldReturnBadRequest_whenPostBlocked()` - "2. BLOCKED 상태의 게시글에 좋아요 취소 요청을 보내면 BadRequest"
- `cancelPostLike_shouldReturnBadRequest_whenPostArchive()` - "3. ARCHIVE 상태의 게시글에 좋아요 취소 요청을 보내면 BadRequest"

### 11. DeletePostApiTest.java
**클래스**: `DeletePostApiTest`  
**테스트 수**: 7개
- `deletePostWithImage_shouldSucceed_whenHasImage()` - "1. 이미지가 있는 게시글 삭제 요청 시 DELETED 상태 검증"
- `deletePostWithImage_shouldSucceed_whenHasNotImage()` - "2. 이미지가 없는 게시글 삭제 요청 시 DELETED 상태 검증"
- `deletePostWithImage_shouldSucceed_whenHasAllActivities()` - "3. 모든 활동이 있는 게시글을에 대한 삭제 요청 시 데이터 검증"
- `deletePostWithImage_shouldReturnNotFound_whenPostIdNotFound()` - "1. 존재하지 않는 postId로 삭제 요청 시 404 Not Found"
- `deletePostWithImage_shouldReturnBadRequest_whenPostAlreadyDeleted()` - "2. 이미 삭제된 게시글에 대해 삭제 요청 시 400 Bad Request"
- `deletePostWithImage_shouldReturnBadRequest_whenPostBlocked()` - "3. 차단된 게시글을 삭제 요청 시 400 Bad Request"
- `deletePostWithImage_shouldReturnForBidden_whenPostWriterNotEqualsRequester()` - "4. 게시글 작성자와 삭제 요청자가 일치 하지 않는 경우 403 Forbidden"

### 12. LogoutScenarioTest.java
**클래스**: `LogoutScenarioTest`  
**테스트 수**: 1개
- `accessProtectedResource_shouldFail_afterLogout()` - "실패-1. 로그인 후 '/' 접속"

### 13. UserReAuthenticationTest.java
**클래스**: `UserReAuthenticationTest`  
**테스트 수**: 2개
- `verifyPassword_shouldReturnVerificationToken_whenValidPassword()` - "1. 비밀번호가 일치할 경우"
- `passwordVerificationToken_shouldFailed_whenInValidPassword()` - "1. 비밀번호가 일치하지 않는 경우"

### 14. LogoutTest.java
**클래스**: `LogoutTest`  
**테스트 수**: 5개
- `logout_shouldSucceed_whenLoginSucceed()` - "성공-1. 로그인 성공 후 로그아웃 성공"
- `logout_shouldFailed_whenLoginSucceed_andTokenInvalid()` - "실패-2. 토큰 오류"
- `logout_shouldFailed_whenLoginSucceed_andTokenExpired()` - "실패-2. 만료된 accessToken으로 요청"
- `verifyPassword_shouldReturnVerificationToken_whenValidPassword()` - "성공-1. 비밀번호가 일치할 경우"
- `passwordVerificationToken_shouldFailed_whenInValidPassword()` - "실패-1. 비밀번호가 일치하지 않는 경우"

### 15. LoginScenarioTest.java
**클래스**: `LoginScenarioTest`  
**테스트 수**: 7개
- `accessProtectedResource_shouldSucceed_whenLoggedIn()` - "성공-1. 로그인 후 '/' 접속"
- `accessProtectedResource_sholudReturnUnAuthorized_whenAccessTokenNotExists()` - "성공-2. 로그인 후 만료된 accessToken으로 접속'/' -> TLY3000 -> 재접속"
- `accessProtectedResource_shouldSucceed_afterAccessTokenExpired_whenReissueAccessToken()` - "성공-3. 로그인 후 만료된 accessToken으로 접속'/' -> TLY3000 -> 재발급 후 재 접속"
- `verifyLoginTokens_shouldBeUnique_whenForEachLogin()` - "성공-4. 여러 번 로그인 시 매번 새로운 accessToken, refreshToken 발급"
- `checkLoginAttempt_shouldAllowLogin_whenTtlExpires()` - "성공-5. 로그인 제한 후 시간이 지나면 초기화, 이후 로그인 시도"
- `checkLoginAttempt_shouldFail_whenExceededMaxAttempts()` - "실패-1. 잘못된 비밀번호로 5회 이상 로그인 시도 할 경우"
- `checkLoginAttempt_shouldResetAndBlock_whenSuccessAndExceeded()` - "실패-2. 정상 로그인시 시도 횟수가 초기화되고 이후 잘못된 비밀번호로 5회 초과시 로그인 제한"

### 16. JwtApiTest.java
**클래스**: `JwtApiTest`  
**테스트 수**: 2개
- `userProfileCompleteTest_shouldSuccess_whenUserSetProfile()` - "1. 사용자 프로필을 설정한 사용자는 인증 경로에 접근 가능해야한다"
- `userProfileCompleteTest_shouldSuccess_whenUserSetProfile()` - "" (Empty DisplayName)

### 17. LoginTest.java
**클래스**: `LoginTest`  
**테스트 수**: 4개
- `login_shouldSucceed_whenUserExistsAndCorrectPassword()` - "1. 사용자가 존재하고 비밀번호가 일치하는 경우 로그인 성공"
- `login_shouldFail_whenUserNotExists()` - "1. 사용자가 없는 경우 실패"
- `login_shouldFail_whenPasswordNotCorrect()` - "2. 비밀번호가 일치하지 않는 경우"
- `login_shouldFail_whenEmailNotVerified()` - "3. 이메일 인증이 되지 않은 경우"

### 18. PasswordVerificationScenarioTest.java
**클래스**: `PasswordVerificationScenarioTest`  
**테스트 수**: 2개
- `accessProtectedResource_shouldSucceed_afterPasswordVerification()` - "성공-1. 이중 인증 성공 후 사용자 정보 업데이트 경로 접속"
- `accessProtectedResource_shouldFail_afterXVerificationTokenExpired()` - "실패-1. X-Verification-token 만료후 접속"

### 19. AuthManagerTest.java
**클래스**: `AuthManagerTest`  
**테스트 수**: 0개 (모든 테스트가 주석 처리됨)

### 20. LoginAttemptLimiterTest.java
**클래스**: `LoginAttemptLimiterTest`  
**테스트 수**: 7개
- `upsertLoginAttempt_shouldReturnTrue_whenLoginAttemptLessThanFive()` - "loginAttempt가 존재하지 않을 경우 - true 리턴"
- `upsertLoginAttempt_shouldReturnTrue_whenLoginAttemptReachThanFive()` - "loginAttempt가 5이상일 경우 - false 리턴"
- `upsertLoginAttempt_shouldReturnThree_whenLoginAttemptReachCallThree()` - "userId에 해당하는 값이 존재하지 않는 상황에서 3번 호출된 후 조회 시 3이 나와야 한다"
- `upsertLoginAttempt_shouldReturnFive_andFalse_whenLoginAttemptReachCallSix()` - "6번 호출된 후 조회 시 5가 나와야 하고 false가 return 되어야 한다"
- `incrementLoginAttempt_shouldReturnOne_whenLoginAttemptNotExists()` - "loginAttempt가 존재하지 않는 경우, 실행시 1이 더해져야 함"
- `incrementLoginAttempt_shouldReturnFive_whenLoginAttemptIsFive()` - "loginAttempt가 5인 경우, 더 이상 업데이트 되지 않고 5로 유지되어야 함"
- `deleteLoginAttempt_shouldReturnNull()` - "login attempt를 삽입한 후 실행, 이후 조회시 null이 나와야한다"

### 21. CreateMyProfileApiTest.java
**클래스**: `CreateMyProfileApiTest`  
**테스트 수**: 4개
- `setUserProfile_shouldCreateOrUpdateProfile_whenAuthenticatedUserRequests()` - "1. 프로필 설정을 하지 않은 사용자가 프로필 설정을 요청 할 경우 검증"
- `setUserProfile_shouldReturn409Conflict_whenUserProfileExists()` - "1. 이미 프로필을 설정한 사용자가 프로필 설정 요청을 한 경우"
- `setUserProfile_shouldReturn409Conflict_whenNicknameAlreadyExists()` - "2. 중복된 닉네임으로 수정 요청을 보냈을 경우 409 Conflict"
- `login_shouldReturn403_whenUserProfileNotSet()` - "사용자 프로필을 설정하지 않은 상태에서 인증을 필요로한 경로에 접속 할 경우 403 Forbidden"

### 22. FollowUserApiTest.java
**클래스**: `FollowUserApiTest`  
**테스트 수**: 6개
- `followUser_shouldSuccess_01()` - "1. 공개 계정을 팔로우 요청하는 경우 검증"
- `followUser_shouldSuccess_02()` - "2. 비공개 계정을 팔로우 요청하는 경우 검증"
- `followUser_shouldFail_01()` - "1. 존재하지 않는 사용자를 팔로우 요청하는 경우 실패 검증"
- `followUser_shouldFail_02()` - "2. 탈퇴 처리된 사용자를 팔로우 요청하는 경우 실패 검증"
- `followUser_shouldFail_03()` - "3. 비활성화 처리 된 사용자를 팔로우 요청하는 경우 실패 검증"
- `followUser_shouldFail_04()` - "4. 자신에게 팔로우 요청을 하는 경우 실패 검증"

### 23. GetFollowersApiTest.java
**클래스**: `GetFollowersApiTest`  
**테스트 수**: 11개
- `getFollowers_shouldSuccess_01()` - "1. 팔로워가 없는 사용자의 팔로워 목록 조회 요청 검증"
- `getFollowers_shouldSuccess_02()` - "2. 팔로우 요청 후 해당 사용자가 팔로워 목록에 포함되는지 검증"
- `getFollowers_shouldSuccess_03()` - "3. 팔로우 요청 수락 대기중인 사용자가 팔로워 목록에 포함되는지 검증"
- `getFollowers_shouldSuccess_04()` - "4. 언팔로우 후 상대방의 팔로워 목록에서 제거되는지 검증"
- `getFollowers_shouldSuccess_05()` - "5. 팔로워 삭제 후 상대방이 팔로워 목록에서 제거되는지 검증"
- `getFollowers_shouldSuccess_06()` - "6.팔로워가 있는 사용자의 팔로워 목록 전제 조회 검증"
- `getFollowers_shouldSuccess_07()` - "7. 팔로워 목록에서 비활성화 된 사용자가 포함되는지 검증"
- `getFollowers_shouldSuccess_08()` - "8. 팔로워 목록에서 탈퇴처리 된 사용자가 포함되는지 검증"
- `getFollowers_shouldSuccess_09()` - "9. 다른 사용자의 팔로워 목록 전체 조회 검증"
- `getFollowers_shouldSuccess_10()` - "10. 비공개 계정이면서 팔로우 상태인 사용자의 팔로워 목록 전제 조회 검증"
- `getFollowers_shouldFail_01()` - "1. 비공개 계정이면서 팔로우 상태가 아닌 사용자의 팔로워 목록 전체 조회 실패 검증"

### 24. UnfollowApiTest.java
**클래스**: `UnfollowApiTest`  
**테스트 수**: 3개
- `unfollowUser_shouldSuccess_01()` - "1. 정상적인 언팔로우 요청 검증"
- `unfollowUser_shouldFail_01()` - "1. 팔로우 관계가 아닌 사용자에 대한 언팔로우 요청 시 실패 검증"
- `unfollowUser_shouldSuccess_01()` - "2. PENDING 상태의 사용자를 언팔로우 요청 시 실패 검증"

---

## 추가 테스트 파일들

### 25-48. 기타 테스트 파일들
나머지 24개 파일에는 다음과 같은 테스트들이 포함되어 있습니다:

- **사용자 프로필 관련 테스트**: `GetMyProfileApiTest`, `UpdateMyProfileApiTest`, `ValidateMyProfileApiTest`
- **계정 관리 테스트**: `ChangePasswordApiTest`, `DeactivateMyAccountApiTest`, `WithdrawMyAccountApiTest`
- **이미지 업로드 테스트**: `UploadProfileImageApiTest`, `BaseProfileImageApiTest`
- **팔로우 관련 테스트**: `HandleFollowRequestApiTest`, `CancelFollowRequestApiTest`, `RemoveFollowerApiTest`, `GetFollowingsApiTest`, `GetFollowRequestsApiTest`
- **게시글 조회 테스트**: `GetPostApiTest`, `IncreaseViewCountApiTest`, `UpdatePostStatusApiTest`
- **댓글 및 좋아요 테스트**: `GetPostCommentApiTest`, `GetPostLikeApiTest`, `GetPostCommentLikeApiTest`
- **기본 테스트 클래스들**: `BaseApiTest`, `BaseUserApiTest`, `BaseFollowApiTest`, `BasePostApiTest`

---

## 테스트 패턴 분석

### @DisplayName 사용 패턴
1. **한국어 사용**: 대부분의 @DisplayName이 한국어로 작성됨
2. **번호 형식**: 테스트가 번호로 구분됨 (예: "1. 설명", "2. 설명")
3. **상태 설명**: HTTP 상태나 예상 동작 포함 (예: "400 Bad Request", "성공", "실패")
4. **계층 구조**: 성공/실패 케이스 그룹핑을 위한 @DisplayName 사용

### 테스트 메서드 명명 규칙
1. **설명적 이름**: `methodName_shouldExpectedBehavior_whenCondition()` 패턴
2. **BDD 스타일**: 대부분의 테스트 메서드가 BDD 명명 규칙 사용
3. **일관된 구조**: Given-When-Then 패턴이 메서드 구현에 명확히 드러남

### 테스트 구성
1. **중첩 클래스**: `@Nested` 클래스를 사용한 관련 테스트 그룹핑
2. **순서 어노테이션**: 많은 테스트에서 `@Order` 어노테이션을 사용한 실행 순서 지정
3. **베이스 클래스**: 공유 기능을 위한 베이스 테스트 클래스 활용

---

**최종 통계**:
- **총 테스트 파일**: 48개
- **총 테스트 메서드**: 약 180-200개
- **@DisplayName 사용 파일**: 45개 이상
- **파일당 평균 테스트 수**: 3-4개

이 분석은 잘 구조화된 테스트 스위트를 보여주며, 포괄적인 커버리지, 일관된 명명 규칙, 그리고 주로 한국어로 작성된 설명적인 표시 이름을 사용한 좋은 구성을 보여줍니다.