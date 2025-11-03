package com.threadly.adapter.storage.post.image;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.threadly.commons.file.UploadImage;
import com.threadly.commons.properties.UploadProperties;
import com.threadly.commons.properties.UploadProperties.AccessUrl;
import com.threadly.commons.properties.UploadProperties.Location;
import com.threadly.core.port.image.UploadImageResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LocalImageUploadAdapter 테스트
 */
@ExtendWith(MockitoExtension.class)
class LocalImageUploadAdapterTest {

  @InjectMocks
  private LocalImageUploadAdapter localImageUploadAdapter;

  @Mock
  private UploadProperties uploadProperties;

  @Mock
  private Location location;

  @Mock
  private AccessUrl accessUrl;

  @TempDir
  Path tempDir;

  private String testPostImagePath;
  private String testProfileImagePath;
  private String testPostImageUrl;
  private String testProfileImageUrl;

  @BeforeEach
  void setUp() {
    testPostImagePath = tempDir.resolve("post-images").toString() + "/";
    testProfileImagePath = tempDir.resolve("profile-images").toString() + "/";
    testPostImageUrl = "http://localhost:8080/images/post/";
    testProfileImageUrl = "http://localhost:8080/images/profile/";
  }

  private void setupPostImageMocks() {
    when(uploadProperties.getLocation()).thenReturn(location);
    when(uploadProperties.getAccessUrl()).thenReturn(accessUrl);
    when(location.getPostImage()).thenReturn(testPostImagePath);
    when(accessUrl.getPostImage()).thenReturn(testPostImageUrl);
  }

  private void setupProfileImageMocks() {
    when(uploadProperties.getLocation()).thenReturn(location);
    when(uploadProperties.getAccessUrl()).thenReturn(accessUrl);
    when(location.getProfileImage()).thenReturn(testProfileImagePath);
    when(accessUrl.getProfileImage()).thenReturn(testProfileImageUrl);
  }

  @AfterEach
  void tearDown() throws IOException {
    // 테스트 후 생성된 파일 정리 (TempDir이 자동으로 처리하지만 명시적으로)
    if (Files.exists(tempDir)) {
      Files.walk(tempDir)
          .sorted((a, b) -> b.compareTo(a)) // 역순으로 정렬하여 파일을 먼저 삭제
          .forEach(path -> {
            try {
              Files.deleteIfExists(path);
            } catch (IOException e) {
              // ignore
            }
          });
    }
  }

  @Nested
  @DisplayName("uploadPostImageList 테스트")
  class UploadPostImageListTest {

    /*[Case #1] 게시글 이미지 리스트를 정상적으로 업로드해야 한다*/
    @DisplayName("uploadPostImageList - 이미지 리스트가 정상적으로 업로드되어야 한다")
    @Test
    public void uploadPostImageList_shouldUploadSuccessfully_whenImagesAreValid()
        throws Exception {
      //given
      setupPostImageMocks();
      List<UploadImage> uploadImages = new ArrayList<>();
      uploadImages.add(createTestUploadImage("test1.jpg"));
      uploadImages.add(createTestUploadImage("test2.png"));

      //when
      List<UploadImageResponse> responses = localImageUploadAdapter.uploadPostImageList(
          uploadImages);

      //then
      assertAll(
          () -> assertThat(responses).hasSize(2),
          () -> assertThat(responses.get(0).getStoredName()).endsWith(".jpg"),
          () -> assertThat(responses.get(0).getImageUrl()).startsWith(testPostImageUrl),
          () -> assertThat(responses.get(1).getStoredName()).endsWith(".png"),
          () -> assertThat(responses.get(1).getImageUrl()).startsWith(testPostImageUrl)
      );

      // 실제 파일이 생성되었는지 확인
      String storedFileName1 = responses.get(0).getStoredName();
      String storedFileName2 = responses.get(1).getStoredName();
      assertThat(Files.exists(Paths.get(testPostImagePath, storedFileName1))).isTrue();
      assertThat(Files.exists(Paths.get(testPostImagePath, storedFileName2))).isTrue();
    }

    /*[Case #2] 빈 리스트를 업로드하면 빈 리스트가 반환되어야 한다*/
    @DisplayName("uploadPostImageList - 빈 리스트를 업로드하면 빈 리스트가 반환되어야 한다")
    @Test
    public void uploadPostImageList_shouldReturnEmptyList_whenImageListIsEmpty()
        throws Exception {
      //given
      // 빈 리스트는 mock 필요 없음
      List<UploadImage> uploadImages = new ArrayList<>();

      //when
      List<UploadImageResponse> responses = localImageUploadAdapter.uploadPostImageList(
          uploadImages);

      //then
      assertThat(responses).isEmpty();
    }

