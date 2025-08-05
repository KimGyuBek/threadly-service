package com.threadly.mapper.follow;

import com.threadly.entity.follow.FollowEntity;
import com.threadly.entity.user.UserEntity;
import com.threadly.follow.Follow;

/**
 * UserFollowMapper
 */
public class FollowMapper {

  /**
   * domain -> entity
   *
   * @param follow
   * @return
   */
  public static FollowEntity toEntity(Follow follow) {
    return new FollowEntity(
        follow.getFollowId(),
        UserEntity.fromId(follow.getFollowerId()),
        UserEntity.fromId(follow.getFollowingId()),
        follow.getStatusType()
    );
  }

  /**
   * entity -> domain
   *
   * @param entity
   * @return
   */
  public static Follow toDomain(FollowEntity entity) {
    return new Follow(
        entity.getFollowId(),
        entity.getFollower().getUserId(),
        entity.getFollowing().getUserId(),
        entity.getStatusType()
    );
  }


}
