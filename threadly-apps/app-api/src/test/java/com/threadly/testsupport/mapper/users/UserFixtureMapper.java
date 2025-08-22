package com.threadly.testsupport.mapper.users;

import com.threadly.testsupport.dto.users.UserFixtureDto;
import com.threadly.core.domain.user.User;
import com.threadly.core.domain.user.profile.UserProfile;

/**
 * User, UserProfile Fixture 객체 매퍼
 */
public class UserFixtureMapper {

  /**
   * UserFixtureDto -> UserDomain
   *
   * @param fixture
   * @return
   */
  public static User toUser(UserFixtureDto fixture) {
    return
        User.newTestUser(
            fixture.getUserId(),
            fixture.getUserName(),
            fixture.getPassword(),
            fixture.getEmail(),
            fixture.getPhone()
        );
  }

  /**
   * UserFixtureDto -> UserProfile Domain
   *
   * @param fixture
   * @return
   */
  public static UserProfile toProfile(UserFixtureDto fixture) {
    return
        UserProfile.newTestProfile(
            fixture.getUserId(),
            fixture.getUserProfile().getNickname(),
            fixture.getUserProfile().getStatusMessage(),
            fixture.getUserProfile().getBio(),
            fixture.getUserProfile().getGender(),
            fixture.getUserProfile().getProfileType()
        );
  }

}
