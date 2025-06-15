package com.threadly.post.image.validator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import com.threadly.exception.ErrorCode;
import com.threadly.exception.post.PostImageException;
import com.threadly.file.UploadImage;
import com.threadly.post.image.helper.image.UploadImageFactory;
import com.threadly.properties.UploadProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = {
    ImageUploadValidator.class,
    ImageUploadValidatorTest.TestConfig.class
})
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
@DisplayName("게시글 이미지 업로드 검증 테스트")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class ImageUploadValidatorTest {

  @Autowired
  private ImageUploadValidator validator;

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

    /*[Case #1] 정상적인 이미지 파일은 예외없이 성공하는지 검증*/
    @Order(1)
    @DisplayName("1. 정상적인 이미지 파일은 예외없이 성공하는지 검증")
    @Test
    void validate_shouldPass_whenValidImageProvided() throws IOException {
      UploadImage image = UploadImageFactory.fromMock(
          new MockMultipartFile("file", "sample.jpg", IMAGE_JPEG_VALUE,
              Files.readAllBytes(Paths.get("src/test/resources/images/sample/sample.jpg"))
          ));

      assertThatCode(() -> validator.validate(List.of(image)))
          .doesNotThrowAnyException();
    }

  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    @Order(1)
    @DisplayName("1. 이미지가 없을 경우 예외가 발생하는지 검증")
    @Test
    public void validate_shouldThrow_whenImageNotExists() throws Exception {
      //given
      List<UploadImage> images = new ArrayList<>();

      //when
      //then
      assertThatThrownBy(
          () -> validator.validate(images)
      ).isInstanceOf(PostImageException.class)
          .hasMessageContaining(ErrorCode.POST_IMAGE_EMPTY.getDesc());
    }

    @Order(2)
    @DisplayName("2. 허용된 업로드 수 초과 시 예외가 발생하는지 검증")
    @Test
    public void validate_shouldThrow_whenImageCountIsMoreThenMaxCount() throws Exception {
      //given
      List<UploadImage> images = new ArrayList<>();

      for (int i = 0; i < uploadProperties.getMaxImageCount() + 1; i++) {
        images.add(UploadImageFactory.fromMock(
            new MockMultipartFile("file", "sample.jpg", IMAGE_JPEG_VALUE,
                Files.readAllBytes(Paths.get("src/test/resources/images/sample/sample.jpg"))
            )));
      }

      //when
      //then
      assertThatThrownBy(
          () -> validator.validate(images)
      ).isInstanceOf(PostImageException.class)
          .hasMessageContaining(ErrorCode.POST_IMAGE_UPLOAD_LIMIT_EXCEEDED.getDesc());
    }

    @Order(3)
    @DisplayName("3. 이미지의 용량이 최대 허용 값 보다 큰 경우 예외가 발생하는지 검증")
    @Test
    void validate_shouldThrow_whenSizeTooLarge() {
      UploadImage image = UploadImageFactory.fromMock(
          new MockMultipartFile("file", "sample.jpg", IMAGE_JPEG_VALUE,
              new byte[6 * 1024 * 1024]) // 6MB
      );

      assertThatThrownBy(() -> validator.validate(List.of(image)))
          .isInstanceOf(PostImageException.class)
          .hasMessageContaining(ErrorCode.POST_IMAGE_TOO_LARGE.getDesc());
    }

    @Order(4)
    @DisplayName("4. 지원하지 않는 파일 확장자로 요청 시 예외가 발생하는지 검증")
    @Test
    public void validate_shouldThrow_whenExtensionNotSupported() throws Exception {
      //given
      UploadImage image = UploadImageFactory.fromMock(
          new MockMultipartFile("file", "sample.mp4", IMAGE_JPEG_VALUE,
              Files.readAllBytes(Paths.get("src/test/resources/images/sample/sample.jpg"))
          ));

      //when
      //then
      assertThatThrownBy(
          () -> validator.validate(List.of(image))
      ).isInstanceOf(PostImageException.class)
          .hasMessageContaining(ErrorCode.POST_IMAGE_INVALID_EXTENSION.getDesc());

    }

  }



}