package com.threadly.testsupport.mapper.posts;

import com.threadly.core.domain.post.comment.CommentLike;
import com.threadly.testsupport.dto.posts.PostCommentLikeFixtureDto;

/**
 * Post 댓글 좋아요 객체 매퍼
 */
public class PostCommentLikeFixtureMapper {

  /**
   * PostCommentLike Fixture Dto -> Post Comment Like Domain
   *
   * @param fixture
   * @return
   */
  public static CommentLike toPostComment(PostCommentLikeFixtureDto fixture) {
    return CommentLike.newTestCommentLike(
        fixture.getCommentId(),
        fixture.getUserId()
    );
  }

}
