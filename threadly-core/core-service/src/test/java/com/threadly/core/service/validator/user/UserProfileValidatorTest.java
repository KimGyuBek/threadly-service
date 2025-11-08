package com.threadly.core.service.validator.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.port.user.out.profile.UserProfileQueryPort;
import com.threadly.core.port.user.out.profile.projection.MyProfileDetailsProjection;
import com.threadly.core.port.user.out.profile.projection.UserProfileProjection;
import java.util.Optional;
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
 * UserProfileValidator 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class UserProfileValidatorTest {

  @InjectMocks
  private UserProfileValidator userProfileValidator;

  @Mock
  private UserProfileQueryPort userProfileQueryPort;

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 닉네임 중복 검증 성공 - 중복 없음*/
    @Order(1)
    @DisplayName("1. 닉네임이 중복되지 않은 경우 예외가 발생하지 않는지 검증")
    @Test
    void validateNicknameDuplicate_shouldPass_whenNicknameNotDuplicated() throws Exception {
      //given
      String nickname = "uniqueNickname";
      when(userProfileQueryPort.existsByNickname(nickname)).thenReturn(false);

      //when & then
      assertThatCode(() -> userProfileValidator.validateNicknameDuplicate(nickname))
          .doesNotThrowAnyException();
      verify(userProfileQueryPort).existsByNickname(nickname);
    }

    /*[Case #2] UserProfileProjection 조회 성공*/
    @Order(2)
    @DisplayName("2. userId로 UserProfileProjection 조회 시 정상적으로 반환되는지 검증")
    @Test
    void getUserProfileProjectionOrElseThrow_shouldReturnProjection_whenUserExists()
        throws Exception {
      //given
      String userId = "user-1";
      UserProfileProjection projection = new UserProfileProjection() {
        @Override
        public String getUserId() {
          return userId;
        }

        @Override
        public String getNickname() {
          return "testNickname";
        }

        @Override
        public String getStatusMessage() {
          return "status message";
        }

        @Override
        public String getBio() {
          return "test bio";
        }

        @Override
        public String getPhone() {
          return "010-1234-5678";
        }

        @Override
        public String getProfileImageUrl() {
          return "/profile.jpg";
        }

        @Override
        public com.threadly.core.domain.user.UserStatus getUserStatus() {
          return com.threadly.core.domain.user.UserStatus.ACTIVE;
        }

        @Override
        public boolean getIsPrivate() {
          return false;
        }
      };

      when(userProfileQueryPort.findUserProfileByUserId(userId))
          .thenReturn(Optional.of(projection));

      //when
      UserProfileProjection result = userProfileValidator.getUserProfileProjectionOrElseThrow(
          userId);

      //then
      assertThat(result).isNotNull();
      assertThat(result.getUserId()).isEqualTo(userId);
      verify(userProfileQueryPort).findUserProfileByUserId(userId);
    }

    /*[Case #3] MyProfileDetailsProjection 조회 성공*/
    @Order(3)
    @DisplayName("3. userId로 MyProfileDetailsProjection 조회 시 정상적으로 반환되는지 검증")
    @Test
    void getMyProfileDetailsProjectionOrElseThrow_shouldReturnProjection_whenUserExists()
        throws Exception {
      //given
      String userId = "user-1";
      MyProfileDetailsProjection projection = new MyProfileDetailsProjection() {
        @Override
        public String getNickname() {
          return "testNickname";
        }

        @Override
        public String getStatusMessage() {
          return "status message";
        }

        @Override
        public String getBio() {
          return "test bio";
        }

        @Override
        public String getPhone() {
          return "010-1234-5678";
        }

        @Override
        public com.threadly.core.domain.user.UserStatus getStatus() {
          return com.threadly.core.domain.user.UserStatus.ACTIVE;
        }

        @Override
        public String getProfileImageId() {
          return "image-1";
        }

        @Override
        public String getProfileImageUrl() {
          return "/profile.jpg";
        }
      };

      when(userProfileQueryPort.findMyProfileDetailsByUserId(userId))
          .thenReturn(Optional.of(projection));

      //when
      MyProfileDetailsProjection result = userProfileValidator.getMyProfileDetailsProjectionOrElseThrow(
          userId);

      //then
      assertThat(result).isNotNull();
      assertThat(result.getNickname()).isEqualTo("testNickname");
      assertThat(result.getPhone()).isEqualTo("010-1234-5678");
      verify(userProfileQueryPort).findMyProfileDetailsByUserId(userId);
    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Case #1] 닉네임 중복 검증 실패 - 중복 있음*/
    @Order(1)
    @DisplayName("1. 닉네임이 중복된 경우 예외가 발생하는지 검증")
    @Test
    void validateNicknameDuplicate_shouldThrow_whenNicknameIsDuplicated() throws Exception {
      //given
      String nickname = "duplicateNickname";
      when(userProfileQueryPort.existsByNickname(nickname)).thenReturn(true);

      //when & then
      assertThatThrownBy(() -> userProfileValidator.validateNicknameDuplicate(nickname))
          .isInstanceOf(UserException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.USER_NICKNAME_DUPLICATED);
      verify(userProfileQueryPort).existsByNickname(nickname);
    }

    /*[Case #2] UserProfileProjection 조회 실패 - 사용자 미존재*/
    @Order(2)
    @DisplayName("2. userId로 UserProfileProjection 조회 시 사용자가 존재하지 않으면 예외 발생")
    @Test
    void getUserProfileProjectionOrElseThrow_shouldThrow_whenUserNotExists() throws Exception {
      //given
      String userId = "nonexistent-user";
      when(userProfileQueryPort.findUserProfileByUserId(userId)).thenReturn(Optional.empty());

      //when & then
      assertThatThrownBy(() -> userProfileValidator.getUserProfileProjectionOrElseThrow(userId))
          .isInstanceOf(UserException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.USER_NOT_FOUND);
      verify(userProfileQueryPort).findUserProfileByUserId(userId);
    }

    /*[Case #3] MyProfileDetailsProjection 조회 실패 - 사용자 미존재*/
    @Order(3)
    @DisplayName("3. userId로 MyProfileDetailsProjection 조회 시 사용자가 존재하지 않으면 예외 발생")
    @Test
    void getMyProfileDetailsProjectionOrElseThrow_shouldThrow_whenUserNotExists() throws Exception {
      //given
      String userId = "nonexistent-user";
      when(userProfileQueryPort.findMyProfileDetailsByUserId(userId)).thenReturn(
          Optional.empty());

      //when & then
      assertThatThrownBy(
          () -> userProfileValidator.getMyProfileDetailsProjectionOrElseThrow(userId))
          .isInstanceOf(UserException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.USER_NOT_FOUND);
      verify(userProfileQueryPort).findMyProfileDetailsByUserId(userId);
    }
  }
}
