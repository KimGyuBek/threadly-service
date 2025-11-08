package com.threadly.core.service.user.profile;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.domain.user.UserGenderType;
import com.threadly.core.domain.user.UserStatus;
import com.threadly.core.port.user.in.profile.command.dto.RegisterMyProfileCommand;
import com.threadly.core.port.user.in.profile.command.dto.UpdateMyProfileCommand;
import com.threadly.core.port.user.out.UserCommandPort;
import com.threadly.core.port.user.out.profile.UserProfileCommandPort;
import com.threadly.core.service.user.validator.UserProfileValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserProfileCommandService 테스트
 */
@ExtendWith(MockitoExtension.class)
class UserProfileCommandServiceTest {

  @InjectMocks
  private UserProfileCommandService userProfileCommandService;

  @Mock
  private UserProfileCommandPort userProfileCommandPort;

  @Mock
  private UserProfileValidator userProfileValidator;

  @Mock
  private UserCommandPort userCommandPort;

  @Nested
  @DisplayName("프로필 생성 테스트")
  class RegisterMyProfileTest {

    /*[Case #1] 프로필 생성 성공 - 새로운 프로필 생성*/
    @DisplayName("프로필 생성 성공 - 새로운 프로필이 생성되어야 한다")
    @Test
    public void registerMyProfile_shouldCreateProfile_whenNicknameNotDuplicated()
        throws Exception {
      //given
      RegisterMyProfileCommand command = new RegisterMyProfileCommand(
          "user1",
          "nickname",
          "status message",
          "bio",
          "010-1234-5678",
          UserGenderType.MALE,
          null
      );

      doNothing().when(userProfileValidator).validateNicknameDuplicate(command.getNickname());

      //when
      userProfileCommandService.registerMyProfile(command);

      //then
      verify(userProfileValidator).validateNicknameDuplicate(command.getNickname());
      verify(userProfileCommandPort).saveUserProfile(any());
      verify(userCommandPort).updateUserStatus(command.getUserId(), UserStatus.ACTIVE);
    }

    /*[Case #2] 프로필 생성 실패 - 닉네임 중복*/
    @DisplayName("프로필 생성 실패 - 닉네임이 중복된 경우 예외가 발생해야 한다")
    @Test
    public void registerMyProfile_shouldThrowException_whenNicknameDuplicated() throws Exception {
      //given
      RegisterMyProfileCommand command = new RegisterMyProfileCommand(
          "user1",
          "nickname",
          "status message",
          "bio",
          "010-1234-5678",
          UserGenderType.MALE,
          null
      );

      doThrow(new UserException(ErrorCode.USER_NICKNAME_DUPLICATED))
          .when(userProfileValidator).validateNicknameDuplicate(command.getNickname());

      //when & then
      assertThrows(UserException.class,
          () -> userProfileCommandService.registerMyProfile(command));
    }
  }

  @Nested
  @DisplayName("프로필 수정 테스트")
  class UpdateMyProfileTest {

    /*[Case #1] 프로필 수정 성공 - 프로필 정보 업데이트*/
    @DisplayName("프로필 수정 성공 - 프로필 정보가 업데이트되어야 한다")
    @Test
    public void updateMyProfile_shouldUpdateProfile_whenNicknameNotDuplicated() throws Exception {
      //given
      UpdateMyProfileCommand command = new UpdateMyProfileCommand(
          "user1",
          "newNickname",
          "new status",
          "new bio",
          "010-9999-9999",
          null
      );

      doNothing().when(userProfileValidator).validateNicknameDuplicate(command.getNickname());

      //when
      userProfileCommandService.updateMyProfile(command);

      //then
      verify(userProfileValidator).validateNicknameDuplicate(command.getNickname());
      verify(userProfileCommandPort).updateMyProfile(any());
      verify(userCommandPort).updateUserPhone(anyString(), anyString());
    }

    /*[Case #2] 프로필 수정 실패 - 닉네임 중복*/
    @DisplayName("프로필 수정 실패 - 닉네임이 중복된 경우 예외가 발생해야 한다")
    @Test
    public void updateMyProfile_shouldThrowException_whenNicknameDuplicated() throws Exception {
      //given
      UpdateMyProfileCommand command = new UpdateMyProfileCommand(
          "user1",
          "nickname",
          "status",
          "bio",
          "010-1234-5678",
          null
      );

      doThrow(new UserException(ErrorCode.USER_NICKNAME_DUPLICATED))
          .when(userProfileValidator).validateNicknameDuplicate(command.getNickname());

      //when & then
      assertThrows(UserException.class, () -> userProfileCommandService.updateMyProfile(command));
    }
  }
}
