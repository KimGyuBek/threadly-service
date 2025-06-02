package com.threadly.mapper.post;

import com.threadly.entity.post.PostCommentEntity;
import com.threadly.entity.post.PostEntity;
import com.threadly.entity.user.UserEntity;
import com.threadly.posts.comment.PostComment;

/**
 * Post Comment Mapper
 */
public class PostCommentMapper {

  /**
   * PostCommentEntity -> PostCommentDomain
   *
   * @param entity
   * @return
   */
  public static PostComment toDomain(PostCommentEntity entity) {
    if (entity == null) {
      throw new IllegalArgumentException("PostCommentEntity cannot be null");
    }
    return new PostComment(
        entity.getCommentId(),
        entity.getPost().getPostId(),
        entity.getUser().getUserId(),
        entity.getContent(),
        entity.getStatus()
    );
  }

  /**
   * PostCommentDomain -> PostCommentEntity
   *
   * @param postComment
   * @return
   */
  public static PostCommentEntity toEntity(PostComment postComment) {
    return new PostCommentEntity(
        postComment.getCommentId(),
        PostEntity.fromId(postComment.getPostId()),
        UserEntity.fromId(postComment.getUserId()),
        postComment.getContent(),
        postComment.getStatus()
    );
  }
}



