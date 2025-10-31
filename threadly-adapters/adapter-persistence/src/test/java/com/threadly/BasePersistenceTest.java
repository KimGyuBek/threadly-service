package com.threadly;

import static org.assertj.core.api.Assertions.assertThat;

import com.threadly.adapter.persistence.user.adapter.UserPersistenceAdapter;
import com.threadly.adapter.persistence.user.adapter.UserProfilePersistenceAdapter;
import com.threadly.core.domain.user.User;
import com.threadly.core.domain.user.UserGenderType;
import com.threadly.core.domain.user.UserRoleType;
import com.threadly.core.domain.user.UserStatus;
import com.threadly.core.domain.user.profile.UserProfile;
import com.threadly.core.domain.user.profile.UserProfileType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence 테스트 베이스 클래스
 */
@SpringBootTest(classes = PersistenceTestApplication.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public abstract class BasePersistenceTest {

  @Autowired
  protected UserPersistenceAdapter userPersistenceAdapter;

  @Autowired
  protected UserProfilePersistenceAdapter userProfilePersistenceAdapter;

  @PersistenceContext
  protected EntityManager entityManager;

  protected static final String TEST_USER_ID = "test-user-id-1";
  protected static final String TEST_USER_EMAIL = "test@example.com";
  protected static final String TEST_USER_PASSWORD = "password123";
  protected static final String TEST_USER_NAME = "테스트유저";
  protected static final String TEST_USER_PHONE = "010-1234-5678";
  protected static final String TEST_USER_NICKNAME = "테스트닉네임";

  /**
   * 기본 테스트 사용자 생성
   */
  @Transactional
  protected User createTestUser() {
    User user = User.builder()
        .userId(TEST_USER_ID)
        .userName(TEST_USER_NAME)
        .password(TEST_USER_PASSWORD)
        .email(TEST_USER_EMAIL)
        .phone(TEST_USER_PHONE)
        .userRoleType(UserRoleType.USER)
        .userStatus(UserStatus.ACTIVE)
        .isEmailVerified(true)
        .isPrivate(false)
        .build();

    userPersistenceAdapter.save(user);

    // 프로필 생성
    UserProfile userProfile = UserProfile.builder()
        .userId(TEST_USER_ID)
        .nickname(TEST_USER_NICKNAME)
        .statusMessage("안녕하세요")
        .bio("테스트 바이오")
        .genderType(UserGenderType.MALE)
        .userProfileType(UserProfileType.USER)
        .build();

    userProfilePersistenceAdapter.saveUserProfile(userProfile);

    entityManager.flush();
    entityManager.clear();

    return user;
  }

  /**
   * 커스텀 사용자 생성
   */
  @Transactional
  protected User createUser(String userId, String email, String nickname) {
    User user = User.builder()
        .userId(userId)
        .userName("사용자")
        .password(TEST_USER_PASSWORD)
        .email(email)
        .phone(TEST_USER_PHONE)
        .userRoleType(UserRoleType.USER)
        .userStatus(UserStatus.ACTIVE)
        .isEmailVerified(true)
        .isPrivate(false)
        .build();

    userPersistenceAdapter.save(user);

    UserProfile userProfile = UserProfile.builder()
        .userId(userId)
        .nickname(nickname)
        .statusMessage("안녕하세요")
        .bio("바이오")
        .genderType(UserGenderType.MALE)
        .userProfileType(UserProfileType.USER)
        .build();

    userProfilePersistenceAdapter.saveUserProfile(userProfile);

    entityManager.flush();
    entityManager.clear();

    return user;
  }
}
