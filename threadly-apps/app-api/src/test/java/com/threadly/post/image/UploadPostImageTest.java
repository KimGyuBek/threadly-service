package com.threadly.post.image;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.CommonResponse;
import com.threadly.post.controller.BasePostApiTest;
import com.threadly.post.create.CreatePostApiResponse;
import com.threadly.utils.TestConstants;
import com.threadly.utils.TestLogUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.web.servlet.MvcResult;

/**
 * 게시글 이미지 업로드 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("게시글 이미지 업로드 테스트")
public class UploadPostImageTest extends BasePostApiTest {

  private static final String UPLOAD_PATH = "src/test/resources/images/temp";


  @BeforeEach
  void setUp() {
    super.setUpDefaultUser();
  }

  @AfterEach
  void tearDown() throws IOException {
    cleanUpDirectoryContents();
  }


  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    @DisplayName("1. 게시글 생성 후 이미지 1개 업로드시 응답 검증")
    @Test
    public void uploadImage_shouldSucceed_whenOneImageUpload() throws Exception {
      //given

      /*1. 로그인 요청*/
      String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

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
      MockMultipartFile image1 = new MockMultipartFile(
          "images",
          path.getFileName().toString(),
          MediaType.IMAGE_JPEG.toString(),
          Files.readAllBytes(path)
      );

      //then
      CommonResponse<UploadPostImagesApiResponse> uploadImageResponse = sendUploadPostImage(
          image1, postId, accessToken);
      assertThat(uploadImageResponse.getData().images().size()).isEqualTo(1);
    }
  }

  /**
   * 이미지 업로드 요청
   *
   * @param image1
   * @param postId
   * @param accessToken
   * @return
   * @throws Exception
   */
  private CommonResponse<UploadPostImagesApiResponse> sendUploadPostImage(MockMultipartFile image1,
      String postId,
      String accessToken)
      throws Exception {
    MvcResult response = mockMvc.perform(
        multipart("/api/post-images")
            .file(image1)
            .param("postId", postId)
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.MULTIPART_FORM_DATA)
    ).andExpect(status().isOk()).andReturn();
    TestLogUtils.log(response);

    return getResponse(response, new TypeReference<>() {
    });

  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

  }

  /**
   * 저장된 업로드 파일 삭제
   */
  private static void cleanUpDirectoryContents() throws IOException {
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

}

