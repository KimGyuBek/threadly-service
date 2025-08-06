package com.threadly.post.mapper;

import com.threadly.post.entity.CommentLikeEntity;
import com.threadly.post.entity.PostCommentEntity;
import com.threadly.post.entity.UserIdAndCommentId;
import com.threadly.user.entity.UserEntity;
import com.threadly.post.comment.CommentLike;

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
