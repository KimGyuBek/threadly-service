package com.threadly.testsupport.fixture.users;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.repository.TestUserProfileImageRepository;
import com.threadly.testsupport.dto.users.UserProfileImageFixtureDto;
import com.threadly.testsupport.fixture.FixtureLoader;
import com.threadly.testsupport.mapper.users.UserProfileImageFixtureMapper;
import com.threadly.user.profile.image.UserProfileImage;
import com.threadly.utils.TestLogUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserProfileImage Fixture Loader
 */
@RequiredArgsConstructor
@Component
public class UserProfileImageFixtureLoader {

  private final TestUserProfileImageRepository testUserProfileImageRepository;

  @PersistenceContext
  private final EntityManager entityManager;

  /**
   * 전체 사용자 데이터 삽입
   *
   * @param path
   */
  @Transactional
  public void load(String path) {
    List<UserProfileImageFixtureDto> userData = getProfileImageData(path);
    generateProfileImage(userData, userData.size());
  }

  /**
   * @param dtoList
   * @param count
   */
  private void generateProfileImage(List<UserProfileImageFixtureDto> dtoList, int count) {

    if (count > dtoList.size() || count < 1 || count == 0) {
      count = dtoList.size();
    }

    for (int i = 0; i < count; i++) {
      UserProfileImageFixtureDto dto = dtoList.get(i);
      UserProfileImage userProfileImage = UserProfileImageFixtureMapper.toDomain(dto);

      testUserProfileImageRepository.save(userProfileImage);
    }

    /*트랜잭션 커밋 전에 insert 쿼리 강제 실행*/
    entityManager.flush();
    entityManager.clear();

    TestLogUtils.log("사용자 프로필 이미지 데이터 생성 완료 : 총 " + count + "개");
  }

  private static List<UserProfileImageFixtureDto> getProfileImageData(String path) {
    return FixtureLoader.load(path, new TypeReference<>() {
    });
  }


}
