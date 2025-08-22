package com.threadly.adapter.persistence.post.mapper;

import com.threadly.adapter.persistence.post.entity.PostEntity;
import com.threadly.adapter.persistence.post.entity.PostIdAndUserId;
import com.threadly.adapter.persistence.post.entity.PostLikeEntity;
import com.threadly.adapter.persistence.user.entity.UserEntity;
import com.threadly.core.domain.post.PostLike;

/**
 * Post Like Mapper
 */
public class PostLikeMapper {

  /**
   * entity -> domain
   *
   * @param entity
   * @return
   */
  public static PostLike toDomain(PostLikeEntity entity) {
    return new PostLike(
        entity.getId().getPostId(),
        entity.getId().getUserId()
    );
  }

  /**
   * domain -> entity
   *
   * @param domain
   * @return
   */
  public static PostLikeEntity toEntity(PostLike domain) {
    return new PostLikeEntity(
        new PostIdAndUserId(domain.getPostId(), domain.getUserId()),
        PostEntity.fromId(domain.getPostId()),
        UserEntity.fromId(domain.getUserId()),
        null
    );
  }

}
