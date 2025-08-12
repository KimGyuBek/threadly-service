package com.threadly.batch.job.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.threadly.adapter.persistence.user.entity.UserEntity;
import com.threadly.batch.BaseBatchTest;
import com.threadly.core.domain.user.UserStatusType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
    "spring.batch.job.name=userHardDeleteDeletedJob"
})
@DisplayName("userHardDeleteDeletedJob")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserHardDeleteDeletedJobConfigTest extends BaseBatchTest {

  /*
   * 1. DELETED 상태의 User가 삭제기준시각 이전에 수정되었으면 정상적으로 삭제 되는지 검증
   * 2. 대상 데이터가 없을 때도 정상 완료하는지 검증
   * 3. Job이 성공적으로 완료되는지 검증
   * */

  /*[Case #1] DELETED 상태의 User Job이 정상적으로 실행되는지 검증*/
  @Order(1)
  @Test
  @DisplayName("1. DELETED 상태의 사용자 삭제 Job이 정상적으로 실행된다")
  void shouldExecuteUserHardDeleteDeletedJobSuccessfully(@Autowired Job userHardDeleteDeletedJob)
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
    JobParameters jobParameters = new JobParameters();
    jobLauncherTestUtils.setJob(userHardDeleteDeletedJob);
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

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

  /* [Case #2] 대상 데이터가 없을 때 Job이 정상 완료되는지 검증 */
  @Order(2)
  @Test
  @DisplayName("2. 대상 데이터가 없을 때 Job이 성공적으로 완료된다")
  void shouldCompleteSuccessfullyWhenNoTargetData(@Autowired Job userHardDeleteDeletedJob)
      throws Exception {
    // given
    /*ACTIVE 상태의 사용자 데이터만 생성*/
    int SIZE = 5;
    for (int i = 0; i < SIZE; i++) {
      createUserTestData("user" + i, UserStatusType.ACTIVE, true);
    }

    List<UserEntity> beforeUsers = userRepository.findAll();
    assertThat(beforeUsers).hasSize(SIZE);
    assertThat(beforeUsers).allMatch(user -> user.getUserStatusType() == UserStatusType.ACTIVE);

    // when
    JobParameters jobParameters = new JobParameters();
    jobLauncherTestUtils.setJob(userHardDeleteDeletedJob);
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

    List<UserEntity> afterUsers = userRepository.findAll();
    assertThat(afterUsers).hasSize(SIZE); // 그대로 유지
    assertThat(afterUsers).allMatch(user -> user.getUserStatusType() == UserStatusType.ACTIVE);
  }

  /* [Case #3] Job의 실행 상태 및 Step 완료 여부 검증 */
  @Order(3)
  @Test
  @DisplayName("3. Job의 모든 Step이 정상적으로 실행되고 완료된다")
  void shouldExecuteAllStepsSuccessfully(@Autowired Job userHardDeleteDeletedJob)
      throws Exception {
    // given
    int SIZE = 2;
    for (int i = 0; i < SIZE; i++) {
      createUserTestData("user" + i, UserStatusType.DELETED, true);
    }

    // when
    JobParameters jobParameters = new JobParameters();
    jobLauncherTestUtils.setJob(userHardDeleteDeletedJob);
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    
    // Step 실행 개수 확인 (userHardDeleteStep)
    assertThat(jobExecution.getStepExecutions()).hasSize(1);
    
    // 모든 Step이 완료되었는지 확인
    jobExecution.getStepExecutions().forEach(stepExecution -> {
      assertThat(stepExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    });

    // 데이터가 실제로 삭제되었는지 확인
    List<UserEntity> remainingUsers = userRepository.findAll();
    assertThat(remainingUsers).hasSize(0); // 모두 삭제됨
  }

}