    /*[Case #3] 여러 이미지를 업로드할 때 각 이미지가 고유한 파일명을 가져야 한다*/
    @DisplayName("uploadPostImageList - 여러 이미지가 고유한 파일명을 가져야 한다")
    @Test
    public void uploadPostImageList_shouldHaveUniqueFileNames_whenMultipleImagesUploaded()
        throws Exception {
      //given
      setupPostImageMocks();
      List<UploadImage> uploadImages = new ArrayList<>();
      uploadImages.add(createTestUploadImage("image.jpg"));
      uploadImages.add(createTestUploadImage("image.jpg")); // 같은 원본 파일명
      uploadImages.add(createTestUploadImage("image.jpg"));

      //when
      List<UploadImageResponse> responses = localImageUploadAdapter.uploadPostImageList(
          uploadImages);

      //then
      assertThat(responses).hasSize(3);
      String name1 = responses.get(0).getStoredName();
      String name2 = responses.get(1).getStoredName();
      String name3 = responses.get(2).getStoredName();

      // 모든 파일명이 서로 달라야 함
      assertAll(
          () -> assertThat(name1).isNotEqualTo(name2),
          () -> assertThat(name1).isNotEqualTo(name3),
          () -> assertThat(name2).isNotEqualTo(name3)
      );
    }
  }

  @Nested
  @DisplayName("uploadProfileImage 테스트")
  class UploadProfileImageTest {

    /*[Case #1] 프로필 이미지를 정상적으로 업로드해야 한다*/
    @DisplayName("uploadProfileImage - 프로필 이미지가 정상적으로 업로드되어야 한다")
    @Test
    public void uploadProfileImage_shouldUploadSuccessfully_whenImageIsValid() throws Exception {
      //given
      setupProfileImageMocks();
      UploadImage uploadImage = createTestUploadImage("profile.jpg");

      //when
      UploadImageResponse response = localImageUploadAdapter.uploadProfileImage(uploadImage);

      //then
      assertAll(
          () -> assertThat(response.getStoredName()).endsWith(".jpg"),
          () -> assertThat(response.getImageUrl()).startsWith(testProfileImageUrl),
          () -> assertThat(response.getImageUrl()).contains(response.getStoredName())
      );

      // 실제 파일이 생성되었는지 확인
      String storedFileName = response.getStoredName();
      assertThat(Files.exists(Paths.get(testProfileImagePath, storedFileName))).isTrue();
    }

    /*[Case #2] PNG 확장자를 가진 프로필 이미지를 업로드해야 한다*/
    @DisplayName("uploadProfileImage - PNG 확장자 이미지가 정상적으로 업로드되어야 한다")
    @Test
    public void uploadProfileImage_shouldUploadSuccessfully_whenImageIsPng() throws Exception {
      //given
      setupProfileImageMocks();
      UploadImage uploadImage = createTestUploadImage("profile.png");

      //when
      UploadImageResponse response = localImageUploadAdapter.uploadProfileImage(uploadImage);

      //then
      assertThat(response.getStoredName()).endsWith(".png");
      assertThat(Files.exists(Paths.get(testProfileImagePath, response.getStoredName()))).isTrue();
    }
  }

  @Nested
  @DisplayName("파일 확장자 처리 테스트")
  class FileExtensionTest {

    /*[Case #1] 다양한 확장자를 가진 이미지가 올바르게 처리되어야 한다*/
    @DisplayName("다양한 확장자를 가진 이미지가 올바르게 처리되어야 한다")
    @Test
    public void uploadImage_shouldHandleVariousExtensions_whenDifferentFormatsProvided()
        throws Exception {
      //given
      setupProfileImageMocks();
      UploadImage jpegImage = createTestUploadImage("test.jpeg");
      UploadImage jpgImage = createTestUploadImage("test.jpg");
      UploadImage pngImage = createTestUploadImage("test.png");
      UploadImage gifImage = createTestUploadImage("test.gif");

      //when
      UploadImageResponse jpegResponse = localImageUploadAdapter.uploadProfileImage(jpegImage);
      UploadImageResponse jpgResponse = localImageUploadAdapter.uploadProfileImage(jpgImage);
      UploadImageResponse pngResponse = localImageUploadAdapter.uploadProfileImage(pngImage);
      UploadImageResponse gifResponse = localImageUploadAdapter.uploadProfileImage(gifImage);

      //then
      assertAll(
          () -> assertThat(jpegResponse.getStoredName()).endsWith(".jpeg"),
          () -> assertThat(jpgResponse.getStoredName()).endsWith(".jpg"),
          () -> assertThat(pngResponse.getStoredName()).endsWith(".png"),
          () -> assertThat(gifResponse.getStoredName()).endsWith(".gif")
      );
    }
  }

  /**
   * 테스트용 UploadImage 생성
   */
  private UploadImage createTestUploadImage(String originalFileName) {
    byte[] testContent = "test image content".getBytes();
    return UploadImage.builder()
        .originalFileName(originalFileName)
        .storedFileName("test-" + System.nanoTime())
        .content(testContent)
        .contentType("image/jpeg")
        .size(testContent.length)
        .build();
  }
}
