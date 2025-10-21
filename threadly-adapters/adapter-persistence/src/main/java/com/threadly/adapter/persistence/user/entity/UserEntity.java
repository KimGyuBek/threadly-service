package com.threadly.adapter.persistence.user.entity;

import com.threadly.adapter.persistence.base.BaseEntity;
import com.threadly.core.domain.user.User;
import com.threadly.core.domain.user.UserStatus;
import com.threadly.core.domain.user.UserRoleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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

  /*User type*/
  @Enumerated(EnumType.STRING)
  @Column(name = "user_type")
  private UserRoleType userRoleType;

  /*UserStatusType*/
  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private UserStatus userStatus;

  /*이메일 인증 유무*/
  @Column(name = "is_email_verified")
  private boolean isEmailVerified;

  /*비공개 계정 유무*/
  @Column(name = "is_private")
  private boolean isPrivate;

  /**
   * 새로운 User 생성
   */
  public static UserEntity newUser(User user) {
    return new UserEntity(
        user.getUserId(),
        user.getUserName(),
        user.getPassword(),
        user.getEmail(),
        user.getPhone(),
        user.getUserRoleType(),
        user.getUserStatus(),
        user.isEmailVerified(),
        user.isPrivate()
    );
  }


  /**
   * 프록시 객체 생성
   *
   * @param userId
   * @return
   */
  public static UserEntity fromId(String userId) {
    UserEntity userEntity = new UserEntity();
    userEntity.userId = userId;

    return userEntity;
  }

}
