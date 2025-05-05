package com.threadly.entity.user;

import com.threadly.entity.BaseEntity;
import com.threadly.user.UserType;
import com.threadly.user.response.UserPortResponse;
import com.threadly.util.RandomUtils;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor()
@AllArgsConstructor()
public class UserEntity extends BaseEntity {

  @Id
  @Column(name = "user_id")
  private String userId;

  /*사용자 이름*/
  @Column(name = "user_name")
  private String userName;

  /*비밀번호*/
  @Column(name = "password")
  private String password;

  /*이메일*/
  @Column(name = "email")
  private String email;

  /*전화번호*/
  @Column(name = "phone")
  private String phone;

  @Enumerated(EnumType.STRING)
  /*사용자 타입*/
  @Column(name = "user_type")
  private UserType userType;

  /*계정 활성화 유무*/
  @Column(name = "is_active")
  private boolean isActive;

  /*이메일 인증 유무*/
  @Column(name = "is_email_verified")
  private boolean isEmailVerified;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "user_profile_id")
  private UserProfileEntity userProfile;
  /**
   * 새로운 User 생성
   *
   * @param userName
   * @param password
   * @param email
   * @param phone
   * @param userType
   * @param isActive
   * @return
   */
  public static UserEntity newUser(String userName, String password, String email, String phone,
      UserType userType,
      boolean isActive, boolean isEmailVerified) {
    return new UserEntity(
        userName,
        password,
        email,
        phone,
        userType,
        isActive,
        isEmailVerified
    );
  }

  public void setUserProfile(UserProfileEntity userProfile) {
    this.userProfile = userProfile;
  }

  /**
   * UserEntity -> UserPortResponse
   *
   * @return
   */
  public UserPortResponse toUserPortResponse() {
    return UserPortResponse.builder()
        .userId(this.getUserId())
        .userName(this.getUserName())
        .password(this.getPassword())
        .email(this.getEmail())
        .phone(this.getPhone())
        .userType(this.getUserType().name())
        .isActive(this.isActive())
        .isEmailVerified(this.isEmailVerified())
        .build();


  }

  /*TODO db pk와 외부 조회용 id 분리 고려*/
  private UserEntity(String userName, String password, String email, String phone,
      UserType userType,
      boolean isActive, boolean isEmailVerified) {
    this.userId = RandomUtils.generateNanoId();
    this.userName = userName;
    this.password = password;
    this.email = email;
    this.phone = phone;
    this.userType = userType;
    this.isActive = isActive;
    this.isEmailVerified = isEmailVerified;
  }
}
