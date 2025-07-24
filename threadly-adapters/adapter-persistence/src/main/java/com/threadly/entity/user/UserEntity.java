package com.threadly.entity.user;

import com.threadly.entity.BaseEntity;
import com.threadly.user.User;
import com.threadly.user.UserType;
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

//  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
//  private UserProfileEntity userProfile;

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
  public static UserEntity newUser(User user) {
    return new UserEntity(
        user.getUserId(),
        user.getUserName(),
        user.getPassword(),
        user.getEmail(),
        user.getPhone(),
        user.getUserType(),
        user.isActive(),
        user.isEmailVerified()
    );
  }

  /**
   * 프록시 객체 생성
   * @param userId
   * @return
   */
  public static UserEntity fromId(String userId) {
    UserEntity userEntity = new UserEntity();
    userEntity.userId = userId;

    return userEntity;
  }

//  public void setUserProfile(UserProfileEntity userProfile) {
//    this.userProfile = userProfile;
//  }

}
