package com.threadly.core.port.user.in.account.command;


import com.threadly.core.port.user.in.account.command.dto.ChangePasswordCommand;
import com.threadly.core.port.user.in.account.command.dto.RegisterUserApiResponse;
import com.threadly.core.port.user.in.account.command.dto.RegisterUserCommand;
import com.threadly.core.port.user.in.account.command.dto.UpdateMyPrivacySettingCommand;

/**
 * 사용자 계정 관련 command usecase
 */
public interface UserAccountCommandUseCase {

  /**
   * 비밀번호 변경
   *
   * @param command
   */
  void changePassword(ChangePasswordCommand command);

  /**
   * 내 계정 탈퇴처리
   *
   * @param userId
   */
  void withdrawMyAccount(String userId, String bearerToken);

  /**
   * 사용자 회원 가입
   *
   * @param request
   * @return
   */
  RegisterUserApiResponse register(RegisterUserCommand request);

  /**
   * 주어진 파라미터로 내 계정 비활성화
   *
   * @param userId
   * @param bearerToken
   */
  void deactivateMyAccount(String userId, String bearerToken);

  /**
   * 내 프로필 공개 여부 관련 요청
   *
   * @param command
   */
  void updatePrivacy(UpdateMyPrivacySettingCommand command);
}
