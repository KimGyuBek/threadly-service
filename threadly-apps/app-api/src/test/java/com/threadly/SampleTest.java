package com.threadly;


import com.threadly.controller.post.BasePostApiTest;
import com.threadly.testsupport.fixture.posts.PostLikeFixtureLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class SampleTest extends BasePostApiTest {

//  @Autowired
//  private UserFixtureLoader userFixtureLoader;
//
//  @Autowired
//  private PostFixtureLoader postFixtureLoader;

  @Autowired
  private PostLikeFixtureLoader postLikeFixtureLoader;

  @BeforeEach
  void setUp() {
//    userFixtureLoader.load("/users/user-fixture.json");
//    postFixtureLoader.load("/posts/post-fixture.json");
    postLikeFixtureLoader.load("post-like-list/post-like-user.json", "post-like-list/post.json",
        "post-like-list/post-likes.json");
  }

  @Test
  public void test() throws Exception {
    //given

    //when

    //then

  }

}
