package com.threadly.core.service.post.image;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.post.PostImageException;
import com.threadly.commons.file.UploadImage;
import com.threadly.commons.properties.UploadProperties;
import com.threadly.core.domain.post.PostImage;
import com.threadly.core.port.image.UploadImagePort;
import com.threadly.core.port.image.UploadImageResponse;
import com.threadly.core.port.post.in.image.UploadPostImageCommand;
import com.threadly.core.port.post.in.image.UploadPostImagesApiResponse;
import com.threadly.core.port.post.out.image.PostImageCommandPort;
import com.threadly.core.service.validator.image.ImageAspectRatioValidator;
import com.threadly.core.service.validator.image.ImageUploadValidator;
import java.io.ByteArrayInputStream;
import java.util.List;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * PostImageUploadService 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class PostImageUploadServiceTest {

  @InjectMocks
  private PostImageUploadService postImageUploadService;

  @Mock
  private UploadImagePort uploadImagePort;

  @Mock
  private UploadProperties uploadProperties;

  @Mock
  private ImageUploadValidator imageUploadValidator;

  @Mock
  private PostImageCommandPort postImageCommandPort;

  @Mock
  private ImageAspectRatioValidator imageAspectRatioValidator;

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 게시글 이미지 업로드*/
    @Order(1)
    @DisplayName("1. 게시글 이미지를 업로드하면 응답과 저장이 정상 수행되는지 검증")
    @Test
    void uploadPostImages_shouldUploadAndPersistMetadata() throws Exception {
      //given
      UploadImage image1 = UploadImage.builder()
          .originalFileName("sample1.jpg")
          .storedFileName("stored1.jpg")
          .content(new byte[]{1})
          .contentType("image/jpeg")
          .size(1024)
          .inputStream(new ByteArrayInputStream(new byte[]{1}))
          .build();
      UploadImage image2 = UploadImage.builder()
          .originalFileName("sample2.jpg")
          .storedFileName("stored2.jpg")
          .content(new byte[]{2})
          .contentType("image/jpeg")
          .size(2048)
          .inputStream(new ByteArrayInputStream(new byte[]{2}))
          .build();

      UploadPostImageCommand command = new UploadPostImageCommand("user-1", List.of(image1, image2));
      when(uploadProperties.getMaxImageCount()).thenReturn(5);
      when(uploadProperties.getMaxImageCount()).thenReturn(5);

      when(uploadImagePort.uploadPostImageList(command.getImages()))
          .thenReturn(List.of(
              new UploadImageResponse("stored1", "/images/1"),
              new UploadImageResponse("stored2", "/images/2")
          ));

      ArgumentCaptor<PostImage> postImageCaptor = ArgumentCaptor.forClass(PostImage.class);

      //when
      UploadPostImagesApiResponse response = postImageUploadService.uploadPostImages(command);

      //then
      verify(imageUploadValidator).validate(command.getImages());
      verify(uploadImagePort).uploadPostImageList(command.getImages());
      verify(postImageCommandPort, org.mockito.Mockito.times(2)).savePostImage(postImageCaptor.capture());

      assertThat(response.images()).hasSize(2);
      assertThat(response.images().getFirst().imageUrl()).isEqualTo("/images/1");
      assertThat(response.images().get(1).imageUrl()).isEqualTo("/images/2");

      List<PostImage> savedImages = postImageCaptor.getAllValues();
      assertThat(savedImages).hasSize(2);
      assertThat(savedImages.getFirst().getImageUrl()).isEqualTo("/images/1");
      assertThat(savedImages.get(1).getImageUrl()).isEqualTo("/images/2");
    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Case #1] 업로드 이미지가 없는 경우*/
    @Order(1)
    @DisplayName("1. 업로드 이미지가 없으면 예외가 발생하는지 검증")
    @Test
    void uploadPostImages_shouldThrow_whenImagesEmpty() throws Exception {
      //given
      UploadPostImageCommand command = new UploadPostImageCommand("user-1", List.of());

      //when & then
      assertThatThrownBy(() -> postImageUploadService.uploadPostImages(command))
          .isInstanceOf(PostImageException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_IMAGE_UPLOAD_LIMIT_EXCEEDED);

      verify(imageUploadValidator).validate(command.getImages());
      verify(uploadImagePort, never()).uploadPostImageList(any());
    }

    /*[Case #2] 업로드 이미지가 최대 허용 수를 초과하는 경우*/
    @Order(2)
    @DisplayName("2. 업로드 이미지가 허용 개수를 초과하면 예외가 발생하는지 검증")
    @Test
    void uploadPostImages_shouldThrow_whenImagesExceedLimit() throws Exception {
      //given
      UploadImage image = UploadImage.builder()
          .originalFileName("sample.jpg")
          .storedFileName("stored.jpg")
          .content(new byte[]{1})
          .contentType("image/jpeg")
          .size(1024)
          .inputStream(new ByteArrayInputStream(new byte[]{1}))
          .build();

      UploadPostImageCommand command = new UploadPostImageCommand("user-1",
          List.of(image, image, image, image));

      when(uploadProperties.getMaxImageCount()).thenReturn(3);

      //when & then
      assertThatThrownBy(() -> postImageUploadService.uploadPostImages(command))
          .isInstanceOf(PostImageException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_IMAGE_UPLOAD_LIMIT_EXCEEDED);

      verify(imageUploadValidator).validate(command.getImages());
      verify(uploadImagePort, never()).uploadPostImageList(any());
    }
  }
}
