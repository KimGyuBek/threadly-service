package com.threadly.user.controller;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.user.account.DeactivateMyAccountUseCase;
import com.threadly.user.account.WithdrawMyAccountUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 계정 관련 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me/account")
public class MyAccountController {

  private final WithdrawMyAccountUseCase withdrawMyAccountUsecase;
  private final DeactivateMyAccountUseCase deactivateMyAccountUseCase;

  /**
   * 사용자 비밀번호 변경
   *
   * @return
   */
  @PatchMapping("/password")
  public ResponseEntity<Void> changePassword() {

    return ResponseEntity.noContent().build();
  }

  /**
   * 내 계정 탈퇴
   *
   * @return
   */
  @DeleteMapping("")
  public ResponseEntity<Void> withdrawMyAccount(@RequestHeader("Authorization") String bearerToken,
      @AuthenticationPrincipal JwtAuthenticationUser user) {
    withdrawMyAccountUsecase.withdrawMyAccount(user.getUserId(), bearerToken);

    return ResponseEntity.status(200).build();
  }

  /**
   * 내 계정 비활성화
   *
   * @param user
   * @return
   */
  @PatchMapping("/deactivate")
  public ResponseEntity<Void> deactivateMyAccount(
      @RequestHeader("Authorization") String bearerToken,
      @AuthenticationPrincipal JwtAuthenticationUser user
  ) {
    deactivateMyAccountUseCase.deactivateMyAccount(user.getUserId(), bearerToken);
    return ResponseEntity.status(200).build();
  }

}
