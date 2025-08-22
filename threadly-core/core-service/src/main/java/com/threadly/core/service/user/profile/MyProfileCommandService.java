package com.threadly.core.service.user.profile;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.port.user.UpdateUserPort;
import com.threadly.core.domain.user.User;
import com.threadly.core.domain.user.UserStatusType;
import com.threadly.core.port.user.profile.fetch.FetchUserProfilePort;
import com.threadly.core.usecase.user.profile.command.dto.RegisterMyProfileCommand;
import com.threadly.core.usecase.user.profile.command.RegisterMyProfileUseCase;
import com.threadly.core.port.user.profile.save.SaveUserProfilePort;
import com.threadly.core.usecase.user.profile.command.dto.UpdateMyProfileCommand;
import com.threadly.core.port.user.profile.update.UpdateMyProfilePort;
import com.threadly.core.usecase.user.profile.command.UpdateMyProfileUseCase;
import com.threadly.core.domain.user.profile.UserProfile;
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
public class MyProfileCommandService implements RegisterMyProfileUseCase,
    UpdateMyProfileUseCase {

  private final SaveUserProfilePort saveUserProfilePort;
  private final FetchUserProfilePort fetchUserProfilePort;
  private final UpdateMyProfilePort updateMyProfilePort;

  private final UpdateUserPort updateUserPort;

  @Transactional
  @Override
  public void registerMyProfile(RegisterMyProfileCommand command) {
    /*닉네임 중복 검증*/
    if (fetchUserProfilePort.existsByNickname(command.getNickname())) {
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

    saveUserProfilePort.saveUserProfile(userProfile);

    /*user statusType   변경*/
    updateUserPort.updateUserStatus(command.getUserId(), UserStatusType.ACTIVE);
    log.debug("userProfile 생성 완료");
  }

  @Transactional
  @Override
  public void updateMyProfile(UpdateMyProfileCommand command) {
    /* 닉네임 중복 검증*/
    if (fetchUserProfilePort.existsByNickname(command.getNickname())) {
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
    updateMyProfilePort.updateMyProfile(user.getUserProfile());

    /*phone 업데이트*/
    updateUserPort.updateUserPhone(user.getUserId(), user.getUserProfile().getPhone());
  }
}

