package com.threadly.user.controller.my.image;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.mock.web.MockMultipartFile;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("내 프로필 이미지 업로드 관련 API 테스트")
public class UploadProfileImageApiTest extends BaseProfileImageApiTest {

  @AfterAll
  static void cleanup() throws IOException {
    cleanUpDirectoryContents();
  }

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    @DisplayName("1. 프로필 이미지 임시 업로드 성공 검증 ")
    @Test
    public void uploadMyProfileImage_shouldSuccess() throws Exception {
      //given

      String accessToken = getAccessToken(USER_EMAIL);

      MockMultipartFile file = generateImageWithRatio("01.jpeg", "jpeg", 100, 100);
      //when
      sendUploadMyProfileImageRequest(accessToken, file, status().isCreated());

      //then

    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

  }
}



