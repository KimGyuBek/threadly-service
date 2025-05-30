package com.threadly.testsupport.fixture;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.adapter.user.UserPersistenceAdapter;
import com.threadly.testsupport.dto.UserFixtureDto;
import com.threadly.user.User;
import com.threadly.user.UserProfile;
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

  @PersistenceContext
  private final EntityManager entityManager;

  /*전체 사용자 데이터 생성*/
  @Transactional
  public void load(String path) {
    List<UserFixtureDto> userData = getUserData(path);
    generateUser(userData, userData.size());
  }

  /*특정 범위 사용자 데이터 생성*/
  @Transactional
  public void load(String path, int count) {
    List<UserFixtureDto> userData = getUserData(path);
    generateUser(userData, count);
  }

  private void generateUser(List<UserFixtureDto> userFixtureDtoList, int count) {
    List<UserFixtureDto> fixtures = userFixtureDtoList;

    if (count > fixtures.size() || count < 1 || count == 0) {
      count = fixtures.size();
    }

    for (int i = 0; i < count; i++) {
      UserFixtureDto dto = fixtures.get(i);
      User user = UserFixtureMapper.toUser(dto);

      if (dto.isEmailVerified()) {
        user.setEmailVerified();
      }

      if (dto.getUserProfile() != null) {
        UserProfile userProfile = UserFixtureMapper.toProfile(dto);
        userPersistenceAdapter.saveUserProfile(user, userProfile);
      }

      userPersistenceAdapter.save(user);
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
