package com.threadly.user.profile.image;

import com.threadly.file.UploadImage;
import com.threadly.image.UploadImagePort;
import com.threadly.image.UploadImageResponse;
import com.threadly.post.image.validator.ImageAspectRatioValidator;
import com.threadly.post.image.validator.ImageUploadValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 사용자 프로필 commandService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileImageCommandService implements SetUserProfileImageUseCase {

  private final UploadImagePort uploadImagePort;

  private final CreateUserProfileImagePort createUserProfileImagePort;

  private final ImageUploadValidator imageUploadValidator;
  private final ImageAspectRatioValidator imageAspectRatioValidator;

  @Override
  public SetProfileImageApiResponse setProfileImage(SetProfileImageCommand command) {
    /*이미지 파일 검증*/
    List<UploadImage> uploadImageToList = List.of(command.getUploadImage());
    imageUploadValidator.validate(uploadImageToList);

    /*이미지 비율 검증*/
//    imageAspectRatioValidator.validate(uploadImageToList);

    /*이미지 파일 저장*/
    UploadImageResponse uploadImageResponse = uploadImagePort.uploadProfileImage(
        command.getUploadImage());
    log.info("이미지 업로드 완료: {}", uploadImageResponse.toString());

    /*이미지 파일 메타 데이터 저장*/
    UserProfileImage userProfileImage = UserProfileImage.newProfileImage(
        uploadImageResponse.getStoredName(),
        uploadImageResponse.getImageUrl());

    createUserProfileImagePort.create(userProfileImage);
    log.debug("이미지 메타 데이터 저장 완료: {}", userProfileImage.toString());

    /*응답 객체 생성*/
    return
        new SetProfileImageApiResponse(
            userProfileImage.getUserProfileImageId(),
            userProfileImage.getImageUrl());
  }
}
