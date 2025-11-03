package com.threadly.core.service.user.profile.image;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserProfileImageException;
import com.threadly.commons.file.UploadImage;
import com.threadly.core.domain.image.ImageStatus;
import com.threadly.core.domain.user.profile.image.UserProfileImage;
import com.threadly.core.port.image.UploadImagePort;
import com.threadly.core.port.image.UploadImageResponse;
import com.threadly.core.port.user.in.profile.image.dto.SetMyProfileImageCommand;
import com.threadly.core.port.user.in.profile.image.dto.UploadMyProfileImageApiResponse;
import com.threadly.core.port.user.out.profile.image.UserProfileCommandPort;
import com.threadly.core.port.user.out.profile.image.UserProfileImageQueryPort;
import com.threadly.core.service.validator.image.ImageAspectRatioValidator;
import com.threadly.core.service.validator.image.ImageUploadValidator;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;
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
 * ProfileImageCommandCommandService 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class ProfileImageCommandCommandServiceTest {

  @InjectMocks
  private ProfileImageCommandCommandService profileImageCommandCommandService;

  @Mock
  private UploadImagePort uploadImagePort;

  @Mock
  private UserProfileCommandPort userProfileCommandPort;

  @Mock
  private UserProfileImageQueryPort userProfileImageQueryPort;

  @Mock
  private ImageUploadValidator imageUploadValidator;

  @Mock
  private ImageAspectRatioValidator imageAspectRatioValidator;


  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("프로필 이미지 업로드")
  class SetMyProfileImageTest {

    /*[Case #1] 프로필 이미지 업로드*/
    @Order(1)
    @DisplayName("1. 프로필 이미지를 업로드하면 메타데이터가 저장되는지 검증")
    @Test
    void setMyProfileImage_shouldUploadAndSaveMetadata() throws Exception {
      //given
      UploadImage uploadImage = UploadImage.builder()
          .originalFileName("profile.jpg")
          .storedFileName("stored.jpg")
          .content(new byte[]{1})
          .contentType("image/jpeg")
          .size(1024)
          .inputStream(new ByteArrayInputStream(new byte[]{1}))
          .build();

      SetMyProfileImageCommand command = new SetMyProfileImageCommand("user-1", uploadImage);

      when(uploadImagePort.uploadProfileImage(uploadImage))
          .thenReturn(new UploadImageResponse("stored-1", "/profiles/1"));

      ArgumentCaptor<UserProfileImage> imageCaptor = ArgumentCaptor.forClass(UserProfileImage.class);

      //when
      UploadMyProfileImageApiResponse response =
          profileImageCommandCommandService.setMyProfileImage(command);

      //then
      verify(imageUploadValidator).validate(List.of(uploadImage));
      verify(uploadImagePort).uploadProfileImage(uploadImage);
      verify(userProfileCommandPort).create(imageCaptor.capture());

      UserProfileImage savedImage = imageCaptor.getValue();
      assertThat(savedImage.getImageUrl()).isEqualTo("/profiles/1");
      assertThat(response.imageUrl()).isEqualTo("/profiles/1");
      assertThat(response.userProfileImageId()).isEqualTo(savedImage.getUserProfileImageId());
    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("프로필 이미지 갱신")
  class UpdateProfileImageTest {

    /*[Case #1] 기존 이미지에서 새로운 이미지로 변경*/
    @Order(1)
    @DisplayName("1. 기존 이미지에서 새로운 이미지로 변경되는지 검증")
    @Test
    void updateProfileImage_shouldReplacePreviousImage() throws Exception {
      //given
      String userId = "user-1";
      String newImageId = "image-new";

      when(userProfileImageQueryPort.existsNotDeletedByUserProfileImageId(newImageId))
          .thenReturn(true);
      when(userProfileImageQueryPort.findConfirmedProfileImageIdByUserId(userId))
          .thenReturn(Optional.of("image-old"));

      //when
      profileImageCommandCommandService.updateProfileImage(userId, newImageId);

      //then
      verify(userProfileCommandPort).updateStatusById("image-old", ImageStatus.DELETED);
      verify(userProfileCommandPort).updateStatusAndUserIdByImageId(newImageId, userId,
          ImageStatus.CONFIRMED);
    }

    /*[Case #2] 기존 이미지 유지*/
    @Order(2)
    @DisplayName("2. 기존 이미지와 동일한 경우 변경이 수행되지 않는지 검증")
    @Test
    void updateProfileImage_shouldDoNothing_whenSameImage() throws Exception {
      //given
      String userId = "user-1";
      String imageId = "image-same";

      when(userProfileImageQueryPort.existsNotDeletedByUserProfileImageId(imageId))
          .thenReturn(true);
      when(userProfileImageQueryPort.findConfirmedProfileImageIdByUserId(userId))
          .thenReturn(Optional.of(imageId));

      //when
      profileImageCommandCommandService.updateProfileImage(userId, imageId);

      //then
      verify(userProfileCommandPort, never()).updateStatusById(any(), any());
      verify(userProfileCommandPort, never()).updateStatusAndUserIdByImageId(any(), any(), any());
    }

    /*[Case #3] 기존 이미지를 제거*/
    @Order(3)
    @DisplayName("3. 새 이미지 없이 기존 이미지만 삭제하는 경우 동작을 검증")
    @Test
    void updateProfileImage_shouldDeletePrevious_whenNewImageNull() throws Exception {
      //given
      String userId = "user-1";

      when(userProfileImageQueryPort.findConfirmedProfileImageIdByUserId(userId))
          .thenReturn(Optional.of("image-old"));

      //when
      profileImageCommandCommandService.updateProfileImage(userId, null);

      //then
      verify(userProfileCommandPort).updateStatusById("image-old", ImageStatus.DELETED);
      verify(userProfileCommandPort, never())
          .updateStatusAndUserIdByImageId(any(), any(), any());
    }

    /*[Case #4] 존재하지 않는 이미지 id로 변경*/
    @Order(4)
    @DisplayName("4. 존재하지 않는 이미지 id로 변경하려는 경우 예외가 발생하는지 검증")
    @Test
    void updateProfileImage_shouldThrow_whenImageNotExists() throws Exception {
      //given
      String userId = "user-1";
      String invalidImageId = "image-invalid";

      when(userProfileImageQueryPort.existsNotDeletedByUserProfileImageId(invalidImageId))
          .thenReturn(false);

      //when & then
      assertThatThrownBy(() -> profileImageCommandCommandService.updateProfileImage(userId,
          invalidImageId))
          .isInstanceOf(UserProfileImageException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.USER_PROFILE_IMAGE_NOT_EXISTS);
    }
  }
}
