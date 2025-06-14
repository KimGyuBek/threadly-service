package com.threadly.post.image;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.threadly.ErrorCode;
import com.threadly.exception.post.PostImageException;
import com.threadly.file.UploadImage;
import com.threadly.helper.UploadImageTestFactory;
import com.threadly.properties.UploadProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = {
    UploadImageValidator.class,
    UploadImageValidatorTest.TestConfig.class
})
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
@DisplayName("게시글 이미지 업로드 검증")
class UploadImageValidatorTest {

  @Autowired
  private UploadImageValidator validator;

  @TestConfiguration
  @EnableConfigurationProperties(UploadProperties.class)
  static class TestConfig {

  }

  /*[Case #1] 정상적인 이미지 파일은 예외없이 성공하는지 검증*/
  @DisplayName("정상적인 이미지 파일은 예외없이 성공하는지 검증")
  @Test
  void validate_shouldPass_whenValidImageProvided() throws IOException {
    UploadImage image = UploadImageTestFactory.fromMock(
        new MockMultipartFile("file", ".jpg", "image/jpeg",
            Files.readAllBytes(Paths.get("src/test/resources/images/sample/01.jpg"))
        ));

    assertThatCode(() -> validator.validate(List.of(image)))
        .doesNotThrowAnyException();
  }

  @DisplayName("이미지의 용량이 설정값보다 큰 경우 예외가 발생하는지 검증")
  @Test
  void validate_shouldThrow_whenSizeTooLarge() {
    UploadImage image = UploadImageTestFactory.fromMock(
        new MockMultipartFile("file", "sample.jpg", "image/jpeg",
            new byte[6 * 1024 * 1024]) // 6MB
    );

    assertThatThrownBy(() -> validator.validate(List.of(image)))
        .isInstanceOf(PostImageException.class)
        .hasMessageContaining(ErrorCode.POST_IMAGE_TOO_LARGE.getDesc());
  }

}