package com.threadly.adapter.persistence.post.mapper;

import com.threadly.adapter.persistence.post.entity.CommentLikeEntity;
import com.threadly.adapter.persistence.post.entity.PostCommentEntity;
import com.threadly.adapter.persistence.post.entity.UserIdAndCommentId;
import com.threadly.adapter.persistence.user.entity.UserEntity;
import com.threadly.core.domain.post.comment.CommentLike;

/**
 * Post Comment Like Mapper
 */
public class CommentLikeMapper {

  /**
   * entity -> domain
   *
   * @param entity
   * @return
   */
  public static CommentLike toDomain(CommentLikeEntity entity) {
    return new CommentLike(
        entity.getId().getCommentId(),
        entity.getId().getUserId()
    );
  }

  /**
   * domain -> entity
   *
   * @param domain
   * @return
   */
  public static CommentLikeEntity toEntity(CommentLike domain) {
    return new CommentLikeEntity(
        new UserIdAndCommentId(domain.getCommentId(), domain.getUserId()),
        PostCommentEntity.fromId(domain.getCommentId()),
        UserEntity.fromId(domain.getUserId()),
        null
    );

  }

}
