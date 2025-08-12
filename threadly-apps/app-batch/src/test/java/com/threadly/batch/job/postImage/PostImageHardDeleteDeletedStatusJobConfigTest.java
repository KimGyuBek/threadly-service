package com.threadly.batch.job.postImage;

import static org.assertj.core.api.Assertions.assertThat;

import com.threadly.adapter.persistence.post.entity.PostImageEntity;
import com.threadly.batch.BaseBatchTest;
import com.threadly.core.domain.image.ImageStatus;
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
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
    "spring.batch.job.name=imageHardDeleteDeletedJob"
})
@DisplayName("postImageHardDeleteDeletedStep")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PostImageHardDeleteDeletedStatusJobConfigTest extends BaseBatchTest {

  @Autowired
  @Qualifier("imageHardDeleteDeletedJob")
  private Job imageHardDeleteDeletedJob;

  @BeforeEach
  void setUpJob() {
    jobLauncherTestUtils.setJob(imageHardDeleteDeletedJob);
  }

  /*
   * 1. DELETED 상태의 PostImage가 삭제기준시각 이전에 수정되었으면 정상적으로 삭제 되는지 검증
   * 2. DELETED 상태의 PostImage가 삭제 기준 시각 적용되지 않으면 삭제 되지 않는지 검증
   * 3. DELETED 상태의 PostImage가 없으면 아무 데이터를 삭제 하지 않는지 검증
   * 4. Step 만 실행해도 정상 동작하는지 검증
   * */

  /*[Case #1] DELETED 상태의 PostImage가 삭제 기준 시각 이전에 수정 되었으면 정상적으로 삭제 되는지 검증*/
  @Order(1)
  @Test
  @DisplayName("1. DELETED 상태의 PostImage가 삭제 기준 시각 이전에 수정 되었으면 정상적으로 삭제 되는지 검증")
  void shouldDeletePostImagesWithDeletedStatus()
      throws Exception {
    // given
    /*DELETED, 삭제 기준 시간 전 데이터 삽입*/
    int DELETE_STATUS_SIZE = 3;
    int ALL_SIZE = 6;
    int i = 1;
    for (; i <= DELETE_STATUS_SIZE; i++) {
      createTestData(i, ImageStatus.DELETED, true);
    }

    /*CONFIRMED 데이터 삽입*/
    for (; i <= ALL_SIZE; i++) {
      createTestData(i, ImageStatus.CONFIRMED, false);
    }

    // 배치 실행 전 데이터 확인
    List<PostImageEntity> allImages = postImageRepository.findAll();
    List<PostImageEntity> deletedImages = postImageRepository.findAllByStatus(ImageStatus.DELETED);
    List<PostImageEntity> confirmedImages = postImageRepository.findAllByStatus(
        ImageStatus.CONFIRMED);

    assertThat(allImages).hasSize(ALL_SIZE);
    assertThat(deletedImages).hasSize(DELETE_STATUS_SIZE);
    assertThat(confirmedImages).hasSize(ALL_SIZE - DELETE_STATUS_SIZE);

    // when
    JobExecution jobExecution = jobLauncherTestUtils.launchStep("postImageHardDeleteDeletedStep");

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

    // DELETED 상태의 이미지들이 삭제되었는지 확인
    List<PostImageEntity> remainingImages = postImageRepository.findAll();
    List<PostImageEntity> remainingDeletedImages = postImageRepository.findAllByStatus(
        ImageStatus.DELETED);
    List<PostImageEntity> remainingConfirmedImages = postImageRepository.findAllByStatus(
        ImageStatus.CONFIRMED);

    assertThat(remainingImages).hasSize(ALL_SIZE - DELETE_STATUS_SIZE); // CONFIRMED 상태만 남음
    assertThat(remainingDeletedImages).hasSize(0); // DELETED 상태는 모두 삭제됨
    assertThat(remainingConfirmedImages).hasSize(ALL_SIZE - DELETE_STATUS_SIZE); // CONFIRMED 상태는 유지
  }

  /* [Case #2] DELETED 상태의 PostImage가 삭제 기준 시각 적용되지 않으면 삭제 되지 않는지 검증 */
  @Order(2)
  @Test
  @DisplayName("2. DELETED 상태의 postImage가 삭제 기준 시각에 적용되지 않으면 삭제 되지 않는지 검증")
  void shouldDeletePostImagesWithDeletedStatus_02()
      throws Exception {
    // given
    /* 데이터 삽입*/
    int DELETE_STATUS_SIZE = 3;
    for (int i = 0; i < DELETE_STATUS_SIZE; i++) {
      createTestData(i, ImageStatus.DELETED, false);
    }

    // 배치 실행 전 데이터 확인
    List<PostImageEntity> allImages = postImageRepository.findAll();
    List<PostImageEntity> deletedImages = postImageRepository.findAllByStatus(ImageStatus.DELETED);

    assertThat(allImages).hasSize(DELETE_STATUS_SIZE);
    assertThat(deletedImages).hasSize(DELETE_STATUS_SIZE);

    // when
    JobExecution jobExecution = jobLauncherTestUtils.launchStep("postImageHardDeleteDeletedStep");

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

    // DELETED 상태의 이미지들이 삭제되었는지 확인
    List<PostImageEntity> remainingDeletedImages = postImageRepository.findAllByStatus(
        ImageStatus.DELETED);

    assertThat(remainingDeletedImages).hasSize(DELETE_STATUS_SIZE);
  }


  /* [Case #3]. DELETED 상태의 PostImage가 없으면 아무 데이터를 삭제 하지 않는지 검증  */
  @Order(3)
  @Test
  @DisplayName("3. DELETED 상태의 PostImage가 없으면 배치 작업이 성공하며 아무것도 삭제하지 않는다")
  void shouldCompleteSuccessfullyWhenNoDeletedImages()
      throws Exception {
    // given
    /*CONFIRMED 상태의 이미지 데이터 생성*/
    int SIZE = 10;
    for (int i = 0; i < SIZE; i++) {
      createTestData(i, ImageStatus.CONFIRMED, true);
    }

    List<PostImageEntity> beforeImages = postImageRepository.findAll();
    assertThat(beforeImages).hasSize(SIZE);
    assertThat(beforeImages).allMatch(image -> image.getStatus() == ImageStatus.CONFIRMED);

    // when
    JobExecution jobExecution = jobLauncherTestUtils.launchStep("postImageHardDeleteDeletedStep");

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

    List<PostImageEntity> afterImages = postImageRepository.findAll();
    assertThat(afterImages).hasSize(SIZE); // 그대로 유지
    assertThat(afterImages).allMatch(image -> image.getStatus() == ImageStatus.CONFIRMED);
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
      createTestData(i, ImageStatus.CONFIRMED, true);
    }

    // when
    JobExecution jobExecution = jobLauncherTestUtils.launchStep("postImageHardDeleteDeletedStep");

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

    List<PostImageEntity> remainingImages = postImageRepository.findAll();
    assertThat(remainingImages).hasSize(SIZE); // CONFIRMED 상태만 남음
  }

}