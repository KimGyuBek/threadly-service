package com.threadly.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;

@Entity
@Table(name = "users")
@Getter
public class UserEntity {

  @Id
  @Column(name = "user_id")
  private String userId;

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

}
