package com.threadly.entity.user;

import com.threadly.entity.BaseEntity;
import com.threadly.user.UserType;
import com.threadly.user.response.UserPortResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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

  @Column(name = "user_name")
  private String userName;

  @Column(name = "password")
  private String password;

  @Column(name = "email")
  private String email;

  @Column(name = "phone")
  private String phone;

  @Enumerated(EnumType.STRING)
  @Column(name = "user_type")
  private UserType userType;

  @Column(name = "is_active")
  private boolean isActive;

  @Column(name = "is_email_verified")
  private boolean isEmailVerified;
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

  private UserEntity(String userName, String password, String email, String phone,
      UserType userType,
      boolean isActive, boolean isEmailVerified) {
    this.userId = UUID.randomUUID().toString();
    this.userName = userName;
    this.password = password;
    this.email = email;
    this.phone = phone;
    this.userType = userType;
    this.isActive = isActive;
    this.isEmailVerified = isEmailVerified;
  }
}
