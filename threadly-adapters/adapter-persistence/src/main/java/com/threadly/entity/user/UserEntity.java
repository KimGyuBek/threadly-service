package com.threadly.entity.user;

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
public class UserEntity {

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

  /*TODO BaseEntity로 이동*/
  @Column(name = "created_at")
  private LocalDateTime createAt;

  @Column(name = "modified_at")
  private LocalDateTime modifiedAt;

  public static UserEntity newUser(String userName, String password, String email, String phone,
      UserType userType,
      boolean isActive) {
    return new UserEntity(
        userName,
        password,
        email,
        phone,
        userType,
        isActive,
        LocalDateTime.now(),
        LocalDateTime.now()
    );
  }


  private UserEntity(String userName, String password, String email, String phone, UserType userType,
      boolean isActive,
      LocalDateTime createAt, LocalDateTime modifiedAt) {
    this.userId = UUID.randomUUID().toString();
    this.userName = userName;
    this.password = password;
    this.email = email;
    this.phone = phone;
    this.userType = userType;
    this.isActive = isActive;
    this.createAt = createAt;
    this.modifiedAt = modifiedAt;
  }
}
