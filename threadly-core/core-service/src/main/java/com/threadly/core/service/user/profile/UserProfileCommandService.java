package com.threadly.core.service.user.profile;

import com.threadly.core.domain.user.User;
import com.threadly.core.domain.user.UserStatus;
import com.threadly.core.domain.user.profile.UserProfile;
import com.threadly.core.port.user.in.profile.command.UserProfileCommandUseCase;
import com.threadly.core.port.user.in.profile.command.dto.RegisterMyProfileCommand;
import com.threadly.core.port.user.in.profile.command.dto.UpdateMyProfileCommand;
import com.threadly.core.port.user.out.UserCommandPort;
import com.threadly.core.port.user.out.profile.UserProfileCommandPort;
import com.threadly.core.service.validator.user.UserProfileValidator;
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

  private final UserCommandPort userCommandPort;

  private final UserProfileValidator userProfileValidator;

  @Transactional
  @Override
  public void registerMyProfile(RegisterMyProfileCommand command) {
    /*닉네임 중복 검증*/
    userProfileValidator.validateNicknameDuplicate(command.getNickname());

    /*프로필 생성*/
    User user = User.of(command.getUserId());
    UserProfile userProfile = user.setUserProfile(
        command.getNickname(),
        command.getStatusMessage(),
        command.getBio(),
        command.getPhone(),
        command.getGender()
    );

    userProfileCommandPort.saveUserProfile(userProfile);

    /*user statusType   변경*/
    userCommandPort.updateUserStatus(command.getUserId(), UserStatus.ACTIVE);
    log.debug("userProfile 생성 완료");
  }

  @Transactional
  @Override
  public void updateMyProfile(UpdateMyProfileCommand command) {
    /* 닉네임 중복 검증*/
    userProfileValidator.validateNicknameDuplicate(command.getNickname());

    /*userProfile 업데이트*/
    User user = User.emptyWithUserId(command.getUserId());
    user.updateProfile(
        command.getNickname(),
        command.getStatusMessage(),
        command.getBio(),
        command.getPhone()
    );

    userProfileCommandPort.updateMyProfile(user.getUserProfile());
    userCommandPort.updateUserPhone(user.getUserId(), user.getUserProfile().getPhone());
    log.debug("프로필 업데이트 완료");
  }

}

