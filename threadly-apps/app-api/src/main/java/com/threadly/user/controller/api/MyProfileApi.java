package com.threadly.user.controller.api;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.core.port.user.in.profile.command.dto.RegisterMyProfileApiResponse;
import com.threadly.core.port.user.in.profile.image.dto.UploadMyProfileImageApiResponse;
import com.threadly.core.port.user.in.profile.query.dto.GetMyProfileDetailsApiResponse;
import com.threadly.user.request.me.RegisterUserProfileRequest;
import com.threadly.user.request.me.UpdateMyPrivacySettingRequest;
import com.threadly.user.request.me.UpdateMyProfileRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "내 프로필 API", description = "사용자 본인의 프로필 관리 API")
public interface MyProfileApi {

  /**
   * 내 프로필 수정용 정보 조회
   */
  @Operation(summary = "내 프로필 상세 조회", description = "프로필 수정을 위한 내 상세 정보를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공",
          content = @Content(schema = @Schema(implementation = GetMyProfileDetailsApiResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
  })
  @GetMapping("")
  ResponseEntity<GetMyProfileDetailsApiResponse> getMyProfileDetails(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user);

  /**
   * 프로필 초기 설정
   */
  @Operation(summary = "프로필 초기 설정", description = "회원가입 후 처음으로 프로필을 설정합니다. 새로운 JWT 토큰이 발급됩니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "프로필 설정 성공",
          content = @Content(schema = @Schema(implementation = RegisterMyProfileApiResponse.class))),
      @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 데이터", content = @Content),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
      @ApiResponse(responseCode = "409", description = "이미 존재하는 닉네임", content = @Content)
  })
  @PostMapping("")
  ResponseEntity<RegisterMyProfileApiResponse> setMyProfile(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @Parameter(description = "프로필 초기 설정 요청 정보", required = true)
      @RequestBody RegisterUserProfileRequest request);

  /**
   * 프로필 업데이트
   */
  @Operation(summary = "프로필 수정", description = "기존 프로필 정보를 수정합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "프로필 수정 성공", content = @Content),
      @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 데이터", content = @Content),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
      @ApiResponse(responseCode = "409", description = "이미 존재하는 닉네임", content = @Content)
  })
  @PatchMapping("")
  ResponseEntity<Void> updateMyProfile(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @Parameter(description = "프로필 수정 요청 정보", required = true)
      @RequestBody UpdateMyProfileRequest request);

  /**
   * 프라이버시 설정 업데이트
   */
  @Operation(summary = "프라이버시 설정", description = "계정 공개/비공개 설정을 변경합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "설정 변경 성공", content = @Content),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
  })
  @PatchMapping("/privacy")
  ResponseEntity<Void> updatePrivacySetting(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @Parameter(description = "프라이버시 설정 요청 정보", required = true)
      @RequestBody UpdateMyPrivacySettingRequest request);

  /**
   * 프로필 이미지 업로드
   */
  @Operation(summary = "프로필 이미지 업로드", description = "새로운 프로필 이미지를 업로드합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "이미지 업로드 성공",
          content = @Content(schema = @Schema(implementation = UploadMyProfileImageApiResponse.class))),
      @ApiResponse(responseCode = "400", description = "유효하지 않은 이미지 파일", content = @Content),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
  })
  @PostMapping("/image")
  ResponseEntity<UploadMyProfileImageApiResponse> uploadMyProfileImage(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @Parameter(description = "업로드할 프로필 이미지 파일")
      @RequestParam(value = "image", required = false) MultipartFile image);

  /**
   * 닉네임 중복 확인
   */
  @Operation(summary = "닉네임 중복 확인", description = "닉네임 사용 가능 여부를 확인합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "사용 가능한 닉네임", content = @Content),
      @ApiResponse(responseCode = "409", description = "이미 사용 중인 닉네임", content = @Content)
  })
  @GetMapping("/check")
  ResponseEntity<Void> checkNickname(
      @Parameter(description = "확인할 닉네임", required = true, example = "john_doe")
      @RequestParam("nickname") String nickName);

}
