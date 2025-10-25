package com.threadly.user.controller.me;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.core.port.auth.in.verification.ReissueTokenUseCase;
import com.threadly.core.port.user.in.account.command.UserAccountCommandUseCase;
import com.threadly.core.port.user.in.profile.command.UserProfileCommandUseCase;
import com.threadly.core.port.user.in.profile.command.dto.RegisterMyProfileApiResponse;
import com.threadly.core.port.user.in.profile.image.MyProfileImageCommandUseCase;
import com.threadly.core.port.user.in.profile.image.dto.SetMyProfileImageCommand;
import com.threadly.core.port.user.in.profile.image.dto.UploadMyProfileImageApiResponse;
import com.threadly.core.port.user.in.profile.query.UserProfileQueryUseCase;
import com.threadly.core.port.user.in.profile.query.dto.GetMyProfileDetailsApiResponse;
import com.threadly.post.mapper.ImageMapper;
import com.threadly.user.request.me.RegisterUserProfileRequest;
import com.threadly.user.request.me.UpdateMyPrivacySettingRequest;
import com.threadly.user.request.me.UpdateMyProfileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 사용자 본인의 프로필 관련 controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me/profile")
public class MyProfileController {


  private final UserProfileQueryUseCase userProfileQueryUseCase;

  private final UserProfileCommandUseCase userProfileCommandUseCase;

  private final ReissueTokenUseCase reissueTokenUseCase;

  private final MyProfileImageCommandUseCase myProfileImageCommandUseCase;

  private final UserAccountCommandUseCase userAccountCommandUseCase;


  /**
   * 내 프로필 수정용 정보 조회
   *
   * @return
   */
  @GetMapping("")
  public ResponseEntity<GetMyProfileDetailsApiResponse> getMyProfileDetails(
      @AuthenticationPrincipal JwtAuthenticationUser user
  ) {

    return ResponseEntity.status(200)
        .body(userProfileQueryUseCase.getMyProfileDetails(user.getUserId()));
  }

  /**
   * 사용자 프로필 초기 설정
   *
   * @param user
   * @param request
   * @return
   */
  @PostMapping("")
  public ResponseEntity<RegisterMyProfileApiResponse> setMyProfile(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestBody RegisterUserProfileRequest request) {

    /*프로필 설정*/
    userProfileCommandUseCase.registerMyProfile(request.toCommand(user.getUserId()));

    return ResponseEntity.status(201).body(
        reissueTokenUseCase.reissueToken(user.getUserId())
    );
  }

  /**
   * 사용자 프로필 업데이트
   *
   * @param user
   * @param request
   * @return
   */
  @PatchMapping("")
  public ResponseEntity<Void> updateMyProfile(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestBody UpdateMyProfileRequest request) {

    /*UserProfile 데이터 업데이트*/
    userProfileCommandUseCase.updateMyProfile(request.toCommand(user.getUserId()));

    /*ProfileImage 업데이트*/
    myProfileImageCommandUseCase.updateProfileImage(user.getUserId(), request.profileImageId());

    return ResponseEntity.status(200).build();
  }

  /**
   * 내 계정 비공개 처리
   *
   * @param user
   * @return
   */
  @PatchMapping("/privacy")
  public ResponseEntity<Void> updatePrivacySetting(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestBody UpdateMyPrivacySettingRequest request
  ) {
    userAccountCommandUseCase.updatePrivacy(request.toCommand(user.getUserId()));
    return ResponseEntity.status(200).build();

  }

  /**
   * 사용자 프로필 이미지 업로드
   *
   * @param user
   * @param image
   * @return
   */
  @PostMapping("/image")
  public ResponseEntity<UploadMyProfileImageApiResponse> uploadMyProfileImage(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestParam(value = "image", required = false) MultipartFile image
  ) {

    return ResponseEntity.status(201).body(
        myProfileImageCommandUseCase.setMyProfileImage(
            new SetMyProfileImageCommand(
                user.getUserId(),
                ImageMapper.toUploadImage(image)))
    );
  }

  /**
   * nickname 중복 체크
   *
   * @param nickName
   * @return
   */
  @GetMapping("/check")
  public ResponseEntity<Void> checkNickname(@RequestParam("nickname") String nickName) {
    userProfileQueryUseCase.validateNicknameUnique(nickName);
    return ResponseEntity.status(200).build();
  }
}
