package com.threadly.auth.controller.api;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.auth.request.PasswordVerificationRequest;
import com.threadly.auth.request.UserLoginRequest;
import com.threadly.core.port.auth.in.token.response.LoginTokenApiResponse;
import com.threadly.core.port.auth.in.token.response.TokenReissueApiResponse;
import com.threadly.core.port.auth.in.verification.response.PasswordVerificationToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "인증 API", description = "사용자 인증 관련 API")
public interface AuthApi {

  /**
   * 로그인
   */
  @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "로그인 성공",
          content = @Content(schema = @Schema(implementation = LoginTokenApiResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패 (잘못된 이메일 또는 비밀번호)", content = @Content),
      @ApiResponse(responseCode = "403", description = "계정 잠김 (로그인 시도 초과)", content = @Content)
  })
  @PostMapping("/login")
  LoginTokenApiResponse login(
      @Parameter(description = "로그인 요청 정보", required = true)
      @RequestBody UserLoginRequest userLoginRequest);

  /**
   * 로그아웃
   */
  @Operation(summary = "로그아웃", description = "현재 세션의 액세스 토큰을 무효화합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
  })
  @PostMapping("/logout")
  void logout(
      @Parameter(description = "Bearer 액세스 토큰")
      @RequestHeader(value = "Authorization", required = false) String accessToken);

  /**
   * refreshToken으로 login Token 재발급
   */
  @Operation(summary = "토큰 재발급", description = "리프레시 토큰으로 새로운 액세스 토큰과 리프레시 토큰을 발급받습니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
          content = @Content(schema = @Schema(implementation = TokenReissueApiResponse.class))),
      @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰", content = @Content)
  })
  @PostMapping("/reissue")
  TokenReissueApiResponse reissueAccessToken(
      @Parameter(description = "리프레시 토큰", required = true)
      @RequestHeader(value = "X-refresh-token", required = false) String refreshToken);

  /**
   * 이메일 인증
   */
  @Operation(summary = "이메일 인증", description = "회원가입 시 전송된 이메일의 인증 코드로 이메일을 인증합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "이메일 인증 성공", content = @Content),
      @ApiResponse(responseCode = "400", description = "유효하지 않은 인증 코드", content = @Content),
      @ApiResponse(responseCode = "404", description = "인증 코드를 찾을 수 없음", content = @Content)
  })
  @GetMapping("/verify-email")
  void verifyMail(
      @Parameter(description = "이메일 인증 코드", required = true, example = "abc123xyz")
      @RequestParam String code);

  /**
   * 2FA 인증 (비밀번호 재확인)
   */
  @Operation(summary = "비밀번호 인증", description = "중요한 작업 수행 전 비밀번호를 재확인하여 인증 토큰을 발급받습니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "비밀번호 인증 성공",
          content = @Content(schema = @Schema(implementation = PasswordVerificationToken.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패 또는 비밀번호 불일치", content = @Content)
  })
  @PostMapping("/verify-password")
  PasswordVerificationToken verifyPassword(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @Parameter(description = "비밀번호 인증 요청 정보", required = true)
      @RequestBody PasswordVerificationRequest request);

}
