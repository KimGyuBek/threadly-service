package com.threadly.testsupport.fixture.users;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.adapter.persistence.user.adapter.UserPersistenceAdapter;
import com.threadly.adapter.persistence.user.adapter.UserProfilePersistenceAdapter;
import com.threadly.core.domain.user.UserStatus;
import com.threadly.testsupport.dto.users.UserFixtureDto;
import com.threadly.testsupport.fixture.FixtureLoader;
import com.threadly.testsupport.mapper.users.UserFixtureMapper;
import com.threadly.core.domain.user.User;
import com.threadly.core.domain.user.profile.UserProfile;
import com.threadly.utils.TestLogUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 로더
 */
@RequiredArgsConstructor
@Component
public class UserFixtureLoader {

  private final UserPersistenceAdapter userPersistenceAdapter;
  private final UserProfilePersistenceAdapter userProfilePersistenceAdapter;

  @PersistenceContext
  private final EntityManager entityManager;

  /**
   * 전체 사용자 데이터 삽입
   *
   * @param path
   */
  @Transactional
  public void load(String path) {
    List<UserFixtureDto> userData = getUserData(path);
    generateUser(userData, userData.size(), UserStatus.ACTIVE, false);
  }

  @Transactional
  public void load(String path, UserStatus userStatus) {
    List<UserFixtureDto> userData = getUserData(path);
    generateUser(userData, userData.size(), userStatus, false);
  }

  @Transactional
  public void load(String path, UserStatus userStatus, boolean isPrivate) {
    List<UserFixtureDto> userData = getUserData(path);
    generateUser(userData, userData.size(), userStatus, isPrivate);
  }

  /**
   * count 만큼 사용자 데이터 삽입
   *
   * @param path
   * @param count
   */
  @Transactional
  public void load(String path, int count) {
    List<UserFixtureDto> userData = getUserData(path);
    generateUser(userData, count, UserStatus.ACTIVE, false);
  }

  /**
   * 사용자 생성
   *
   * @param userFixtureDtoList
   * @param count
   */
  private void generateUser(List<UserFixtureDto> userFixtureDtoList, int count,
      UserStatus userStatus, boolean isPrivate) {
    List<UserFixtureDto> fixtures = userFixtureDtoList;

    if (count > fixtures.size() || count < 1 || count == 0) {
      count = fixtures.size();
    }

    for (int i = 0; i < count; i++) {
      UserFixtureDto dto = fixtures.get(i);
      User user = UserFixtureMapper.toUser(dto);

      if (dto.isEmailVerified()) {
        user.verifyEmail();
      }

      switch (userStatus) {
        case ACTIVE:
          user.markAsActive();
          break;
        case INACTIVE:
          user.markAsInactive();
          break;
        case DELETED:
          user.markAsDeleted();
          break;
        case BANNED:
          user.markAsBanned();
          break;
        case INCOMPLETE_PROFILE:
          user.markAsIncompleteProfile();
          break;
      }

      if (isPrivate) {
        user.markAsPrivate();
      } else {
        user.markAsPublic();
      }

      userPersistenceAdapter.save(user);
      if (dto.getUserProfile() != null) {
        UserProfile userProfile = UserFixtureMapper.toProfile(dto);
        userProfilePersistenceAdapter.saveUserProfile(userProfile);
      }
    }

    /*트랜잭션 커밋 전에 insert 쿼리 강제 실행*/
    entityManager.flush();
    entityManager.clear();

    TestLogUtils.log("사용자 데이터 생성 완료 : 총 " + count + "개");
  }

  private static List<UserFixtureDto> getUserData(String path) {
    List<UserFixtureDto> fixtures = FixtureLoader.load(path, new TypeReference<>() {
    });
    return fixtures;
  }


}
