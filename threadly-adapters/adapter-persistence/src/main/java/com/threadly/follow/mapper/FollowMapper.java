package com.threadly.follow.mapper;

import com.threadly.follow.entity.FollowEntity;
import com.threadly.user.entity.UserEntity;
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
