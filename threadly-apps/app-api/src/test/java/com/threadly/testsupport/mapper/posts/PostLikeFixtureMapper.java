package com.threadly.testsupport.mapper.posts;

import com.threadly.post.PostLike;
import com.threadly.testsupport.dto.posts.PostLikeFixtureDto;

/**
 * Post Like 객체 매퍼
 */
public class PostLikeFixtureMapper {

  /**
   * PostLike Fixture Dto -> Post Like Domain
   *
   * @param fixture
   * @return
   */
  public static PostLike toPostLike(PostLikeFixtureDto fixture) {
    return
        new PostLike(
            fixture.getPostId(),
            fixture.getUserId()
        );
  }

}
