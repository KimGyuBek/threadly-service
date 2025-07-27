package com.threadly.user.controller;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.post.mapper.ImageMapper;
import com.threadly.user.profile.image.SetProfileImageApiResponse;
import com.threadly.user.profile.image.SetProfileImageCommand;
import com.threadly.user.profile.image.SetUserProfileImageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * 사용자 프로필 이미지 관련 컨트롤러
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/user/profile/image")
public class UserProfileImageController {

  private final SetUserProfileImageUseCase setUserProfileImageUseCase;

  /**
   * 사용자 프로필 설정
   *
   * @param user
   * @param image
   * @return
   */
  @PutMapping()
  public ResponseEntity<SetProfileImageApiResponse> setProfileImage(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestParam(value = "image", required = false) MultipartFile image
  ) {

    return ResponseEntity.status(201).body(
        setUserProfileImageUseCase.setProfileImage(
            new SetProfileImageCommand(
                user.getUserId(),
                ImageMapper.toUploadImage(image)))
    );
  }


}
