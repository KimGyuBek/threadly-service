package com.threadly.mapper.user;

import com.threadly.entity.user.UserEntity;
import com.threadly.entity.user.UserFollowEntity;
import com.threadly.user.Follow;

/**
 * UserFollowMapper
 */
public class UserFollowMapper {

  /**
   * domain -> entity
   * @param follow
   * @return
   */
  public static UserFollowEntity toEntity(Follow follow) {
    return new UserFollowEntity(
        follow.getFollowId(),
        UserEntity.fromId(follow.getFollowerId()),
        UserEntity.fromId(follow.getFollowingId()),
        follow.getStatusType()
    );
  }


}
