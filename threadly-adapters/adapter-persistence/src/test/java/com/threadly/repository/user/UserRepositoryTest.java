//package com.threadly.repository.user;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertAll;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.threadly.entity.user.UserEntity;
//import com.threadly.user.UserType;
//import com.threadly.user.CreateUser;
//import com.threadly.user.response.UserPortResponse;
//import java.util.Optional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
///**
// * UserRepository Test
// */
//
//@ExtendWith(MockitoExtension.class)
//class UserRepositoryTest {
//
//  @Mock
//  private UserJpaRepository userJpaRepository;
//
////  private UserRepository userRepository;
//
//  @BeforeEach
//  void setUp() {
//    userRepository = new UserRepository(userJpaRepository);
//  }
//
//  /**
//   * findByEmail test
//   */
//  @DisplayName("email이 존재할 경우 리턴해야 한다.")
//  @Test
//  public void shouldReturnUserPortResponseWhenEmailExists() throws Exception {
//    //given
//    String email = "test@email.com";
//    UserEntity userEntity = UserEntity.newUser(
//        "user1",
//        "1234",
//        email,
//        "123-1234-1234",
//        UserType.USER,
//        true
//    );
//
//    when(userJpaRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
//    //when
//
//    Optional<UserPortResponse> result = userRepository.findByEmail(email);
//
//    //then
//    assertAll(
//        () -> assertThat(result).isPresent(),
//        () -> assertThat(result.get().getEmail()).isEqualTo(email)
//    );
//    verify(userJpaRepository, times(1)).findByEmail(email);
//  }
//
//  @Test
//  public void shouldReturnEmptyWhenEmailDoesNotExist() throws Exception {
//    //given
//    String email = "test@email.com";
//
//    when(userJpaRepository.findByEmail(email)).thenReturn(Optional.empty());
//
//    //when
//    Optional<UserPortResponse> result = userRepository.findByEmail(email);
//
//    //then
//    assertThat(result).isEmpty();
//    verify(userJpaRepository, times(1)).findByEmail(email);
//  }
//
//
//  /**
//   * 회원가입 테스트 - 회원가입시 email은 중복되면 안 된다.
//   */
//  @DisplayName("회원가입 - 이메일 중복 테스트")
////  @Test
//  public void createUser_WithDuplicateEmail() throws Exception {
//    //given
//    String email = "test@email.com";
//    CreateUser createUser = new CreateUser(email, "user1", "1234", "123-1234-1234");
//
//    //when
//
//
//    Optional<UserPortResponse> byEmail = userRepository.findByEmail(email);
//    System.out.println("email : "+ byEmail.get().getEmail());
//
//    //then
//    assertAll(
////        () -> assertThat(userPortResponse.getEmail()).isEqualTo(email)
//    );
//
//    verify(userJpaRepository, times(1)).findByEmail(createUser.getEmail());
//
//  }
//
//}