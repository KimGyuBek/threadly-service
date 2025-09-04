package com.threadly.batch.job.image.temporary;

import static org.assertj.core.api.Assertions.assertThat;

import com.threadly.adapter.persistence.post.entity.PostImageEntity;
import com.threadly.batch.BaseBatchTest;
import com.threadly.core.domain.image.ImageStatus;
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
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
    "spring.batch.job.name=imageHardDeleteTemporaryJob"
})
@DisplayName("imageHardDeleteTemporaryJob")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ImageHardDeleteTemporaryJobConfigTest extends BaseBatchTest {

  /*
   * 1. TEMPORARY 상태의 PostImage와 ProfileImage가 모두 삭제기준시각 이전에 수정되었으면 정상적으로 삭제 되는지 검증
   * 2. Flow Job이 성공적으로 완료되는지 검증
   * 3. 대상 데이터가 없을 때도 정상 완료하는지 검증
   * */

  /*[Case #1] TEMPORARY 상태의 PostImage Flow Job이 정상적으로 실행되는지 검증*/
  @Order(1)
  @Test
  @DisplayName("1. TEMPORARY 상태의 이미지 삭제 Flow Job이 정상적으로 실행된다")
  void shouldExecuteImageHardDeleteTemporaryJobSuccessfully(@Autowired Job imageHardDeleteTemporaryJob)
      throws Exception {
    // given
    /*TEMPORARY, 삭제 기준 시간 전 데이터 삽입*/
    int TEMPORARY_STATUS_SIZE = 3;
    int ALL_SIZE = 6;
    int i = 1;
    for (; i <= TEMPORARY_STATUS_SIZE; i++) {
      createTestData(i, ImageStatus.TEMPORARY, true);
    }

    /*CONFIRMED 데이터 삽입*/
    for (; i <= ALL_SIZE; i++) {
      createTestData(i, ImageStatus.CONFIRMED, false);
    }

    // 배치 실행 전 데이터 확인
    List<PostImageEntity> allImages = postImageRepository.findAll();
    List<PostImageEntity> temporaryImages = postImageRepository.findAllByStatus(
        ImageStatus.TEMPORARY);
    List<PostImageEntity> confirmedImages = postImageRepository.findAllByStatus(
        ImageStatus.CONFIRMED);

    assertThat(allImages).hasSize(ALL_SIZE);
    assertThat(temporaryImages).hasSize(TEMPORARY_STATUS_SIZE);
    assertThat(confirmedImages).hasSize(ALL_SIZE - TEMPORARY_STATUS_SIZE);

    // when
    JobParameters jobParameters = new JobParameters();
    jobLauncherTestUtils.setJob(imageHardDeleteTemporaryJob);
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

    // TEMPORARY 상태의 이미지들이 삭제되었는지 확인
    List<PostImageEntity> remainingImages = postImageRepository.findAll();
    List<PostImageEntity> remainingTemporaryImages = postImageRepository.findAllByStatus(
        ImageStatus.TEMPORARY);
    List<PostImageEntity> remainingConfirmedImages = postImageRepository.findAllByStatus(
        ImageStatus.CONFIRMED);

    assertThat(remainingImages).hasSize(ALL_SIZE - TEMPORARY_STATUS_SIZE); // CONFIRMED 상태만 남음
    assertThat(remainingTemporaryImages).hasSize(0); // TEMPORARY 상태는 모두 삭제됨
    assertThat(remainingConfirmedImages).hasSize(ALL_SIZE - TEMPORARY_STATUS_SIZE); // CONFIRMED 상태는 유지
  }

  /* [Case #2] 대상 데이터가 없을 때 Flow Job이 정상 완료되는지 검증 */
  @Order(2)
  @Test
  @DisplayName("2. 대상 데이터가 없을 때 Flow Job이 성공적으로 완료된다")
  void shouldCompleteSuccessfullyWhenNoTargetData(@Autowired Job imageHardDeleteTemporaryJob)
      throws Exception {
    // given
    /*CONFIRMED 상태의 이미지 데이터만 생성*/
    int SIZE = 5;
    for (int i = 0; i < SIZE; i++) {
      createTestData(i, ImageStatus.CONFIRMED, true);
    }

    List<PostImageEntity> beforeImages = postImageRepository.findAll();
    assertThat(beforeImages).hasSize(SIZE);
    assertThat(beforeImages).allMatch(image -> image.getStatus() == ImageStatus.CONFIRMED);

    // when
    JobParameters jobParameters = new JobParameters();
    jobLauncherTestUtils.setJob(imageHardDeleteTemporaryJob);
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

    List<PostImageEntity> afterImages = postImageRepository.findAll();
    assertThat(afterImages).hasSize(SIZE); // 그대로 유지
    assertThat(afterImages).allMatch(image -> image.getStatus() == ImageStatus.CONFIRMED);
  }

  /* [Case #3] Flow Job의 실행 상태 및 Step 완료 여부 검증 */
  @Order(3)
  @Test
  @DisplayName("3. Flow Job의 모든 Step이 순차적으로 실행되고 완료된다")
  void shouldExecuteAllStepsInFlow(@Autowired Job imageHardDeleteTemporaryJob)
      throws Exception {
    // given
    int SIZE = 2;
    for (int i = 0; i < SIZE; i++) {
      createTestData(i, ImageStatus.TEMPORARY, true);
    }

    // when
    JobParameters jobParameters = new JobParameters();
    jobLauncherTestUtils.setJob(imageHardDeleteTemporaryJob);
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    
    // Step 실행 개수 확인 (profileImageFlow + postImageFlow)
    assertThat(jobExecution.getStepExecutions()).hasSize(2);
    
    // 모든 Step이 완료되었는지 확인
    jobExecution.getStepExecutions().forEach(stepExecution -> {
      assertThat(stepExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    });
  }

}