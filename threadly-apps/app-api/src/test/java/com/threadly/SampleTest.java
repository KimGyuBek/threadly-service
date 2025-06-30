package com.threadly;


import com.threadly.post.controller.BasePostApiTest;
import com.threadly.testsupport.fixture.posts.PostCommentFixtureLoader;
import com.threadly.testsupport.fixture.posts.PostCommentLikeFixtureLoader;
import com.threadly.testsupport.fixture.posts.PostLikeFixtureLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class SampleTest extends BasePostApiTest {

  @Autowired
  private PostCommentLikeFixtureLoader postCommentLikeFixtureLoader;

  @Autowired
  private PostLikeFixtureLoader postLikeFixtureLoader;

  @BeforeEach
  void setUp() {
    postCommentLikeFixtureLoader.load(
        "/posts/delete/user.json",
        "/posts/delete/post.json",
        "/posts/delete/post-comment.json",
        "/posts/delete/comment-like.json"
    );
    postLikeFixtureLoader.load(
        "/posts/delete/post-like.json"
    );
  }

  @Test
  public void test() throws Exception {
    //given

    //when

    //then

  }

}
