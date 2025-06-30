package com.threadly.mapper.post;

import com.threadly.entity.post.PostEntity;
import com.threadly.entity.post.PostIdAndUserId;
import com.threadly.entity.post.PostLikeEntity;
import com.threadly.entity.user.UserEntity;
import com.threadly.post.PostLike;

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
