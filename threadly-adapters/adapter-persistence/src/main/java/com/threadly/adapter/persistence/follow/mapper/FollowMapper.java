package com.threadly.adapter.persistence.follow.mapper;

import com.threadly.adapter.persistence.follow.entity.FollowEntity;
import com.threadly.adapter.persistence.user.entity.UserEntity;
import com.threadly.core.domain.follow.Follow;

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
