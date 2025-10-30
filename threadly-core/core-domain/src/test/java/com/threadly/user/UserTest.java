package com.threadly.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.threadly.core.domain.user.CannotBannedException;
import com.threadly.core.domain.user.CannotInactiveException;
import com.threadly.core.domain.user.User;
import com.threadly.core.domain.user.UserGenderType;
import com.threadly.core.domain.user.UserStatus;
import com.threadly.core.domain.user.profile.UserProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * User 도메인 테스트
 */
class UserTest {

  private User user;

  /**
   * newUser()
   */
  /*[Case #1] 새로운 사용자 생성시 필드가 올바르게 세팅되는지 확인*/
  @DisplayName("newUser - 사용자가 정상적으로 생성되어야 한다")
  @Test
  public void newUser_shouldCreateUserSuccessfully() throws Exception {
    //given
    String userName = "username";
    String password = "password";
    String email = "test@test.com";
    String phone = "010-1234-5678";

    //when
    user = User.newUser(userName, password, email, phone);

    //then
    assertAll(
        () -> assertThat(user.getUserName()).isEqualTo(userName),
        () -> assertThat(user.getPassword()).isEqualTo(password),
        () -> assertThat(user.getEmail()).isEqualTo(email),
        () -> assertThat(user.getPhone()).isEqualTo(phone),
        () -> assertThat(user.getUserStatus()).isEqualTo(UserStatus.INCOMPLETE_PROFILE)
    );
  }

  /**
   * verifyEmail()
   */
  /*[Case #1] 이메일 인증 시 상태가 true로 변경되어야 한다*/
  @DisplayName("verifyEmail - 이메일 인증 시 상태가 true로 변경되어야 한다")
  @Test
  public void verifyEmail_shouldMarkEmailAsVerified() throws Exception {
    //given
    generateUser();

    //when
    user.verifyEmail();

    //then
    assertTrue(user.isEmailVerified());
  }

  /**
   * markAsActive()
   */
  /*[Case #1] ACTIVE 상태로 변경시 상태가 올바르게 변경되어야 한다*/
  @DisplayName("markAsActive - ACTIVE 상태로 변경되어야 한다")
  @Test
  public void markAsActive_shouldChangeStatusToActive() throws Exception {
    //given
    generateUser();

    //when
    user.markAsActive();

    //then
    assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
  }

  /**
   * markAsDeleted()
   */
  /*[Case #1] DELETED 상태로 변경시 상태가 올바르게 변경되어야 한다*/
  @DisplayName("markAsDeleted - DELETED 상태로 변경되어야 한다")
  @Test
  public void markAsDeleted_shouldChangeStatusToDeleted() throws Exception {
    //given
    generateUser();

    //when
    user.markAsDeleted();

    //then
    assertThat(user.getUserStatus()).isEqualTo(UserStatus.DELETED);
  }

  /**
   * markAsInactive()
   */
  /*[Case #1] DELETED 상태에서 INACTIVE로 변경시 예외가 발생해야 한다*/
  @DisplayName("markAsInactive - DELETED 상태에서 예외가 발생해야 한다")
  @Test
  public void markAsInactive_shouldThrowException_whenUserIsDeleted() throws Exception {
    //given
    generateUser();
    user.markAsDeleted();

    //when & then
    assertThrows(CannotInactiveException.class, () -> user.markAsInactive());
  }

  /*[Case #2] ACTIVE 상태에서 INACTIVE로 변경되어야 한다*/
  @DisplayName("markAsInactive - INACTIVE 상태로 변경되어야 한다")
  @Test
  public void markAsInactive_shouldChangeStatusToInactive() throws Exception {
    //given
    generateUser();
    user.markAsActive();

    //when
    user.markAsInactive();

    //then
    assertThat(user.getUserStatus()).isEqualTo(UserStatus.INACTIVE);
  }

  /**
   * markAsBanned()
   */
  /*[Case #1] DELETED 상태에서 BANNED로 변경시 예외가 발생해야 한다*/
  @DisplayName("markAsBanned - DELETED 상태에서 예외가 발생해야 한다")
  @Test
  public void markAsBanned_shouldThrowException_whenUserIsDeleted() throws Exception {
    //given
    generateUser();
    user.markAsDeleted();

    //when & then
    assertThrows(CannotBannedException.class, () -> user.markAsBanned());
  }

  /*[Case #2] ACTIVE 상태에서 BANNED로 변경되어야 한다*/
  @DisplayName("markAsBanned - BANNED 상태로 변경되어야 한다")
  @Test
  public void markAsBanned_shouldChangeStatusToBanned() throws Exception {
    //given
    generateUser();
    user.markAsActive();

    //when
    user.markAsBanned();

    //then
    assertThat(user.getUserStatus()).isEqualTo(UserStatus.BANNED);
  }

  /**
   * setUserProfile()
   */
  /*[Case #1] 프로필 생성시 올바르게 세팅되어야 한다*/
  @DisplayName("setUserProfile - 프로필이 정상적으로 생성되어야 한다")
  @Test
  public void setUserProfile_shouldCreateProfileSuccessfully() throws Exception {
    //given
    generateUser();

    String nickname = "nickname";
    String statusMessage = "status";
    String bio = "bio";
    String phone = "010-1234-5678";
    UserGenderType gender = UserGenderType.MALE;

    //when
    UserProfile profile = user.setUserProfile(nickname, statusMessage, bio, phone, gender);

    //then
    assertAll(
        () -> assertThat(profile.getNickname()).isEqualTo(nickname),
        () -> assertThat(profile.getStatusMessage()).isEqualTo(statusMessage),
        () -> assertThat(profile.getBio()).isEqualTo(bio),
        () -> assertThat(profile.getPhone()).isEqualTo(phone),
        () -> assertThat(profile.getGenderType()).isEqualTo(gender)
    );
  }

  /**
   * markAsPrivate()
   */
  /*[Case #1] 공개 계정에서 비공개로 변경되어야 한다*/
  @DisplayName("markAsPrivate - 비공개 상태로 변경되어야 한다")
  @Test
  public void markAsPrivate_shouldChangeToPrivate() throws Exception {
    //given
    generateUser();

    //when
    user.markAsPrivate();

    //then
    assertTrue(user.isPrivate());
  }

  /*사용자 생성*/
  private void generateUser() {
    user = User.newUser("username", "password", "test@test.com", "010-1234-5678");
  }
}
