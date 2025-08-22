package com.threadly.commons.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RandomUtilsTest {

  @DisplayName("UUID 생성 테스트")
  @Test
  public void generateUUIDTest() throws Exception {
    //given
    //when
    String randomId = RandomUtils.generateUUID();

    //then
    assertThat(randomId.length()).isEqualTo(16);
    System.out.println(randomId);
  }

  @DisplayName("NanoId 생성 테스트")
  @Test
  public void generateNanoIdTest() throws Exception {
    //given
    //when
    String randomId = RandomUtils.generateNanoId();

    //then
    assertThat(randomId.length()).isEqualTo(16);
    System.out.println(randomId);

  }

}