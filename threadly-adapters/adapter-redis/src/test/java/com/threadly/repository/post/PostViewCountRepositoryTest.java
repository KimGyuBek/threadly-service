package com.threadly.repository.post;

import static org.assertj.core.api.Assertions.assertThat;

import com.threadly.RedisTestApplication;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * 게시글 조회 수 증가 관련 Redis 레파지토리 테스트
 */
@ActiveProfiles("test")
@SpringBootTest(classes = {RedisTestApplication.class})
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class PostViewCountRepositoryTest {

  @Autowired
  private PostViewCountRepository postViewCountRepository;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;


  @BeforeEach
  void setUp() {
    redisTemplate.getConnectionFactory().getConnection().flushDb();
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("게시글 조회 기록 저장 테스트")
  @Nested
  class RecordPostViewTest {

    /*[Case #1] recordPostView - 조회 기록이 존재하지 않을 경우 저장에 성공햐야한다*/
    @Order(1)
    @DisplayName("1. 조회 기록이 존재 하지 않을 경우 저장 검증")
    @Test
    public void recordPostView_shouldStoreRecordView_whenRecordNotExists() throws Exception {
      //given
      String postId = "post1";
      String userId = "user1";
      String key = "view:" + postId + ":" + userId;
      Duration ttl = Duration.ofSeconds(3);

      //when
      /*저장*/
      postViewCountRepository.recordPostView(postId, userId, ttl);

      //then
      Boolean result = redisTemplate.hasKey(key);
      assertThat(result).isTrue();
    }

    /*[Case #2] recordPostView - 조회 기록이 존재 할  경우 저장에 성공햐야한다*/
    @Order(2)
    @DisplayName("2. 조회 기록이 존재 할 경우 저장 성공 검증")
    @Test
    public void recordPostView_shouldStoreRecordView_whenRecordExists() throws Exception {
      //given
      String postId = "post1";
      String userId = "user1";
      String key = "view:" + postId + ":" + userId;
      Duration ttl = Duration.ofSeconds(3);

      redisTemplate.opsForSet().add(key, postId);

      //when
      /*저장*/
      postViewCountRepository.recordPostView(postId, userId, ttl);

      //then
      Boolean result = redisTemplate.hasKey(key);
      assertThat(result).isTrue();
    }
  }


  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("데이터 검증 로작 테스트")
  @Nested
  class ExistsPostViewTest {

    /*[Case #1] existPostView - 데이터가 존재 하지 않을 경우 검증*/
    @Order(1)
    @DisplayName("1. 데이터가 존재 할 경우 검증")
    @Test
    public void existPostView_shouldReturnTrue_whenRecordNotExists() throws Exception {
      //given
      String postId = "post1";
      String userId = "user1";
      String key = "view:" + postId + ":" + userId;

      //when
      //then
      boolean result = postViewCountRepository.existsPostView(postId, userId);

      assertThat(result).isFalse();

    }

    /*[Case #2] existPostView - 데이터가 존재 하는 경우 검증*/
    @Order(2)
    @DisplayName("2. 데이터가 존재 하지 않을 경우 검증")
    @Test
    public void existPostView_shouldReturnTrue_whenRecordExists() throws Exception {
      //given
      String postId = "post1";
      String userId = "user1";
      String key = "view:" + postId + ":" + userId;

      //when
      /*값 저장*/
      redisTemplate.opsForValue().set(key, "1", Duration.ofSeconds(3));

      //then
      boolean result = postViewCountRepository.existsPostView(postId, userId);

      assertThat(result).isTrue();
    }

  }


}