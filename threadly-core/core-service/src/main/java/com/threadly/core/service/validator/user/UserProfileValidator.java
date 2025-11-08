package com.threadly.core.service.validator.user;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.port.user.out.profile.UserProfileQueryPort;
import com.threadly.core.port.user.out.profile.projection.MyProfileDetailsProjection;
import com.threadly.core.port.user.out.profile.projection.UserProfileProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * UserProfile 관련 Validator
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserProfileValidator {

  private final UserProfileQueryPort userProfileQueryPort;

  /**
   * 주어진 nickname의 중복 여부 검증
   *
   * @param nickname
   */
  public void validateNicknameDuplicate(String nickname) {
    if (userProfileQueryPort.existsByNickname(nickname)) {
      log.info("이미 존재하는 닉네임: {}", nickname);

      throw new UserException(ErrorCode.USER_NICKNAME_DUPLICATED);
    }
  }

  /**
   * 주어진 userId에 해당하는 UserProjection 인터페이스 조회
   * <p>
   *   존재하지 않는 경우 예외 발생
   * </p>
   *
   * @param userId
   * @return
   * @throws UserException
   */
  public UserProfileProjection getUserProfileProjectionOrElseThrow(String userId) {
    return userProfileQueryPort.findUserProfileByUserId(
        userId).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND)
    );
  }

  /**
   * 주어진 userId에 해당하는 사용자의 MyProfileDetialsProjection 인터페이스 조회
   * <p>
   *   존재하지 않는 경우 예외 발생
   * </p>
   * @param userId
   * @return
   */
  public MyProfileDetailsProjection getMyProfileDetailsProjectionOrElseThrow(String userId) {
    return userProfileQueryPort.findMyProfileDetailsByUserId(
        userId).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
  }


}
