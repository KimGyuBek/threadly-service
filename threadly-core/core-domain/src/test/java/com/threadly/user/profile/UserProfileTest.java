package com.threadly.user.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.threadly.core.domain.user.UserGenderType;
import com.threadly.core.domain.user.profile.UserProfile;
import com.threadly.core.domain.user.profile.UserProfileType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * UserProfile 도메인 테스트
 */
class UserProfileTest {

  private UserProfile profile;

  /**
   * setProfile()
   */
  /*[Case #1] 프로필 생성시 필드가 올바르게 세팅되는지 확인*/
  @DisplayName("setProfile - 프로필이 정상적으로 생성되어야 한다")
  @Test
  public void setProfile_shouldCreateProfileSuccessfully() throws Exception {
    //given
    String userId = "user1";
    String nickname = "nickname";
    String statusMessage = "status";
    String bio = "bio";
    String phone = "010-1234-5678";
    UserGenderType gender = UserGenderType.MALE;

    //when
    profile = UserProfile.setProfile(userId, nickname, statusMessage, bio, phone, gender);

    //then
    assertAll(
        () -> assertThat(profile.getUserId()).isEqualTo(userId),
        () -> assertThat(profile.getNickname()).isEqualTo(nickname),
        () -> assertThat(profile.getStatusMessage()).isEqualTo(statusMessage),
        () -> assertThat(profile.getBio()).isEqualTo(bio),
        () -> assertThat(profile.getPhone()).isEqualTo(phone),
        () -> assertThat(profile.getGenderType()).isEqualTo(gender),
        () -> assertThat(profile.getUserProfileType()).isEqualTo(UserProfileType.USER)
    );
  }

  /**
   * updateProfile()
   */
  /*[Case #1] 프로필 업데이트시 변경된 내용이 반영되어야 한다*/
  @DisplayName("updateProfile - 프로필이 정상적으로 업데이트되어야 한다")
  @Test
  public void updateProfile_shouldUpdateProfileSuccessfully() throws Exception {
    //given
    generateProfile();

    String newNickname = "newNickname";
    String newStatusMessage = "newStatus";
    String newBio = "newBio";
    String newPhone = "010-9999-9999";

    //when
    profile.updateProfile(newNickname, newStatusMessage, newBio, newPhone);

    //then
    assertAll(
        () -> assertThat(profile.getNickname()).isEqualTo(newNickname),
        () -> assertThat(profile.getStatusMessage()).isEqualTo(newStatusMessage),
        () -> assertThat(profile.getBio()).isEqualTo(newBio),
        () -> assertThat(profile.getPhone()).isEqualTo(newPhone)
    );
  }

  /*프로필 생성*/
  private void generateProfile() {
    profile = UserProfile.setProfile(
        "user1",
        "nickname",
        "status",
        "bio",
        "010-1234-5678",
        UserGenderType.MALE
    );
  }
}
