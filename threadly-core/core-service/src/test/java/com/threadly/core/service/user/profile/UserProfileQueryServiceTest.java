package com.threadly.core.service.user.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.domain.follow.FollowStatus;
import com.threadly.core.domain.user.UserStatus;
import com.threadly.core.port.user.in.profile.query.dto.GetMyProfileDetailsApiResponse;
import com.threadly.core.port.user.in.profile.query.dto.GetUserProfileApiResponse;
import com.threadly.core.port.user.out.profile.UserProfileQueryPort;
import com.threadly.core.port.user.out.profile.projection.MyProfileDetailsProjection;
import com.threadly.core.port.user.out.profile.projection.UserProfileProjection;
import com.threadly.core.service.validator.follow.FollowAccessValidator;
import com.threadly.core.service.validator.user.UserProfileValidator;
import com.threadly.core.service.validator.user.UserValidator;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserProfileQueryQueryService 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class UserProfileQueryServiceTest {

  @InjectMocks
  private UserProfileQueryService userProfileQueryService;

  @Mock
  private FollowAccessValidator followAccessValidator;

  @Mock
  private UserValidator userValidator;

  @Mock
  private UserProfileValidator userProfileValidator;

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("닉네임 중복 검증")
  class ValidateNicknameUniqueTest {

    /*[Case #1] 닉네임 중복 검증 성공 - 사용 가능한 닉네임*/
    @Order(1)
    @DisplayName("1. 사용 가능한 닉네임일 경우 예외가 발생하지 않는지 검증")
    @Test
    void validateNicknameUnique_shouldPass_whenNicknameNotDuplicated() throws Exception {
      //given
      String nickname = "uniqueNickname";
      doNothing().when(userProfileValidator).validateNicknameDuplicate(nickname);

      //when
      userProfileQueryService.validateNicknameUnique(nickname);

      //then
      verify(userProfileValidator).validateNicknameDuplicate(nickname);
    }

    /*[Case #2] 닉네임 중복 검증 실패 - 중복된 닉네임*/
    @Order(2)
    @DisplayName("2. 중복된 닉네임일 경우 예외가 발생하는지 검증")
    @Test
    void validateNicknameUnique_shouldThrow_whenNicknameDuplicated() throws Exception {
      //given
      String nickname = "duplicatedNickname";
      doThrow(new UserException(ErrorCode.USER_NICKNAME_DUPLICATED))
          .when(userProfileValidator).validateNicknameDuplicate(nickname);

      //when & then
      assertThatThrownBy(() -> userProfileQueryService.validateNicknameUnique(nickname))
          .isInstanceOf(UserException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.USER_NICKNAME_DUPLICATED);
    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("사용자 프로필 조회")
  class GetUserProfileTest {

    /*[Case #1] 사용자 프로필 조회 성공*/
    @Order(1)
    @DisplayName("1. 사용자 프로필이 정상적으로 조회되는지 검증")
    @Test
    void getUserProfile_shouldReturnProfile_whenValidUser() throws Exception {
      //given
      String userId = "user-1";
      String targetUserId = "target-1";

      UserProfileProjection projection = new UserProfileProjection() {
        @Override
        public String getUserId() {
          return targetUserId;
        }

        @Override
        public String getNickname() {
          return "targetNickname";
        }

        @Override
        public String getProfileImageUrl() {
          return "/profile.png";
        }

        @Override
        public String getStatusMessage() {
          return "status message";
        }

        @Override
        public String getBio() {
          return "bio";
        }

        @Override
        public String getPhone() {
          return "010-1234-5678";
        }

        @Override
        public UserStatus getUserStatus() {
          return UserStatus.ACTIVE;
        }

        @Override
        public boolean getIsPrivate() {
          return false;
        }
      };

      when(userProfileValidator.getUserProfileProjectionOrElseThrow(targetUserId))
          .thenReturn(projection);
      doNothing().when(userValidator).validateUserStatusWithException(UserStatus.ACTIVE);
      when(followAccessValidator.validateProfileAccessible(userId, targetUserId))
          .thenReturn(FollowStatus.APPROVED);

      //when
      GetUserProfileApiResponse response = userProfileQueryService.getUserProfile(userId,
          targetUserId);

      //then
      verify(userProfileValidator).getUserProfileProjectionOrElseThrow(targetUserId);
      verify(userValidator).validateUserStatusWithException(UserStatus.ACTIVE);
      verify(followAccessValidator).validateProfileAccessible(userId, targetUserId);

      assertThat(response.user().userId()).isEqualTo(targetUserId);
      assertThat(response.user().nickname()).isEqualTo("targetNickname");
      assertThat(response.user().profileImageUrl()).isEqualTo("/profile.png");
      assertThat(response.statusMessage()).isEqualTo("status message");
      assertThat(response.bio()).isEqualTo("bio");
      assertThat(response.followStatus()).isEqualTo(FollowStatus.APPROVED);
    }

    /*[Case #2] 사용자 프로필 조회 - 프로필 이미지가 없는 경우*/
    @Order(2)
    @DisplayName("2. 프로필 이미지가 없을 경우 기본값이 설정되는지 검증")
    @Test
    void getUserProfile_shouldUseDefaultImage_whenProfileImageNull() throws Exception {
      //given
      String userId = "user-1";
      String targetUserId = "target-1";

      UserProfileProjection projection = new UserProfileProjection() {
        @Override
        public String getUserId() {
          return targetUserId;
        }

        @Override
        public String getNickname() {
          return "targetNickname";
        }

        @Override
        public String getProfileImageUrl() {
          return null;
        }

        @Override
        public String getStatusMessage() {
          return "status message";
        }

        @Override
        public String getBio() {
          return "bio";
        }

        @Override
        public String getPhone() {
          return "010-1234-5678";
        }

        @Override
        public UserStatus getUserStatus() {
          return UserStatus.ACTIVE;
        }

        @Override
        public boolean getIsPrivate() {
          return false;
        }
      };

      when(userProfileValidator.getUserProfileProjectionOrElseThrow(targetUserId))
          .thenReturn(projection);
      doNothing().when(userValidator).validateUserStatusWithException(UserStatus.ACTIVE);
      when(followAccessValidator.validateProfileAccessible(userId, targetUserId))
          .thenReturn(FollowStatus.NONE);

      //when
      GetUserProfileApiResponse response = userProfileQueryService.getUserProfile(userId,
          targetUserId);

      //then
      assertThat(response.user().profileImageUrl()).isEqualTo("/");
    }

    /*[Case #3] 사용자 프로필 조회 실패 - 사용자가 존재하지 않는 경우*/
    @Order(3)
    @DisplayName("3. 사용자가 존재하지 않을 경우 예외가 발생하는지 검증")
    @Test
    void getUserProfile_shouldThrow_whenUserNotFound() throws Exception {
      //given
      String userId = "user-1";
      String targetUserId = "nonexistent-user";

      when(userProfileValidator.getUserProfileProjectionOrElseThrow(targetUserId))
          .thenThrow(new UserException(ErrorCode.USER_NOT_FOUND));

      //when & then
      assertThatThrownBy(
          () -> userProfileQueryService.getUserProfile(userId, targetUserId))
          .isInstanceOf(UserException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    /*[Case #4] 사용자 프로필 조회 실패 - 사용자 상태가 유효하지 않은 경우*/
    @Order(4)
    @DisplayName("4. 사용자 상태가 유효하지 않을 경우 예외가 발생하는지 검증")
    @Test
    void getUserProfile_shouldThrow_whenUserStatusInvalid() throws Exception {
      //given
      String userId = "user-1";
      String targetUserId = "target-1";

      UserProfileProjection projection = new UserProfileProjection() {
        @Override
        public String getUserId() {
          return targetUserId;
        }

        @Override
        public String getNickname() {
          return "targetNickname";
        }

        @Override
        public String getProfileImageUrl() {
          return null;
        }

        @Override
        public String getStatusMessage() {
          return "status message";
        }

        @Override
        public String getBio() {
          return "bio";
        }

        @Override
        public String getPhone() {
          return "010-1234-5678";
        }

        @Override
        public UserStatus getUserStatus() {
          return UserStatus.DELETED;
        }

        @Override
        public boolean getIsPrivate() {
          return false;
        }
      };

      when(userProfileValidator.getUserProfileProjectionOrElseThrow(targetUserId))
          .thenReturn(projection);
      doThrow(new UserException(ErrorCode.USER_ALREADY_DELETED))
          .when(userValidator).validateUserStatusWithException(UserStatus.DELETED);

      //when & then
      assertThatThrownBy(
          () -> userProfileQueryService.getUserProfile(userId, targetUserId))
          .isInstanceOf(UserException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.USER_ALREADY_DELETED);
    }
  }

  @Order(3)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("내 프로필 상세 조회")
  class GetMyProfileDetailsTest {

    /*[Case #1] 내 프로필 상세 조회 성공*/
    @Order(1)
    @DisplayName("1. 내 프로필 상세 정보가 정상적으로 조회되는지 검증")
    @Test
    void getMyProfileDetails_shouldReturnDetails_whenValidUser() throws Exception {
      //given
      String userId = "user-1";

      MyProfileDetailsProjection projection = new MyProfileDetailsProjection() {
        @Override
        public String getNickname() {
          return "myNickname";
        }

        @Override
        public String getStatusMessage() {
          return "my status";
        }

        @Override
        public String getBio() {
          return "my bio";
        }

        @Override
        public String getPhone() {
          return "010-1234-5678";
        }

        @Override
        public String getProfileImageId() {
          return "image-1";
        }

        @Override
        public String getProfileImageUrl() {
          return "/my-profile.png";
        }

        @Override
        public UserStatus getStatus() {
          return UserStatus.ACTIVE;
        }
      };

      when(userProfileValidator.getMyProfileDetailsProjectionOrElseThrow(userId))
          .thenReturn(projection);
      doNothing().when(userValidator).validateMyStatusWithException(UserStatus.ACTIVE);

      //when
      GetMyProfileDetailsApiResponse response = userProfileQueryService.getMyProfileDetails(
          userId);

      //then
      verify(userProfileValidator).getMyProfileDetailsProjectionOrElseThrow(userId);
      verify(userValidator).validateMyStatusWithException(UserStatus.ACTIVE);

      assertThat(response.userId()).isEqualTo(userId);
      assertThat(response.nickname()).isEqualTo("myNickname");
      assertThat(response.statusMessage()).isEqualTo("my status");
      assertThat(response.bio()).isEqualTo("my bio");
      assertThat(response.phone()).isEqualTo("010-1234-5678");
      assertThat(response.profileImageId()).isEqualTo("image-1");
      assertThat(response.profileImageUrl()).isEqualTo("/my-profile.png");
    }

    /*[Case #2] 내 프로필 상세 조회 실패 - 사용자가 존재하지 않는 경우*/
    @Order(2)
    @DisplayName("2. 사용자가 존재하지 않을 경우 예외가 발생하는지 검증")
    @Test
    void getMyProfileDetails_shouldThrow_whenUserNotFound() throws Exception {
      //given
      String userId = "nonexistent-user";

      when(userProfileValidator.getMyProfileDetailsProjectionOrElseThrow(userId))
          .thenThrow(new UserException(ErrorCode.USER_NOT_FOUND));

      //when & then
      assertThatThrownBy(() -> userProfileQueryService.getMyProfileDetails(userId))
          .isInstanceOf(UserException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    /*[Case #3] 내 프로필 상세 조회 실패 - 사용자 상태가 유효하지 않은 경우*/
    @Order(3)
    @DisplayName("3. 사용자 상태가 유효하지 않을 경우 예외가 발생하는지 검증")
    @Test
    void getMyProfileDetails_shouldThrow_whenUserStatusInvalid() throws Exception {
      //given
      String userId = "user-1";

      MyProfileDetailsProjection projection = new MyProfileDetailsProjection() {
        @Override
        public String getNickname() {
          return "myNickname";
        }

        @Override
        public String getStatusMessage() {
          return "my status";
        }

        @Override
        public String getBio() {
          return "my bio";
        }

        @Override
        public String getPhone() {
          return "010-1234-5678";
        }

        @Override
        public String getProfileImageId() {
          return "image-1";
        }

        @Override
        public String getProfileImageUrl() {
          return "/my-profile.png";
        }

        @Override
        public UserStatus getStatus() {
          return UserStatus.DELETED;
        }
      };

      when(userProfileValidator.getMyProfileDetailsProjectionOrElseThrow(userId))
          .thenReturn(projection);
      doThrow(new UserException(ErrorCode.USER_ALREADY_DELETED))
          .when(userValidator).validateMyStatusWithException(UserStatus.DELETED);

      //when & then
      assertThatThrownBy(() -> userProfileQueryService.getMyProfileDetails(userId))
          .isInstanceOf(UserException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.USER_ALREADY_DELETED);
    }
  }
}
