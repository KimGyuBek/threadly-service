package com.threadly.adapter.persistence.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.threadly.BasePersistenceTest;
import com.threadly.core.domain.user.User;
import com.threadly.core.domain.user.UserRoleType;
import com.threadly.core.domain.user.UserStatus;
import com.threadly.core.port.user.out.UserResult;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.ClassOrderer;

/**
 * UserPersistenceAdapter 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class UserPersistenceAdapterTest extends BasePersistenceTest {

  @Order(1)
  @DisplayName("사용자 저장 테스트")
  @Nested
  class SaveUserTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 사용자 저장 시 userId가 정상적으로 반환된다")
      @Test
      void saveUser_shouldReturnUserId_whenUserIsValid() {
        //given
        String userId = "new-user-id";
        String email = "newuser@example.com";
        User user = User.builder()
            .userId(userId)
            .userName("새사용자")
            .password("password123")
            .email(email)
            .phone("010-9999-9999")
            .userRoleType(UserRoleType.USER)
            .userStatus(UserStatus.ACTIVE)
            .isEmailVerified(false)
            .isPrivate(false)
            .build();

        //when
        UserResult result = userPersistenceAdapter.save(user);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getEmail()).isEqualTo(email);
      }
    }
  }

  @Order(2)
  @DisplayName("이메일로 사용자 조회 테스트")
  @Nested
  class FindByEmailTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 존재하는 이메일로 조회 시 사용자가 반환된다")
      @Test
      void findByEmail_shouldReturnUser_whenEmailExists() {
        //given
        createTestUser();
        String email = TEST_USER_EMAIL;

        //when
        Optional<User> result = userPersistenceAdapter.findByEmail(email);

        //then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
        assertThat(result.get().getUserName()).isEqualTo(TEST_USER_NAME);
      }
    }

    @DisplayName("실패")
    @Nested
    class Fail {

      @DisplayName("1. 존재하지 않는 이메일로 조회 시 빈 Optional이 반환된다")
      @Test
      void findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
        //given
        String nonExistentEmail = "nonexistent@example.com";

        //when
        Optional<User> result = userPersistenceAdapter.findByEmail(nonExistentEmail);

        //then
        assertThat(result).isEmpty();
      }
    }
  }

  @Order(3)
  @DisplayName("사용자 ID로 조회 테스트")
  @Nested
  class FindByUserIdTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 존재하는 userId로 조회 시 사용자가 반환된다")
      @Test
      void findByUserId_shouldReturnUser_whenUserIdExists() {
        //given
        createTestUser();
        String userId = TEST_USER_ID;

        //when
        Optional<User> result = userPersistenceAdapter.findByUserId(userId);

        //then
        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(userId);
        assertThat(result.get().getEmail()).isEqualTo(TEST_USER_EMAIL);
      }
    }

    @DisplayName("실패")
    @Nested
    class Fail {

      @DisplayName("1. 존재하지 않는 userId로 조회 시 빈 Optional이 반환된다")
      @Test
      void findByUserId_shouldReturnEmpty_whenUserIdDoesNotExist() {
        //given
        String nonExistentUserId = "non-existent-user-id";

        //when
        Optional<User> result = userPersistenceAdapter.findByUserId(nonExistentUserId);

        //then
        assertThat(result).isEmpty();
      }
    }
  }

  @Order(4)
  @DisplayName("이메일 인증 상태 업데이트 테스트")
  @Nested
  class UpdateEmailVerificationTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 이메일 인증 상태가 정상적으로 업데이트된다")
      @Test
      void updateEmailVerification_shouldUpdateStatus_whenUserExists() {
        //given
        createTestUser();
        String userId = TEST_USER_ID;

        //when
        userPersistenceAdapter.updateEmailVerification(userId, true);
        entityManager.flush();
        entityManager.clear();

        //then
        Optional<User> result = userPersistenceAdapter.findByUserId(userId);
        assertThat(result).isPresent();
        assertThat(result.get().isEmailVerified()).isTrue();
      }
    }
  }

  @Order(5)
  @DisplayName("사용자 상태 업데이트 테스트")
  @Nested
  class UpdateUserStatusTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 사용자 상태가 INACTIVE로 정상적으로 변경된다")
      @Test
      void updateUserStatus_shouldChangeStatus_whenUserExists() {
        //given
        createTestUser();
        String userId = TEST_USER_ID;
        UserStatus newStatus = UserStatus.INACTIVE;

        //when
        userPersistenceAdapter.updateUserStatus(userId, newStatus);
        entityManager.flush();
        entityManager.clear();

        //then
        Optional<User> result = userPersistenceAdapter.findByUserId(userId);
        assertThat(result).isPresent();
        assertThat(result.get().getUserStatus()).isEqualTo(newStatus);
      }
    }
  }
}
