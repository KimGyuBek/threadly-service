package com.threadly.testsupport.fixture.users;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.core.domain.user.UserStatus;
import com.threadly.testsupport.dto.users.UserFollowFixtureDto;
import com.threadly.testsupport.fixture.FixtureLoader;
import com.threadly.testsupport.mapper.users.UserFollowFixtureMapper;
import com.threadly.core.port.follow.out.FollowCommandPort;
import com.threadly.utils.TestLogUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 팔로우 데이터 로더
 */
@RequiredArgsConstructor
@Component
public class UserFollowFixtureLoader {

  private final FollowCommandPort followCommandPort;
  private final UserFixtureLoader userFixtureLoader;

  @PersistenceContext
  private final EntityManager entityManager;

  /**
   * 전체 사용자 데이터 삽입
   *
   * @param followDataPath
   */
  @Transactional
  public void load(String userDataPath, String followDataPath) {
    userFixtureLoader.load(userDataPath);
    List<UserFollowFixtureDto> data = getData(followDataPath);
    generateData(data);
  }

  @Transactional
  public void load(String userDataPath, boolean isPrivate, String followDataPath) {
    userFixtureLoader.load(userDataPath, UserStatus.ACTIVE, isPrivate);
    List<UserFollowFixtureDto> data = getData(followDataPath);
    generateData(data);
  }


  /**
   * 데이터 생성
   *
   * @param dtoList
   */
  private void generateData(List<UserFollowFixtureDto> dtoList) {

    dtoList.forEach(dto -> {
      followCommandPort.createFollow(UserFollowFixtureMapper.toDomain(dto));
    });

    entityManager.flush();
    entityManager.clear();

    TestLogUtils.log("팔로우 데이터 생성 완료 : 총 " + dtoList.size() + "개");

  }

  /**
   * 데이터 조회
   *
   * @param path
   * @return
   */
  private static List<UserFollowFixtureDto> getData(String path) {
    List<UserFollowFixtureDto> fixtures = FixtureLoader.load(path, new TypeReference<>() {
    });
    return fixtures;
  }


}
