package com.threadly.user.controller.profile.my.image;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.CommonResponse;
import com.threadly.properties.UploadProperties;
import com.threadly.user.controller.profile.my.BaseMyProfileApiTest;
import com.threadly.user.profile.image.UploadMyProfileImageApiResponse;
import com.threadly.utils.TestLogUtils;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

/**
 * 프로필 이미지 관련 Base Api Test
 */
public abstract class BaseProfileImageApiTest extends BaseMyProfileApiTest {


  public static final String UPLOAD_PATH = "src/test/resources/images/temp";

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
        "image",
        imageName,
        "image/" + format,
        boas.toByteArray()
    );

  }

  /**
   * 프로필 이미지 업로드 요청 전송
   * @param accessToken
   * @param file
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<UploadMyProfileImageApiResponse> sendUploadMyProfileImageRequest(String accessToken,
      MockMultipartFile file, ResultMatcher expectedStatus) throws Exception {
    MockMultipartHttpServletRequestBuilder builder = (MockMultipartHttpServletRequestBuilder) multipart(
        "/api/me/profile/image")
        .header("Authorization", "Bearer " + accessToken)
        .contentType(MediaType.MULTIPART_FORM_DATA);

    builder.file(file);

    MvcResult result = mockMvc.perform(builder).andExpect(expectedStatus).andReturn();
    TestLogUtils.log(result);
    return getResponse(result, new TypeReference<>() {
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
