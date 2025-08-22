package com.threadly.core.service.user.profile.image;

import com.google.common.base.Objects;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserProfileImageException;
import com.threadly.commons.file.UploadImage;
import com.threadly.core.domain.image.ImageStatus;
import com.threadly.core.port.image.UploadImagePort;
import com.threadly.core.port.image.UploadImageResponse;
import com.threadly.core.usecase.user.profile.image.SetMyProfileImageUseCase;
import com.threadly.core.usecase.user.profile.image.UpdateMyProfileImageUseCase;
import com.threadly.core.usecase.user.profile.image.dto.SetMyProfileImageCommand;
import com.threadly.core.usecase.user.profile.image.dto.UploadMyProfileImageApiResponse;
import com.threadly.core.service.validator.image.ImageAspectRatioValidator;
import com.threadly.core.service.validator.image.ImageUploadValidator;
import com.threadly.core.domain.user.profile.image.UserProfileImage;
import com.threadly.core.port.user.profile.image.CreateMyProfileImagePort;
import com.threadly.core.port.user.profile.image.FetchMyProfileImagePort;
import com.threadly.core.port.user.profile.image.UpdateMyProfileImagePort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 프로필 commandService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileImageCommandService implements SetMyProfileImageUseCase,
    UpdateMyProfileImageUseCase {

  private final UploadImagePort uploadImagePort;

  private final CreateMyProfileImagePort createMyProfileImagePort;
  private final FetchMyProfileImagePort fetchMyProfileImagePort;
  private final UpdateMyProfileImagePort updateMyProfileImagePort;

  private final ImageUploadValidator imageUploadValidator;
  private final ImageAspectRatioValidator imageAspectRatioValidator;


  @Override
  public UploadMyProfileImageApiResponse setMyProfileImage(SetMyProfileImageCommand command) {
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

    createMyProfileImagePort.create(userProfileImage);
    log.debug("이미지 메타 데이터 저장 완료: {}", userProfileImage.toString());

    /*응답 객체 생성*/
    return
        new UploadMyProfileImageApiResponse(
            userProfileImage.getUserProfileImageId(),
            userProfileImage.getImageUrl());
  }

  /*
   * 1. 프로필 이미지 유지
   * 2. 프로필 이미지 변경
   * 3. 프로필 이미지 새로 생성
   * 4. 프로필 이미지 삭제
   * */
  @Transactional
  @Override
  public void updateProfileImage(String userId, String profileImageId) {

    /*profileImageId 유효성 검증*/
    if (profileImageId != null && !fetchMyProfileImagePort.existsNotDeletedByUserProfileImageId(
        profileImageId)) {
      throw new UserProfileImageException(ErrorCode.USER_PROFILE_IMAGE_NOT_EXISTS);
    }

    /*기존 이미지 id 조회*/
    String previousProfileImageId = fetchMyProfileImagePort.findConfirmedProfileImageIdByUserId(
        userId).orElse(null);

    /*이미지 변경 없으면 종료*/
    if (Objects.equal(previousProfileImageId, profileImageId)) {
      return;
    }

    /*기존 이미지 삭제*/
    if (previousProfileImageId != null) {
      updateMyProfileImagePort.updateStatusById(previousProfileImageId, ImageStatus.DELETED);
    }
    /*새로운 이미지 설정*/
    if (profileImageId != null) {
      updateMyProfileImagePort.updateStatusAndUserIdByImageId(profileImageId, userId,
          ImageStatus.CONFIRMED);
    }
  }
}
