package com.threadly.post.image;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.exception.ErrorCode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

/**
 * 게시글 이미지 업로드 검증 API 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("게시글 이미지 업로드 검증 API 테스트")
public class UploadPostImageValidateApiTest extends BasePostImageApiTest {

  private String accessToken;
  private String postId;

  @BeforeEach
  void setUp() throws Exception {
    super.setUpDefaultUser();

    /*로그인*/
    accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);
    /*게시글 생성 후 postId 가져오기*/
    postId = getPostId(accessToken);
  }


  @BeforeEach
  void tearDown() throws IOException {
    super.clearFiles();
  }


  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 이미지 업로드 검증 - 1. 정상적인 이미지 파일 업로드 시 응답 검증*/
    @Order(1)
    @DisplayName("1. 정상적인 이미지 파일 1개 업로드 시 응답 검증")
    @Test
    public void uploadImage_shouldSucceed_whenValidImageUpload() throws Exception {
      //given
      //when
      /*이미지 파일 생성*/
      MockMultipartFile image = generateImageWithRatio(
          "01.jpg", "jpeg", 300, 400
      );

      //then
      /* 게시글 이미지 업로드 요청*/
      CommonResponse<UploadPostImagesApiResponse> uploadImageResponse = sendUploadPostImage(
          accessToken, List.of(image), status().isCreated());

      assertThat(uploadImageResponse.getData().images().size()).isEqualTo(1);
    }

    /*[Case #2] 게시글 업로드 검증 - 2. 정상적인 이미지를 최대 허용 수 만큼 업로드 시 응답 검증*/
    @Order(2)
    @DisplayName("2. 정상적인 이미지를 최대 허용 수 만큼 업로드 시 응답 검증")
    @Test
    public void uploadImage_shouldSucceed_whenUploadMaxCount() throws Exception {
      //given
      //when
      /*이미지 파일 생성*/
      List<MockMultipartFile> images = new ArrayList<>();
      for (int i = 0; i < uploadProperties.getMaxImageCount(); i++) {
        images.add(
            generateImageWithRatio(
                "01.jpg", "jpeg", 300, 400
            ));
      }

      //then
      /* 게시글 이미지 업로드 요청*/
      CommonResponse<UploadPostImagesApiResponse> uploadImageResponse = sendUploadPostImage(
          accessToken, images, status().isCreated());

      assertThat(uploadImageResponse.getData().images().size()).isEqualTo(
          uploadProperties.getMaxImageCount());
    }

  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Case #1] 이미지 업로드 검증 - 이미지가 빈 리스트 상태에서 요청시 400 Bad Request*/
    @Order(1)
    @DisplayName("1. 이미지가 빈 리스트인 상태에서 요청시 400 Bad Request")
    @Test
    public void uploadImage_shouldReturnBadRequest_whenImageIsEmpty() throws Exception {
      //given
      //when
      //then
      CommonResponse<UploadPostImagesApiResponse> uploadResponse = sendUploadPostImage(
          accessToken, List.of(), status().isBadRequest());
      assertThat(uploadResponse.getCode()).isEqualTo(ErrorCode.POST_IMAGE_EMPTY.getCode());
    }

    /*[Case #2] 이미지 업로드 검증 - 이미지가 null인 상태로 요청 시 400 Bad Request*/
    @Order(2)
    @DisplayName("2. 이미지가 null인 상태에서 요청시 400 Bad Request")
    @Test
    public void uploadImage_shouldReturnBadRequest_whenImageIsNull() throws Exception {
      //given
      //when
      //then
      CommonResponse<UploadPostImagesApiResponse> uploadResponse = sendUploadPostImage(
          accessToken, null, status().isBadRequest());
      assertThat(uploadResponse.getCode()).isEqualTo(ErrorCode.POST_IMAGE_EMPTY.getCode());
    }

    /*[Case #3] 이미지 업로드 검증 - 최대 허용 수 이상의 이미지 요청 시 400 Bad Request*/
    @Order(3)
    @DisplayName("3. 최대 허용 수를 초과하는 이미지 요청 시 400 Bad Request")
    @Test
    public void uploadImage_shouldReturnBadRequest_whenUploadOverMaxCount() throws Exception {
      //given
      //when
      List<MockMultipartFile> images = generateMultipartFiles(
          uploadProperties.getMaxImageCount() + 1, "01.jpg", "images",
          MediaType.IMAGE_JPEG_VALUE);
      //then
      CommonResponse<UploadPostImagesApiResponse> uploadResponse = sendUploadPostImage(
          accessToken, images, status().isBadRequest());
      assertThat(uploadResponse.getCode()).isEqualTo(
          ErrorCode.POST_IMAGE_UPLOAD_LIMIT_EXCEEDED.getCode());
    }

    /*[Case #4] 이미지 업로드 검증 - 이미지 용량이 최대 허용 값 보다 큰 경우 400 Bad Request*/
    @Order(4)
    @DisplayName("4. 최대 허용 용량 이상이 이미지 요청 시 400 Bad Request")
    @Test
    public void uploadImage_shouldReturnBadRequest_whenUploadOverMaxSize() throws Exception {
      //given
      //when
      MockMultipartFile image = new MockMultipartFile("images", "sample.jpg", "image/jpeg",
          new byte[6 * 1024 * 1024]
      );

      //then
      CommonResponse<UploadPostImagesApiResponse> uploadResponse = sendUploadPostImage(
          accessToken, List.of(image), status().isBadRequest());
      assertThat(uploadResponse.getCode()).isEqualTo(
          ErrorCode.IMAGE_TOO_LARGE.getCode());
    }

    /*[Case #5] 이미지 업로드 검증 - 지원하지 않는 확장자의 파일 요청 시 400 Bad Request*/
    @Order(5)
    @DisplayName("5. 지원하지 않는 확장자의 파일을 요청 시 400 Bad Request")
    @Test
    public void uploadImage_shouldReturnBadRequest_whenUploadFileExtensionNotSupported()
        throws Exception {
      //given
      //when
      MockMultipartFile image = new MockMultipartFile("images", "sample.gif",
          MediaType.IMAGE_GIF_VALUE,
          Files.readAllBytes(Paths.get("src/test/resources/images/sample/01.jpg"))
      );

      //then
      CommonResponse<UploadPostImagesApiResponse> uploadResponse = sendUploadPostImage(
          accessToken, List.of(image), status().isBadRequest());
      assertThat(uploadResponse.getCode()).isEqualTo(
          ErrorCode.IMAGE_INVALID_EXTENSION.getCode());
    }

  }
}

