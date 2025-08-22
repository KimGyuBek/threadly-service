package com.threadly.adapter.redis.repository.post;

import com.threadly.core.port.post.view.RecordPostViewPort;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * 게시글 조회수 증가 관련 repository
 */
@Repository
@RequiredArgsConstructor
public class PostViewCountRepository implements RecordPostViewPort {


  private final RedisTemplate<String, String> redisTemplate;

  /**
   * 게시글 조회 기록 저장
   *
   * @param postId
   * @param userId
   */
  @Override
  public void recordPostView(String postId, String userId, Duration ttl) {
    String key = generateKey(postId, userId);

    /*저장*/
    redisTemplate.opsForValue().setIfAbsent(key, "1", ttl);
  }

  /**
   * 일치하는 데이터가 저장 되어 있는지 조회
   *
   * @param postId
   * @return
   */
  @Override
  public boolean existsPostView(String postId, String userId) {
    String key = generateKey(postId, userId);

    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }

  /**
   * key 생성
   *
   * @param postId
   * @return
   */
  /*key : view:{postId}:{userId}*/
  private String generateKey(String postId, String userId) {
    return "view:" + postId + ":" + userId;
  }

}
