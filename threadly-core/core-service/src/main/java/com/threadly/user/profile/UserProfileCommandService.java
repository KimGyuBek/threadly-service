package com.threadly.user.profile;

import com.threadly.exception.ErrorCode;
import com.threadly.exception.user.UserException;
import com.threadly.user.FetchUserPort;
import com.threadly.user.SaveUserPort;
import com.threadly.user.User;
import com.threadly.user.UserProfile;
import com.threadly.user.profile.register.RegisterUserProfileCommand;
import com.threadly.user.profile.register.RegisterUserProfileUseCase;
import com.threadly.user.profile.save.SaveUserProfilePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 사용자 프로필 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileCommandService implements RegisterUserProfileUseCase {

  private final SaveUserProfilePort saveUserProfilePort;


  /**
   * 사용자 프로필 등록
   *
   * @param command
   */
  @Override
  public void registerUserProfile(RegisterUserProfileCommand command) {
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

//  /**
//   * TODO 이건 upsert 임
//   * @param command
//   */
//  @Transactional
//  @Override
//  public void registerUserProfile(RegisterUserProfileCommand command) {
//    /*userId로 user 조회*/
//    User user = fetchUserPort.findByUserIdWithUserProfile(command.getUserId())
//        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
//
//    /*조회한 user의 profile이 있는지 검증*/
//    /*있으면 대치  */
//    if (user.hasUserProfile()) {
//      user = updateExistingProfile(command, user);
//
//    } else {
//      /*없으면 생성*/
//      user = createNewProfile(command, user);
//    }
//  }
//
//  /**
//   * 새로운 profile 생성
//   *
//   * @param command
//   * @param user
//   */
//  private User createNewProfile(RegisterUserProfileCommand command, User user) {
//    user.setProfile(
//        command.getNickname(),
//        command.getStatusMessage(),
//        command.getBio(),
//        command.getGender(),
//        command.getProfileImageUrl(),
//        UserProfileType.USER
//    );
//
//    saveUserPort.saveUserProfile(user, user.getUserProfile());
//
//    return user;
//  }
//
//  /**
//   * profile 업데이트
//   *
//   * @param command
//   * @param user
//   */
//  private User updateExistingProfile(RegisterUserProfileCommand command, User user) {
//    UserProfile userProfile = fetchUserPort.findUserProfileByUserProfileId(
//            user.getUserProfileId())
//        .orElseThrow(() -> new UserException(ErrorCode.USER_PROFILE_NOT_FOUND));
//
//    user.updateUserProfile(
//        command.getNickname(),
//        command.getStatusMessage(),
//        command.getBio(),
//        command.getGender(),
//        command.getProfileImageUrl()
//    );
//
//    /*저장*/
//    saveUserPort.saveUserProfile(user, userProfile);
//
//    return user;
//  }
}

