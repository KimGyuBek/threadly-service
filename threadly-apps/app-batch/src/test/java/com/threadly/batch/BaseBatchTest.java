package com.threadly.batch;

import com.threadly.adapter.persistence.post.entity.PostEntity;
import com.threadly.adapter.persistence.post.repository.PostImageJpaRepository;
import com.threadly.adapter.persistence.post.repository.PostJpaRepository;
import com.threadly.adapter.persistence.user.entity.UserEntity;
import com.threadly.adapter.persistence.user.repository.UserJpaRepository;
import com.threadly.batch.properties.RetentionProperties;
import com.threadly.core.domain.image.ImageStatus;
import com.threadly.core.domain.post.PostImage;
import com.threadly.core.domain.post.PostStatus;
import com.threadly.core.domain.user.User;
import com.threadly.core.domain.user.UserStatusType;
import com.threadly.core.domain.user.UserType;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ActiveProfiles("test")
@SpringBatchTest
@SpringBootTest(
    classes = {
        BatchApplication.class,
    }
)
@SpringJUnitConfig(classes = {BatchApplication.class})
@TestPropertySource(properties = {
    "jwt.enabled=false",
    "ttl.enabled=false",
    "spring.batch.job.enabled=false"
})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
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
  public UserJpaRepository userRepository;

  @Autowired
  public PostJpaRepository postRepository;

  @Autowired
  public RetentionProperties retentionProperties;

  @BeforeEach
  void setUp() {
    // 테스트 전 배치 메타데이터 정리
    jobRepositoryTestUtils.removeJobExecutions();
    // 테스트 데이터 정리
    postImageRepository.deleteAll();
    userRepository.deleteAll();
    postRepository.deleteAll();
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

  /**
   * 주어진 파라미터에 해당하는 User 데이터 생성
   *
   * @param userId
   * @param userStatusType
   * @param isDeletion  true면 threshold 이전(삭제 대상)으로, false면 현재시각(비 삭제 대상)
   */
  public void createUserTestData(String userId, UserStatusType userStatusType, boolean isDeletion) {
    Duration retention = retentionProperties.getUser().getDeleted();
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime threshold = now.minus(retention);

    LocalDateTime modifiedAt = isDeletion ? threshold.minusMinutes(1)
        : threshold.plusMinutes(1);

    saveUserData(createUser(userId, userStatusType), modifiedAt);
  }

  public UserEntity createUser(String userId, UserStatusType statusType) {
    return new UserEntity(
        userId, // userId
        "Test User " + userId, // userName
        "password123", // password
        userId + "@test.com", // email
        "010-1234-5678", // phone
        UserType.USER, // userType
        statusType, // userStatusType
        false, // isEmailVerified
        false // isPrivate
    );
  }

  /**
   * User 데이터 삽입
   *
   * @param user
   * @param modifiedAt
   */
  private void saveUserData(UserEntity user, LocalDateTime modifiedAt) {
    jdbcTemplate.update("""
            insert into users(user_id, user_name, password, email, phone, user_type, status, is_email_verified, is_private, created_at, modified_at)
            values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
        user.getUserId(),
        user.getUserName(),
        user.getPassword(),
        user.getEmail(),
        user.getPhone(),
        user.getUserType() != null ? user.getUserType().name() : null,
        user.getUserStatusType().name(),
        user.isEmailVerified(),
        user.isPrivate(),
        LocalDateTime.now(),
        modifiedAt
    );
  }

  /**
   * 주어진 파라미터에 해당하는 Post 데이터 생성
   *
   * @param postId
   * @param postStatus
   * @param isDeletion  true면 threshold 이전(삭제 대상)으로, false면 현재시각(비 삭제 대상)
   */
  public void createPostTestData(String postId, PostStatus postStatus, boolean isDeletion) {
    Duration retention = retentionProperties.getPost().getDeleted();
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime threshold = now.minus(retention);

    LocalDateTime modifiedAt = isDeletion ? threshold.minusMinutes(1)
        : threshold.plusMinutes(1);

    // Create a test user for the post if it doesn't exist
    String testUserId = "test-user-" + postId;
    if (userRepository.findById(testUserId).isEmpty()) {
      createUserTestData(testUserId, UserStatusType.ACTIVE, false);
    }

    savePostData(createPost(postId, postStatus, testUserId), modifiedAt);
  }

  public PostEntity createPost(String postId, PostStatus status, String userId) {
    return new PostEntity(
        postId, // postId
        UserEntity.fromId(userId), // user (create from userId)
        "Test content for " + postId, // content
        0, // viewCount
        status // status
    );
  }

  /**
   * Post 데이터 삽입
   *
   * @param post
   * @param modifiedAt
   */
  private void savePostData(PostEntity post, LocalDateTime modifiedAt) {
    jdbcTemplate.update("""
            insert into posts(post_id, user_id, content, view_count, status, created_at, modified_at)
            values(?, ?, ?, ?, ?, ?, ?)
            """,
        post.getPostId(),
        post.getUser().getUserId(), // get user_id from User entity
        post.getContent(),
        post.getViewCount(),
        post.getStatus().name(),
        LocalDateTime.now(),
        modifiedAt
    );
  }
}
