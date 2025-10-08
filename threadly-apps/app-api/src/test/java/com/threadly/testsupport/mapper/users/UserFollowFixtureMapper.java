package com.threadly.testsupport.mapper.users;

import com.threadly.testsupport.dto.users.UserFollowFixtureDto;
import com.threadly.core.domain.follow.Follow;

/**
 * User Follow Fixture 객체 매퍼
 */
public class UserFollowFixtureMapper {




  /**
   * FixtureDto -> Domain
   *
   * @param fixture
   * @return
   */
  public static Follow toDomain(UserFollowFixtureDto fixture) {
    return new Follow(
        fixture.getFollowId(),
        fixture.getFollowerId(),
        fixture.getFollowingId(),
        fixture.getFollowStatus()
    );
  }

}
