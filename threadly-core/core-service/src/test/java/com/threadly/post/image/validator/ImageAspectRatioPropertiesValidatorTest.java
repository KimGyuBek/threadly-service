package com.threadly.post.image.validator;

import static com.threadly.post.image.helper.image.UploadImageFactory.generateImageWithRatio;
import static org.assertj.core.api.Assertions.assertThat;

import com.threadly.file.UploadImage;
import com.threadly.properties.UploadProperties;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * 이미지 비율 검증 유틸 테스트
 */
@SpringBootTest(classes = {
    ImageAspectRatioValidator.class,
    ImageAspectRatioPropertiesValidatorTest.TestConfig.class
})
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("이미지 비율 검증 유틸 테스트")
class ImageAspectRatioPropertiesValidatorTest {

  @Autowired
  private ImageAspectRatioValidator imageAspectRatioValidator;

  @Autowired
  private UploadProperties uploadProperties;

  @TestConfiguration
  @EnableConfigurationProperties(UploadProperties.class)
  static class TestConfig {

  }

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    @Order(1)
    @DisplayName("1. 설정값과 일치하는 비율의 이미지 요청 시")
    @Test
    public void isValidAspectRatio_shouldReturnTrue_whenImageRatioIsValid() throws Exception {
      //given
      UploadImage uploadImage = generateImageWithRatio(
          "sample.jpg",
          "jpeg",
          300,
          400
      );

      //when
      //then
      assertThat(
          imageAspectRatioValidator.isValid(List.of(uploadImage))
      ).isTrue();
    }

    @Order(2)
    @DisplayName("2. 최대 허용 수 만큼의 일치하는 비율의 이미지 요청 시")
    @Test
    public void isValidAspectRatio_shouldReturnTrue_whenImageRatioIsValidAndMaxImageCount()
        throws Exception {
      //given
      List<UploadImage> images = new ArrayList<>();
      for (int i = 0; i < uploadProperties.getMaxImageCount(); i++) {
        images.add(generateImageWithRatio(
            "sample_0" + (i + 1) + ".jpg",
            "jpeg",
            300,
            400
        ));
      }

      //when
      //then
      assertThat(
          imageAspectRatioValidator.isValid(images)
      ).isTrue();
    }

    @Order(2)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("실패")
    class fail {

      @DisplayName("1. 설정값과 다른 비율의 이미지 요청 시 false")
      @Test
      public void isValidAspectRatio_shouldReturnFalse_whenInvalidRatioImage() throws Exception {
        //given
        UploadImage uploadImage = generateImageWithRatio(
            "sample.jpg",
            "jpeg",
            100,
            100
        );

        //when
        //then
        assertThat(imageAspectRatioValidator.isValid(List.of(uploadImage))).isFalse();

      }

    }


  }


}