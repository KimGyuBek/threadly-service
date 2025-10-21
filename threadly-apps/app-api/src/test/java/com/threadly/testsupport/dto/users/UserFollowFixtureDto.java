package com.threadly.testsupport.dto.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.threadly.core.domain.follow.FollowStatus;
import lombok.Data;

/**
 * UserFollowFixtureDto
 */
@Data
public class UserFollowFixtureDto {

  private String followId;
  private String followerId;
  private String followingId;

  @JsonProperty("status")
  private FollowStatus followStatus;

}
