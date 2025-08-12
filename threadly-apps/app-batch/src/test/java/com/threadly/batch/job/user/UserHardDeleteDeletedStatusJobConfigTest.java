package com.threadly.batch.job.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.threadly.adapter.persistence.user.entity.UserEntity;
import com.threadly.batch.BaseBatchTest;
import com.threadly.core.domain.user.UserStatusType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
    "spring.batch.job.name=userHardDeleteDeletedJob"
})
@DisplayName("userHardDeleteStep")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserHardDeleteDeletedStatusJobConfigTest extends BaseBatchTest {

  @Autowired
  @Qualifier("userHardDeleteDeletedJob")
  private Job userHardDeleteDeletedJob;

  @BeforeEach
  void setUpJob() {
    jobLauncherTestUtils.setJob(userHardDeleteDeletedJob);
  }

  /*
   * 1. DELETED 상태의 User가 삭제기준시각 이전에 수정되었으면 정상적으로 삭제 되는지 검증
   * 2. DELETED 상태의 User가 삭제 기준 시각 적용되지 않으면 삭제 되지 않는지 검증
   * 3. DELETED 상태의 User가 없으면 아무 데이터를 삭제 하지 않는지 검증
   * 4. Step 만 실행해도 정상 동작하는지 검증
   * */

  /*[Case #1] DELETED 상태의 User가 삭제 기준 시각 이전에 수정 되었으면 정상적으로 삭제 되는지 검증*/
  @Order(1)
  @Test
  @DisplayName("1. DELETED 상태의 User가 삭제 기준 시각 이전에 수정 되었으면 정상적으로 삭제 되는지 검증")
  void shouldDeleteUsersWithDeletedStatus()
      throws Exception {
    // given
    /*DELETED, 삭제 기준 시간 전 데이터 삽입*/
    int DELETE_STATUS_SIZE = 3;
    int ALL_SIZE = 6;
    int i = 1;
    for (; i <= DELETE_STATUS_SIZE; i++) {
      createUserTestData("user" + i, UserStatusType.DELETED, true);
    }

    /*ACTIVE 데이터 삽입*/
    for (; i <= ALL_SIZE; i++) {
      createUserTestData("user" + i, UserStatusType.ACTIVE, false);
    }

    // 배치 실행 전 데이터 확인
    List<UserEntity> allUsers = userRepository.findAll();
    List<UserEntity> deletedUsers = allUsers.stream()
        .filter(user -> user.getUserStatusType() == UserStatusType.DELETED)
        .toList();
    List<UserEntity> activeUsers = allUsers.stream()
        .filter(user -> user.getUserStatusType() == UserStatusType.ACTIVE)
        .toList();

    assertThat(allUsers).hasSize(ALL_SIZE);
    assertThat(deletedUsers).hasSize(DELETE_STATUS_SIZE);
    assertThat(activeUsers).hasSize(ALL_SIZE - DELETE_STATUS_SIZE);

    // when
    JobExecution jobExecution = jobLauncherTestUtils.launchStep("userHardDeleteStep");

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

    // DELETED 상태의 사용자들이 삭제되었는지 확인
    List<UserEntity> remainingUsers = userRepository.findAll();
    List<UserEntity> remainingDeletedUsers = remainingUsers.stream()
        .filter(user -> user.getUserStatusType() == UserStatusType.DELETED)
        .toList();
    List<UserEntity> remainingActivatedUsers = remainingUsers.stream()
        .filter(user -> user.getUserStatusType() == UserStatusType.ACTIVE)
        .toList();

    assertThat(remainingUsers).hasSize(ALL_SIZE - DELETE_STATUS_SIZE); // ACTIVE 상태만 남음
    assertThat(remainingDeletedUsers).hasSize(0); // DELETED 상태는 모두 삭제됨
    assertThat(remainingActivatedUsers).hasSize(ALL_SIZE - DELETE_STATUS_SIZE); // ACTIVE 상태는 유지
  }

  /* [Case #2] DELETED 상태의 User가 삭제 기준 시각 적용되지 않으면 삭제 되지 않는지 검증 */
  @Order(2)
  @Test
  @DisplayName("2. DELETED 상태의 User가 삭제 기준 시각에 적용되지 않으면 삭제 되지 않는지 검증")
  void shouldNotDeleteUsersWithDeletedStatus_whenNotExpired()
      throws Exception {
    // given
    /* 데이터 삽입*/
    int DELETE_STATUS_SIZE = 3;
    for (int i = 0; i < DELETE_STATUS_SIZE; i++) {
      createUserTestData("user" + i, UserStatusType.DELETED, false);
    }

    // 배치 실행 전 데이터 확인
    List<UserEntity> allUsers = userRepository.findAll();
    List<UserEntity> deletedUsers = allUsers.stream()
        .filter(user -> user.getUserStatusType() == UserStatusType.DELETED)
        .toList();

    assertThat(allUsers).hasSize(DELETE_STATUS_SIZE);
    assertThat(deletedUsers).hasSize(DELETE_STATUS_SIZE);

    // when
    JobExecution jobExecution = jobLauncherTestUtils.launchStep("userHardDeleteStep");

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

    // DELETED 상태의 사용자들이 삭제되지 않았는지 확인
    List<UserEntity> remainingDeletedUsers = userRepository.findAll().stream()
        .filter(user -> user.getUserStatusType() == UserStatusType.DELETED)
        .toList();

    assertThat(remainingDeletedUsers).hasSize(DELETE_STATUS_SIZE);
  }

  /* [Case #3]. DELETED 상태의 User가 없으면 아무 데이터를 삭제 하지 않는지 검증  */
  @Order(3)
  @Test
  @DisplayName("3. DELETED 상태의 User가 없으면 배치 작업이 성공하며 아무것도 삭제하지 않는다")
  void shouldCompleteSuccessfullyWhenNoDeletedUsers()
      throws Exception {
    // given
    /*ACTIVE 상태의 사용자 데이터 생성*/
    int SIZE = 10;
    for (int i = 0; i < SIZE; i++) {
      createUserTestData("user" + i, UserStatusType.ACTIVE, true);
    }

    List<UserEntity> beforeUsers = userRepository.findAll();
    assertThat(beforeUsers).hasSize(SIZE);
    assertThat(beforeUsers).allMatch(user -> user.getUserStatusType() == UserStatusType.ACTIVE);

    // when
    JobExecution jobExecution = jobLauncherTestUtils.launchStep("userHardDeleteStep");

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

    List<UserEntity> afterUsers = userRepository.findAll();
    assertThat(afterUsers).hasSize(SIZE); // 그대로 유지
    assertThat(afterUsers).allMatch(user -> user.getUserStatusType() == UserStatusType.ACTIVE);
  }

  /*[Case #4] Step만 실행해도 정상 동작한다*/
  @Order(4)
  @Test
  @DisplayName("4. Step만 실행해도 정상 동작한다")
  void shouldExecuteStepSuccessfully() throws Exception {
    // given
    /*테스트 데이터 삽입*/
    int SIZE = 10;
    for (int i = 0; i < SIZE; i++) {
      createUserTestData("user" + i, UserStatusType.ACTIVE, true);
    }

    // when
    JobExecution jobExecution = jobLauncherTestUtils.launchStep("userHardDeleteStep");

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

    List<UserEntity> remainingUsers = userRepository.findAll();
    assertThat(remainingUsers).hasSize(SIZE); // ACTIVE 상태만 남음
  }

}