package com.threadly.user.profile;

import com.threadly.exception.ErrorCode;
import com.threadly.exception.user.UserException;
import com.threadly.user.FetchUserPort;
import com.threadly.user.User;
import com.threadly.user.profile.fetch.FetchUserProfilePort;
import com.threadly.user.profile.register.RegisterMyProfileUseCase;
import com.threadly.user.profile.register.RegisterMyProfileCommand;
import com.threadly.user.profile.save.SaveUserProfilePort;
import com.threadly.user.profile.update.UpdateMyProfileCommand;
import com.threadly.user.profile.update.UpdateMyProfileUseCase;
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

  private final FetchUserPort fetchUserPort;


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
    log.debug("userProfile 생성 완료");
  }

  @Transactional
  @Override
  public void updateMyProfile(UpdateMyProfileCommand command) {
    /*1. 닉네임 중복 검증*/
    if (fetchUserProfilePort.existsByNickname(command.getNickname())) {
      throw new UserException(ErrorCode.USER_NICKNAME_DUPLICATED);
    }
    /*2. userprofile 조회*/
    User user = fetchUserPort.findUserWithProfile(command.getUserId());

    /*3. userprofile update*/
    user.updateProfile(
        command.getNickname(),
        command.getStatusMessage(),
        command.getBio(),
        command.getPhone(),
        /*TODO profile image id로 변경*/
//        command.get()
        null
    );

    /*4. 저장*/
    saveUserProfilePort.saveUserProfile(user.getUserProfile());
  }
}

