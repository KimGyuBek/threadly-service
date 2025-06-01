package com.threadly.testsupport.mapper.posts;

import com.threadly.posts.Post;
import com.threadly.testsupport.dto.posts.PostFixtureDto;

/**
 * Post 객체 매퍼
 */
public class PostFixtureMapper {

  /**
   * PostFixtureDto -> Post Domain
   *
   * @param fixture
   * @return
   */
  public static Post toPost(PostFixtureDto fixture) {
    return
        Post.newTestPost(
            fixture.getPostId(),
            fixture.getUserId(),
            fixture.getContent(),
            fixture.getViewCount(),
            fixture.getStatus()
        );
  }

}
