package com.threadly.core.service.user.profile;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.domain.user.User;
import com.threadly.core.domain.user.UserStatusType;
import com.threadly.core.domain.user.profile.UserProfile;
import com.threadly.core.port.user.in.profile.command.dto.RegisterMyProfileCommand;
import com.threadly.core.port.user.in.profile.command.dto.UpdateMyProfileCommand;
import com.threadly.core.port.user.in.profile.command.UserProfileCommandUseCase;
import com.threadly.core.port.user.out.UserCommandPort;
import com.threadly.core.port.user.out.profile.UserProfileQueryPort;
import com.threadly.core.port.user.out.profile.UserProfileCommandPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 프로필 command 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileCommandService implements
    UserProfileCommandUseCase {

  private final UserProfileCommandPort userProfileCommandPort;
  private final UserProfileQueryPort userProfileQueryPort;

  private final UserCommandPort userCommandPort;

  @Transactional
  @Override
  public void registerMyProfile(RegisterMyProfileCommand command) {
    /*닉네임 중복 검증*/
    if (userProfileQueryPort.existsByNickname(command.getNickname())) {
      throw new UserException(ErrorCode.USER_NICKNAME_DUPLICATED);
    }

    User user = User.of(command.getUserId());

    /*프로필 생성*/
    UserProfile userProfile = user.setUserProfile(
        command.getNickname(),
        command.getStatusMessage(),
        command.getBio(),
        command.getPhone(),
        command.getGender()
    );

    userProfileCommandPort.saveUserProfile(userProfile);

    /*user statusType   변경*/
    userCommandPort.updateUserStatus(command.getUserId(), UserStatusType.ACTIVE);
    log.debug("userProfile 생성 완료");
  }

  @Transactional
  @Override
  public void updateMyProfile(UpdateMyProfileCommand command) {
    /* 닉네임 중복 검증*/
    if (userProfileQueryPort.existsByNickname(command.getNickname())) {
      throw new UserException(ErrorCode.USER_NICKNAME_DUPLICATED);
    }

    /*userProfile 업데이트*/
    User user = User.emptyWithUserId(command.getUserId());
    user.updateProfile(
        command.getNickname(),
        command.getStatusMessage(),
        command.getBio(),
        command.getPhone()
    );

    /*프로필 업데이트*/
    userProfileCommandPort.updateMyProfile(user.getUserProfile());

    /*phone 업데이트*/
    userCommandPort.updateUserPhone(user.getUserId(), user.getUserProfile().getPhone());
  }

}

