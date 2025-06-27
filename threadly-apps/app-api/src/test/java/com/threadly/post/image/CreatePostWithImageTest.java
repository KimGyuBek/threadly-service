package com.threadly.post.image;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.entity.post.PostImageEntity;
import com.threadly.post.PostImageStatus;
import com.threadly.post.create.CreatePostApiResponse;
import com.threadly.post.create.CreatePostApiResponse.PostImageApiResponse;
import com.threadly.post.image.UploadPostImagesApiResponse.PostImageResponse;
import com.threadly.post.request.CreatePostRequest.ImageRequest;
import com.threadly.repository.post.PostImageJpaRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
    super.setUpDefaultUser();
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
      List<MockMultipartFile> images = generateUploadImages(SIZE);

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
          uploadImageResponse, accessToken);

      String postId = createPostResponse.getData().postId();

      /*db 조회*/
      /*id 추출*/
      validateResponseToData(createPostResponse, postId);
    }

  }

  /**
   * 게시글 생성 응답과 db 데이터 검증
   *
   * @param createPostResponse
   * @param postId
   */
  private void validateResponseToData(CommonResponse<CreatePostApiResponse> createPostResponse,
      String postId) {
    List<String> uploadedImageIds = createPostResponse.getData().images()
        .stream()
        .map(PostImageApiResponse::imageId)
        .toList();

    /*db  조회*/
    List<PostImageEntity> imageEntities = postImageJpaRepository.findAllById(uploadedImageIds);

    Map<String, PostImageEntity> imageEntityMap = imageEntities.stream().collect(
        Collectors.toMap(PostImageEntity::getPostImageId, it -> it)
    );

    createPostResponse.getData().images().forEach(
        it -> {
          PostImageEntity postImageEntity = imageEntityMap.get(it.imageId());
          assertThat(postImageEntity.getPost().getPostId()).isEqualTo(postId);
          assertThat(postImageEntity.getImageOrder()).isEqualTo(it.imageOrder());
          assertThat(postImageEntity.getImageUrl()).isEqualTo(it.imageUrl());
          assertThat(postImageEntity.getStatus()).isEqualTo(PostImageStatus.CONFIRMED);
        }
    );
  }

  /**
   * 이미지 포함 게시글 생성 요청
   *
   * @param uploadImageResponse
   * @param accessToken
   * @return
   * @throws Exception
   */
  private CommonResponse<CreatePostApiResponse> sendCreatePostWithImages(
      CommonResponse<UploadPostImagesApiResponse> uploadImageResponse, String accessToken)
      throws Exception {
    List<ImageRequest> imageRequest = new ArrayList<>();
    int i = 0;
    for (PostImageResponse postImageResponse : uploadImageResponse.getData().images()) {
      imageRequest.add((new ImageRequest(postImageResponse.imageId(), i++)));
    }

    /*게시글 생성 요청*/
    CommonResponse<CreatePostApiResponse> createPostResponse = sendCreatePostRequest(
        accessToken,
        "content",
        imageRequest,
        status().isCreated()
    );
    return createPostResponse;
  }

  private List<MockMultipartFile> generateUploadImages(int SIZE) throws IOException {
    List<MockMultipartFile> images = new ArrayList<>();
    for (int i = 0; i < SIZE; i++) {
      images.add(
          generateImageWithRatio(
              "0" + (i + 1) + ".jpg", "jpeg", 300, 400
          )
      );
    }
    return images;
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

  }


}
