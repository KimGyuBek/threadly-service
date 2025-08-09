package com.threadly.adapter.persistence.post.mapper;

import com.threadly.adapter.persistence.post.entity.PostEntity;
import com.threadly.adapter.persistence.user.entity.UserEntity;
import com.threadly.core.domain.post.Post;

/**
 * Post Mapper
 */
public class PostMapper {

  /**
   * PostEntity -> PostDomain
   *
   * @param entity
   * @return
   */
  public static Post toDomain(PostEntity entity) {
    if (entity == null) {
      throw new IllegalArgumentException("PostEntity cannot be null");
    }
    String userId = (entity.getUser() != null) ? entity.getUser().getUserId() : null;
    return new Post(
        entity.getPostId(),
        userId,
        entity.getContent(),
        entity.getViewCount(),
        entity.getStatus(),
        entity.getModifiedAt()
    );
  }

  /**
   * PostDomain -> PostEntity
   *
   * @param post
   * @return
   */
  public static PostEntity toEntity(Post post) {
    return new PostEntity(
        post.getPostId(),
        UserEntity.fromId(post.getUserId()),
        post.getContent(),
        post.getViewCount(),
        post.getStatus()
    );
  }
}



