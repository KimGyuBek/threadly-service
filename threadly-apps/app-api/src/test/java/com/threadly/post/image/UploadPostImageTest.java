package com.threadly.post.image;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.ErrorCode;
import com.threadly.post.create.CreatePostApiResponse;
import com.threadly.post.get.GetPostDetailApiResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
 * 게시글 이미지 업로드 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("게시글 이미지 업로드 테스트")
public class UploadPostImageTest extends BasePostImageApiTest {


  @BeforeEach
  void setUp() {
    super.setUpDefaultUser();
  }

  @BeforeEach
  void tearDown() throws IOException {
    cleanUpDirectoryContents();
  }


  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /* [Case #1] 게시글 업로드 - 게시글 정상 생성 후 1개의 이미지 업로드시 성공하는지 검증*/
    @Order(1)
    @DisplayName("1. 게시글 생성 후 이미지 1개 업로드시 응답 검증")
    @Test
    public void uploadImage_shouldSucceed_whenOneImageUpload() throws Exception {
      //given

      /*1. 로그인 요청*/
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      /*2. 게시글 생성 요청*/
      String content = "content1";
      CommonResponse<CreatePostApiResponse> createPostResponse = sendCreatePostRequest(
          accessToken,
          content,
          status().isCreated()
      );
      String postId = createPostResponse.getData().postId();

      //when
      /*3. 게시글 이미지 업로드 요청*/
      Path path = Paths.get("src/test/resources/images/sample/01.jpg");
      MockMultipartFile image = new MockMultipartFile(
          "images",
          path.getFileName().toString(),
          MediaType.IMAGE_JPEG.toString(),
          Files.readAllBytes(path)
      );

      //then
      CommonResponse<UploadPostImagesApiResponse> uploadImageResponse = sendUploadPostImage(
          accessToken, postId, List.of(image), status().isCreated());

      assertThat(uploadImageResponse.getData().images().size()).isEqualTo(1);
    }


    /*[Case #2] 게시글 정상 생성 및 이미지 업로드 후 해당 게시글 조회 시 업로드된 이미지가 정상 조회 되는지 검증*/
    @Order(2)
    @DisplayName("2. 게시글 생성 및 이미지 업로드 후 해당 게시글 조회 시 응답 검증")
    @Test
    public void uploadImage_shouldReturnUploadImageData_whenRequestGetPost() throws Exception {
      //given
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);
      CommonResponse<CreatePostApiResponse> createContentResponse = sendCreatePostRequest(
          accessToken, "content", status().isCreated()
      );
      String postId = createContentResponse.getData().postId();
      Path path = Paths.get("src/test/resources/images/sample/01.jpg");
      MockMultipartFile image = new MockMultipartFile(
          "images",
          path.getFileName().toString(),
          MediaType.IMAGE_JPEG.toString(),
          Files.readAllBytes(path)
      );
      sendUploadPostImage(
          accessToken, postId, List.of(image), status().isCreated()
      );

      CommonResponse<GetPostDetailApiResponse> getPostRequest = sendGetPostRequest(
          accessToken,
          postId,
          status().isOk()
      );
    }

    //when

    //then
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Case #1] 게시글 작성자와 일치하지 않는 사용자가 요청 시 */
    @Order(1)
    @DisplayName("1. 게시글 작성자와 요청자가 일치하지 않는 경우 403 ForBidden")
    @Test
    public void uploadImage_shouldForBidden_whenPostWriterNotEqualsUser() throws Exception {
      //given

      String user1 = EMAIL_VERIFIED_USER_1;
      String user2 = EMAIL_VERIFIED_USER_2;

      /*user1 로그인 후 게시글 생성*/
      String accessToken1 = getAccessToken(user1);
      CommonResponse<CreatePostApiResponse> createPostResponse = sendCreatePostRequest(
          accessToken1, "content", status().isCreated()
      );

      String postId = createPostResponse.getData().postId();

      //when
      /*user2 로그인 후 게시글 업로드 요청*/
      String accessToken2 = getAccessToken(user2);
      List<MockMultipartFile> images = generateMultipartFiles(1, "01.jpg");

      //then
      CommonResponse<UploadPostImagesApiResponse> uploadImageResponse = sendUploadPostImage(
          accessToken2,
          postId, images, status().isForbidden()
      );
      assertThat(uploadImageResponse.getCode()).isEqualTo(
          ErrorCode.POST_IMAGE_UPLOAD_FORBIDDEN.getCode());
    }

    /*[Case #2] 존재하지 않는 게시글에 대한 업로드 요청 시 400 Bad Request */
    @Order(2)
    @DisplayName("2. 존재하지 않는 게시글에 대한 업로드 요청 시 400 Bad Request")
    @Test
    public void uploadImage_shouldReturnBadRequest_whenPostNotExists() throws Exception {
      //given
      /*로그인 요청*/
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      /*이미지 생성*/
      List<MockMultipartFile> images = generateMultipartFiles(1, "01.jpg");

      //then
      /*업로드 요청*/
      CommonResponse<UploadPostImagesApiResponse> uploadImageResponse = sendUploadPostImage(
          accessToken,
          "content", images, status().isNotFound());
      assertThat(uploadImageResponse.getCode()).isEqualTo(ErrorCode.POST_NOT_FOUND.getCode());
    }

    /*[Case #3] 이미지 업로드 - IMAGE_MAX_COUNT 이상의 수를 업로드 시 400 Bad Request */
    @Order(3)
    @DisplayName("3. IMAGE_MAX_COUNT 이상의 사진 업로드 시 400 Bad Request")
    @Test
    public void uploadImage_shouldBadRequest_whenUploadImageCountMoreThenIMAGE_MAX_COUNT()
        throws Exception {
      //given
      /*로그인 요청*/
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      /*게시글 생성*/
      CommonResponse<CreatePostApiResponse> createPostResponse = sendCreatePostRequest(
          accessToken,
          "content",
          status().isCreated()
      );

      String postId = createPostResponse.getData().postId();

      //when
      //then

      List<MockMultipartFile> images = generateMultipartFiles(
          UploadPostImageTest.this.uploadProperties.getMaxImageCount(), "01.jpg");

      CommonResponse<UploadPostImagesApiResponse> uploadResponse = sendUploadPostImage(
          accessToken, postId, images, status().isBadRequest());

      assertThat(uploadResponse.getCode()).isEqualTo(
          ErrorCode.POST_IMAGE_UPLOAD_LIMIT_EXCEEDED.getCode());
    }
  }
}

