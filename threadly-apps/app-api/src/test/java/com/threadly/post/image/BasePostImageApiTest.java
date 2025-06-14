package com.threadly.post.image;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.CommonResponse;
import com.threadly.post.controller.BasePostApiTest;
import com.threadly.properties.UploadProperties;
import com.threadly.utils.TestLogUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
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
  public UploadProperties uploadProperties;


  /**
   * 이미지 파일 목록 생성
   *
   * @param imageCount
   * @param fileName
   * @return
   * @throws IOException
   */
  public List<MockMultipartFile> generateMultipartFiles(int imageCount, String fileName)
      throws IOException {
    List<MockMultipartFile> images = new ArrayList<>();

    for (int i = 0; i < imageCount; i++) {
      Path path = Paths.get("src/test/resources/images/sample/" + fileName);
      images.add(new MockMultipartFile(
          "images",
          path.getFileName().toString(),
          MediaType.IMAGE_JPEG.toString(),
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
      String postId, List<MockMultipartFile> images, ResultMatcher expected)
      throws Exception {
    MockMultipartHttpServletRequestBuilder builder = (MockMultipartHttpServletRequestBuilder) multipart(
        "/api/post-images")
        .param("postId", postId)
        .header("Authorization", "Bearer " + accessToken)
        .contentType(MediaType.MULTIPART_FORM_DATA);

    images.forEach(builder::file);

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

}

