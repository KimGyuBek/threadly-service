package com.threadly.batch;

import com.threadly.adapter.persistence.post.repository.PostImageJpaRepository;
import com.threadly.batch.properties.RetentionProperties;
import com.threadly.core.domain.image.ImageStatus;
import com.threadly.core.domain.post.PostImage;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringBatchTest
@SpringBootTest(
    classes = SpringBootTest.class,
    webEnvironment = WebEnvironment.NONE
)
@ActiveProfiles("test")
@SpringJUnitConfig(classes = {BatchApplication.class})
@TestPropertySource(properties = {
    "jwt.enabled=false",
    "ttl.enabled=false",
    "spring.batch.job.enabled=false"
})
//@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class BaseBatchTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  public JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  public JobRepositoryTestUtils jobRepositoryTestUtils;

  @Autowired
  public PostImageJpaRepository postImageRepository;

  @Autowired
  public RetentionProperties retentionProperties;

  @BeforeEach
  void setUp() {
    // 테스트 전 배치 메타데이터 정리
    jobRepositoryTestUtils.removeJobExecutions();
    // 테스트 데이터 정리
    postImageRepository.deleteAll();
  }

  /**
   * 주어진 파라미터에 해당하는 postImage 데이터 생성
   *
   * @param imageId
   * @param imageStatus
   * @param isDeletion  true면 threshold 이전(삭제 대상)으로, false면 현재시각(비 삭제 대상)
   */
  public void createTestData(int imageId, ImageStatus imageStatus, boolean isDeletion) {
    Duration retention;
    if (imageStatus.equals(ImageStatus.DELETED)) {
      retention = retentionProperties.getImage().getDeleted();
    } else {
      retention = retentionProperties.getImage().getTemporary();
    }
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime threshold = now.minus(retention);

    LocalDateTime modifiedAt = isDeletion ? threshold.minusMinutes(1)
        : threshold.plusMinutes(1);

    savePostImageData(createPostImage("image-" + imageId, imageStatus), modifiedAt);
  }

  public PostImage createPostImage(String imageId, ImageStatus status) {
    return new PostImage(
        imageId,
        imageId + "_stored.jpg",
        "http://localhost:8080/" + imageId + ".jpg",
        1,
        status
    );
  }

  /**
   * 데이터 삽입
   *
   * @param postImage
   * @param modifiedAt
   */
  private void savePostImageData(PostImage postImage, LocalDateTime modifiedAt) {
    jdbcTemplate.update("""
            insert into post_images(post_image_id, post_id, stored_file_name, image_order, image_url, status, created_at, modified_at)
            values(?, ?, ?, ?, ?, ?, ?, ?)
            """,
        postImage.getPostImageId(),
        null,
        postImage.getStoredName(),
        1,
        postImage.getImageUrl(),
        postImage.getStatus().name(),
        LocalDateTime.now(),
        modifiedAt
    );
  }
}
