package com.threadly.post.image;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.CommonResponse;
import com.threadly.entity.post.PostImageEntity;
import com.threadly.post.PostCommentStatus;
import com.threadly.post.PostImageStatus;
import com.threadly.post.controller.BasePostApiTest;
import com.threadly.post.create.CreatePostApiResponse;
import com.threadly.post.create.CreatePostApiResponse.PostImageApiResponse;
import com.threadly.post.image.UploadPostImagesApiResponse.PostImageResponse;
import com.threadly.post.request.CreatePostRequest.ImageRequest;
import com.threadly.properties.UploadProperties;
import com.threadly.repository.post.PostImageJpaRepository;
import com.threadly.repository.post.PostLikeJpaRepository;
import com.threadly.repository.post.comment.CommentLikeJpaRepository;
import com.threadly.repository.post.comment.PostCommentJpaRepository;
import com.threadly.utils.TestLogUtils;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

/**
 * 게시글 이미지 업로드 테스트 관련
 */
public abstract class BasePostImageApiTest extends BasePostApiTest {

  public static final String UPLOAD_PATH = "src/test/resources/images/temp";

  @Autowired
  private PostImageJpaRepository postImageJpaRepository;

  @Autowired
  private PostLikeJpaRepository postLikeJpaRepository;

  @Autowired
  private PostCommentJpaRepository postCommentJpaRepository;

  @Autowired
  private CommentLikeJpaRepository commentLikeJpaRepository;

  @Autowired
  public UploadProperties uploadProperties;

  /**
   * 테스트 파일 삭제
   *
   * @throws IOException
   */
  public void clearFiles() throws IOException {
    cleanUpDirectoryContents();
  }


  /**
   * 특정 비율에 맞는 이미지 생성
   *
   * @param imageName
   * @param format
   * @param width
   * @param height
   * @return
   */
  public MockMultipartFile generateImageWithRatio(String imageName, String format, int width,
      int height)
      throws IOException {
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    ByteArrayOutputStream boas = new ByteArrayOutputStream();
    ImageIO.write(image, format, boas);

    return new MockMultipartFile(
        "images",
        imageName,
        "image/" + format,
        boas.toByteArray()
    );
  }

  /**
   * 이미지 파일 목록 생성
   *
   * @param imageCount
   * @param fileName
   * @param name
   * @param mediaType
   * @return
   * @throws IOException
   */
  public List<MockMultipartFile> generateMultipartFiles(int imageCount, String fileName,
      String name, String mediaType)
      throws IOException {
    List<MockMultipartFile> images = new ArrayList<>();

    for (int i = 0; i < imageCount; i++) {
      Path path = Paths.get("src/test/resources/images/sample/" + fileName);
      images.add(new MockMultipartFile(
          name,
          path.getFileName().toString(),
          mediaType,
          Files.readAllBytes(path)
      ));
    }
    return images;
  }

  /**
   * 이미지 업로드 요청
   *
   * @param accessToken
   * @param postId
   * @param images
   * @param expected
   * @return
   * @throws Exception
   */
  public CommonResponse<UploadPostImagesApiResponse> sendUploadPostImage(String accessToken,
      List<MockMultipartFile> images, ResultMatcher expected)
      throws Exception {
    MockMultipartHttpServletRequestBuilder builder = (MockMultipartHttpServletRequestBuilder) multipart(
        "/api/post-images")
        .header("Authorization", "Bearer " + accessToken)
        .contentType(MediaType.MULTIPART_FORM_DATA);

    if (images != null) {
      images.forEach(builder::file);
    }

    MvcResult result = mockMvc.perform(builder).andExpect(expected).andReturn();

    TestLogUtils.log(result);
    return
        getResponse(result, new TypeReference<>() {
        });
  }


  /**
   * 저장된 업로드 파일 삭제
   */
  public static void cleanUpDirectoryContents() throws IOException {
    Path uploadPath = Paths.get(UPLOAD_PATH);
    if (Files.exists(uploadPath)) {
      try (Stream<Path> files = Files.list(uploadPath)) {
        files.forEach(file -> {
          try {
            Files.deleteIfExists(file);
          } catch (IOException e) {
            System.out.println("파일 삭제 실패 " + file.getFileName());
          }
        });
      }
    }
  }

  /**
   * 게시글 생성 후 postId 리턴
   *
   * @throws Exception
   */
  public String getPostId(String accessToken) throws Exception {

    /*2. 게시글 생성 요청*/
    String content = "content1";
    CommonResponse<CreatePostApiResponse> createPostResponse = sendCreatePostRequest(
        accessToken,
        content,
        List.of(),
        status().isCreated()
    );
    return createPostResponse.getData().postId();
  }


  /**
   * 이미지 포함 게시글 생성 요청
   *
   * @param accessToken
   * @param images
   * @return
   * @throws Exception
   */
  public CommonResponse<CreatePostApiResponse> sendCreatePostWithImages(
      String accessToken, List<PostImageResponse> images)
      throws Exception {
    List<ImageRequest> imageRequest = new ArrayList<>();
    int i = 0;
    for (PostImageResponse postImageResponse : images) {
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

  /**
   * uploadImage 목록 생성
   *
   * @param SIZE
   * @param width
   * @param height
   * @return
   * @throws IOException
   */
  public List<MockMultipartFile> generateUploadImagesWithRatio(int SIZE, int width, int height)
      throws IOException {
    List<MockMultipartFile> images = new ArrayList<>();
    for (int i = 0; i < SIZE; i++) {
      images.add(
          generateImageWithRatio(
              "0" + (i + 1) + ".jpg", "jpeg", width, height
          )
      );
    }
    return images;
  }

  /*JPA 검증 로직*/

  /**
   * 게시글 생성 응답과 db 데이터 검증
   *
   * @param createPostResponse
   * @param postId
   * @param expectedStatus
   */
  public void validateImageResponse(CommonResponse<CreatePostApiResponse> createPostResponse,
      String postId, PostImageStatus expectedStatus) {
    if (createPostResponse.getData().images().isEmpty()) {
      return;
    }

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
          assertThat(postImageEntity.getStatus()).isEqualTo(expectedStatus);
        }
    );
  }

  /**
   * 게시글 좋아요 삭제 검증
   *
   * @param postId
   * @param expectedLikeCount
   */
  public void validatePostLike(String postId, int expectedLikeCount) {
    assertThat(postLikeJpaRepository.findAllByPostId(postId)).isEqualTo(expectedLikeCount);
  }

  /**
   * status, postId에 해당하는 데이터 수 검증
   *
   * @param postId
   * @param expectedStatus
   * @param expectedCount
   */
  public void validateCommentCountByStatusAndPostId(String postId,
      PostCommentStatus expectedStatus, long expectedCount) {
    long result = postCommentJpaRepository.countByStatusAndPostId(expectedStatus, postId);
    assertThat(result).isEqualTo(expectedCount);
  }

  /**
   * postId에 해당하는 댓글 좋아요 수 검증
   *
   * @param postId
   */
  public void validateCommentLikeCountByPostId(String postId, int expectedLikeCount) {
    long result = commentLikeJpaRepository.countByComment_Post_PostId(postId);
    assertThat(result).isEqualTo(expectedLikeCount);
  }

}

