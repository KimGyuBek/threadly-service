package com.threadly.user;

import com.threadly.exception.ErrorCode;
import com.threadly.exception.user.UserException;
import com.threadly.user.command.UserRegistrationCommand;
import com.threadly.user.command.UserSetProfileCommand;
import com.threadly.user.response.UserPortResponse;
import com.threadly.user.response.UserProfileSetupApiResponse;
import com.threadly.user.response.UserRegistrationResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCommandService implements RegisterUserUseCase , UpdateUserUseCase {

  private final SaveUserPort saveUserPort;
  private final FetchUserPort fetchUserPort;


  @Transactional
  @Override
  public UserRegistrationResponse register(UserRegistrationCommand command) {

    /*email로 사용자 조회*/
    Optional<User> byEmail = fetchUserPort.findByEmail(command.getEmail());

    /*이미 존재하는 사용자면*/
    if (byEmail.isPresent()) {
      throw new UserException(ErrorCode.USER_ALREADY_EXISTS);
    }

    /*사용자 생성*/
    User user = User.newUser(
        command.getUserName(),
        command.getPassword(),
        command.getEmail(),
        command.getPhone()
    );

    UserPortResponse userPortResponse = saveUserPort.save(user);

    log.info("회원 가입 성공");

    return UserRegistrationResponse.builder()
        .userId(userPortResponse.getUserId())
        .userName(userPortResponse.getUserName())
        .userType(userPortResponse.getUserType())
        .email(userPortResponse.getEmail())
        .isActive(userPortResponse.isActive())
        .isEmailVerified(userPortResponse.isEmailVerified())
        .build();
  }


  @Transactional
  @Override
  public void upsertUserProfile(UserSetProfileCommand command) {
    /*userId로 user 조회*/
    User user = fetchUserPort.findByUserIdWithUserProfile(command.getUserId())
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    /*조회한 user의 profile이 있는지 검증*/
    /*있으면 대치  */
    if (user.hasUserProfile()) {
      user = updateExistingProfile(command, user);

      /*TODO 도메인을 리턴해버리니깐 생기는 문제! 도메인에도 적용 해줘야지*/

    } else {
      /*없으면 생성*/
      user = createNewProfile(command, user);
    }
  }

  /**
   * 새로운 profile 생성
   *
   * @param command
   * @param user
   */
  private User createNewProfile(UserSetProfileCommand command, User user) {
    user.setProfile(
        command.getNickname(),
        command.getStatusMessage(),
        command.getBio(),
        command.getGender(),
        command.getProfileImageUrl(),
        UserProfileType.USER
    );

    saveUserPort.saveUserProfile(user, user.getUserProfile());

    return user;
  }

  /**
   * profile 업데이트
   *
   * @param command
   * @param user
   */
  private User updateExistingProfile(UserSetProfileCommand command, User user) {
    UserProfile userProfile = fetchUserPort.findUserProfileByUserProfileId(
            user.getUserProfileId())
        .orElseThrow(() -> new UserException(ErrorCode.USER_PROFILE_NOT_FOUND));

    user.updateUserProfile(
        command.getNickname(),
        command.getStatusMessage(),
        command.getBio(),
        command.getGender(),
        command.getProfileImageUrl()
    );

    /*저장*/
    saveUserPort.saveUserProfile(user, userProfile);

    return user;
  }
}