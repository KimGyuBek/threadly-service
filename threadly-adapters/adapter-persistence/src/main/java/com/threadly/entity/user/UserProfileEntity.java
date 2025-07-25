package com.threadly.entity.user;

import com.threadly.entity.BaseEntity;
import com.threadly.user.UserGenderType;
import com.threadly.user.profile.UserProfileType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * user_profile 엔티티
 */
@Entity
@Table(name = "user_profile")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserProfileEntity extends BaseEntity {

  @Id
  @Column(name = "user_id")
  private String userId;

  /*닉네임*/
  @Column(name = "nickname")
  private String nickname;

  /*상태 메세지 */
  @Column(name = "status_message")
  private String statusMessage;

  /*설명(소개글)*/
  @Column(name = "bio")
  private String bio;

  /*성별*/
  @Enumerated(EnumType.STRING)
  @Column(name = "gender")
  private UserGenderType gender;

  /*프로필 계정 타입*/
  @Enumerated(EnumType.STRING)
  @Column(name = "profile_type")
  private UserProfileType profileType;

  /*프로필 이미지 경로*/
  @Column(name = "profile_image_url")
  private String profileImageUrl;

  public static UserProfileEntity newUserProfile(String userId,String nickname, String statusMessage, String bio, UserGenderType gender,
      UserProfileType profileType, String profileImageUrl) {

    return new UserProfileEntity(
        userId,
        nickname,
        statusMessage,
        bio,
        gender,
        profileType,
        profileImageUrl
    );
  }
}
