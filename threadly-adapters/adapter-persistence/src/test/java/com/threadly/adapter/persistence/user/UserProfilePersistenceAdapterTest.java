package com.threadly.adapter.persistence.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.threadly.BasePersistenceTest;
import com.threadly.core.domain.user.User;
import com.threadly.core.domain.user.UserGenderType;
import com.threadly.core.domain.user.UserRoleType;
import com.threadly.core.domain.user.UserStatus;
import com.threadly.core.domain.user.profile.UserProfile;
import com.threadly.core.domain.user.profile.UserProfileType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.ClassOrderer;

/**
 * UserProfilePersistenceAdapter 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class UserProfilePersistenceAdapterTest extends BasePersistenceTest {

  private static final String TEST_PROFILE_USER_ID = "test-profile-user-id";
  private static final String TEST_NICKNAME = "테스트닉네임";
  private static final String TEST_STATUS_MESSAGE = "테스트 상태 메시지";
  private static final String TEST_BIO = "테스트 바이오";

  /**
   * 프로필이 없는 사용자 생성
   */
  private User createUserWithoutProfile() {
    User user = User.builder()
        .userId(TEST_PROFILE_USER_ID)
        .userName("프로필테스트유저")
        .password("password123")
        .email("profile@example.com")
        .phone("010-1234-5678")
        .userRoleType(UserRoleType.USER)
        .userStatus(UserStatus.ACTIVE)
        .isEmailVerified(true)
        .isPrivate(false)
        .build();

    userPersistenceAdapter.save(user);
    entityManager.flush();
    entityManager.clear();
    return user;
  }

  @Order(1)
  @DisplayName("프로필 저장 테스트")
  @Nested
  class SaveUserProfileTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 프로필이 정상적으로 저장된다")
      @Test
      void saveUserProfile_shouldSaveProfile_whenValid() {
        //given
        createUserWithoutProfile();
        UserProfile profile = UserProfile.builder()
            .userId(TEST_PROFILE_USER_ID)
            .nickname(TEST_NICKNAME)
            .statusMessage(TEST_STATUS_MESSAGE)
            .bio(TEST_BIO)
            .genderType(UserGenderType.MALE)
            .userProfileType(UserProfileType.USER)
            .build();

        //when
        userProfilePersistenceAdapter.saveUserProfile(profile);
        entityManager.flush();
        entityManager.clear();

        //then
        boolean exists = userProfilePersistenceAdapter.existsUserProfileByUserId(
            TEST_PROFILE_USER_ID);
        assertThat(exists).isTrue();
      }
    }
  }

  @Order(2)
  @DisplayName("프로필 존재 여부 확인 테스트")
  @Nested
  class ExistsUserProfileByUserIdTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 프로필이 존재하면 true를 반환한다")
      @Test
      void existsUserProfileByUserId_shouldReturnTrue_whenProfileExists() {
        //given
        createUser(TEST_PROFILE_USER_ID, "profile@example.com", TEST_NICKNAME);

        //when
        boolean exists = userProfilePersistenceAdapter.existsUserProfileByUserId(
            TEST_PROFILE_USER_ID);

        //then
        assertThat(exists).isTrue();
      }

      @DisplayName("2. 프로필이 존재하지 않으면 false를 반환한다")
      @Test
      void existsUserProfileByUserId_shouldReturnFalse_whenProfileDoesNotExist() {
        //given
        createUserWithoutProfile();

        //when
        boolean exists = userProfilePersistenceAdapter.existsUserProfileByUserId(
            TEST_PROFILE_USER_ID);

        //then
        assertThat(exists).isFalse();
      }
    }
  }

  @Order(3)
  @DisplayName("닉네임 중복 확인 테스트")
  @Nested
  class ExistsByNicknameTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 닉네임이 존재하면 true를 반환한다")
      @Test
      void existsByNickname_shouldReturnTrue_whenNicknameExists() {
        //given
        createUser(TEST_PROFILE_USER_ID, "profile@example.com", TEST_NICKNAME);

        //when
        boolean exists = userProfilePersistenceAdapter.existsByNickname(TEST_NICKNAME);

        //then
        assertThat(exists).isTrue();
      }

      @DisplayName("2. 닉네임이 존재하지 않으면 false를 반환한다")
      @Test
      void existsByNickname_shouldReturnFalse_whenNicknameDoesNotExist() {
        //given
        String nonExistentNickname = "존재하지않는닉네임";

        //when
        boolean exists = userProfilePersistenceAdapter.existsByNickname(nonExistentNickname);

        //then
        assertThat(exists).isFalse();
      }
    }
  }

  @Order(4)
  @DisplayName("프로필 업데이트 테스트")
  @Nested
  class UpdateMyProfileTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 프로필이 정상적으로 업데이트된다")
      @Test
      void updateMyProfile_shouldUpdateProfile_whenProfileExists() {
        //given
        createUser(TEST_PROFILE_USER_ID, "profile@example.com", TEST_NICKNAME);

        String newNickname = "변경된닉네임";
        String newStatusMessage = "변경된 상태 메시지";
        String newBio = "변경된 바이오";

        UserProfile updatedProfile = UserProfile.builder()
            .userId(TEST_PROFILE_USER_ID)
            .nickname(newNickname)
            .statusMessage(newStatusMessage)
            .bio(newBio)
            .genderType(UserGenderType.MALE)
            .userProfileType(UserProfileType.USER)
            .build();

        //when
        userProfilePersistenceAdapter.updateMyProfile(updatedProfile);
        entityManager.flush();
        entityManager.clear();

        //then
        // 닉네임이 변경되었는지 확인
        boolean nicknameExists = userProfilePersistenceAdapter.existsByNickname(newNickname);
        assertThat(nicknameExists).isTrue();
      }
    }
  }
}
