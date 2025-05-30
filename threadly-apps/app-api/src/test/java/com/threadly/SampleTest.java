package com.threadly;


import com.threadly.controller.post.BasePostApiTest;
import com.threadly.testsupport.fixture.UserFixtureLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class SampleTest extends BasePostApiTest {

  @Autowired
  private UserFixtureLoader userFixtureLoader;

  @BeforeEach
  void setUp() {
    userFixtureLoader.load("/users/user-email-not-verified.json");
//    userFixtureLoader.load("/users/user-fixture.json", 10);
  }

  @Test
  public void test() throws Exception {
    //given

    //when

    //then

  }

}
