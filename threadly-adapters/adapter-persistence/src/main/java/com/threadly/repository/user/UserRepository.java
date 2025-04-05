package com.threadly.repository.user;

import com.threadly.entity.user.UserEntity;
import com.threadly.user.UserType;
import com.threadly.user.CreateUser;
import com.threadly.user.FetchUserPort;
import com.threadly.user.InsertUserPort;
import com.threadly.user.response.UserPortResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepository implements InsertUserPort, FetchUserPort {

  private final UserJpaRepository userJpaRepository;

  public Optional<UserPortResponse> findByEmail(String email) {
    return
        userJpaRepository.findByEmail(email)
            .map(
                UserEntity::toUserPortResponse
//                entity -> UserPortResponse.builder()
//                    .userId(entity.getUserId())
//                    .userName(entity.getUserName())
//                    .password(entity.getPassword())
//                    .email(entity.getEmail())
//                    .phone(entity.getPhone())
//                    .userType(entity.getUserType().name())
//                    .isActive(entity.isActive())
//                    .build()
            );
  }

  @Override
  public Optional<UserPortResponse> findByUserId(String userId) {

    Optional<UserEntity> result = userJpaRepository.findByUserId(userId);

    return
        result.map(
            UserEntity::toUserPortResponse
//            entity -> UserPortResponse.builder()
//                .userId(entity.getUserId())
//                .userName(entity.getUserName())
//                .password(entity.getPassword())
//                .email(entity.getEmail())
//                .phone(entity.getPhone())
//                .userType(entity.getUserType().name())
//                .isActive(entity.isActive())
//                .build()
        );

  }


  /**
   * 회원가입
   *
   * @param createUser
   * @return
   */
  @Override
  public Optional<UserPortResponse> create(CreateUser createUser) {

    /*email 중복 조회*/
    String email = createUser.getEmail();
    Optional<UserPortResponse> byEmail = findByEmail(email);

    /*있으면 예외 던지기*/
    if (byEmail.isPresent()) {
      throw new RuntimeException("User with email " + email + " already exists");
    }

    /*중복 되지 않는 경우*/
    /*새로운 user 생성*/
    UserEntity newUser = UserEntity.newUser(
        createUser.getUserName(),
        createUser.getPassword(),
        createUser.getEmail(),
        createUser.getPhone(),
        UserType.USER,
        true
    );


    /*저장*/
    UserEntity save = userJpaRepository.save(newUser);

    return Optional.ofNullable(save.toUserPortResponse());
  }

}
