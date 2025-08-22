package com.threadly.testsupport.mapper.posts;

import com.threadly.core.domain.post.comment.PostComment;
import com.threadly.testsupport.dto.posts.PostCommentFixtureDto;

/**
 * Post 댓글 객체 매퍼
 */
public class PostCommentFixtureMapper {

  /**
   * PostComment Fixture Dto -> Post Comment Domain
   *
   * @param fixture
   * @return
   */
  public static PostComment toPostComment(PostCommentFixtureDto fixture) {
    return PostComment.newTestComment(
        fixture.getCommentId(),
        fixture.getPostId(),
        fixture.getUserId(),
        fixture.getContent(),
        fixture.getStatus()
    );
  }

}
