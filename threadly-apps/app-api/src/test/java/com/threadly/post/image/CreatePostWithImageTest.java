package com.threadly.post.image;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.image.ImageStatus;
import com.threadly.post.create.CreatePostApiResponse;
import com.threadly.post.repository.PostImageJpaRepository;
import java.io.IOException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

/**
 * 이미지를 포함한 게시글 생성 API 테스트
 */
@DisplayName("이미지를 포함한 게시글 생성 API 테스트")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class CreatePostWithImageTest extends BasePostImageApiTest {

  @Autowired
  private PostImageJpaRepository postImageJpaRepository;

  @BeforeEach
  void setUp() throws IOException {
    super.clearFiles();
  }


  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    @Order(1)
    @DisplayName("1. 이미지 1개 업로드 검증")
    @Test
    public void createPostWithImages_shouldSucceed_whenUploadOneImage() throws Exception {
      //given
      int SIZE = 1;

      /*1. 로그인*/
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      /*이미지 생성*/
      List<MockMultipartFile> images = generateUploadImagesWithRatio(SIZE, 300, 400);

      /*2. 이미지 업로드*/
      CommonResponse<UploadPostImagesApiResponse> uploadImageResponse = sendUploadPostImage(
          accessToken,
          images,
          status().isCreated()
      );

      //when
      /*3. 이미지 포함 게시글 업로드*/
      /*이미지 요청 생성*/
      CommonResponse<CreatePostApiResponse> createPostResponse = sendCreatePostWithImages(
          accessToken, uploadImageResponse.getData().images());

      String postId = createPostResponse.getData().postId();

      /*db 조회*/
      /*id 추출*/
      validateImageResponse(createPostResponse, postId, ImageStatus.CONFIRMED);
    }

  }


  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

  }


}
