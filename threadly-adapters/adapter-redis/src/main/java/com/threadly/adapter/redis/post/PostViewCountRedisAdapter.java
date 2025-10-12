package com.threadly.adapter.redis.post;

import com.threadly.core.port.post.out.view.RecordPostViewPort;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * 게시글 조회수 증가 관련 repository
 */
@Repository
@RequiredArgsConstructor
public class PostViewCountRedisAdapter implements RecordPostViewPort {

  private final PostViewCountRepository postViewCountRepository;

  @Override
  public void recordPostView(String postId, String userId, Duration ttl) {
    postViewCountRepository.recordPostView(postId, userId, ttl);
  }

  @Override
  public boolean existsPostView(String postI, String userId) {
    return postViewCountRepository.existsPostView(postI, userId);
  }
}
