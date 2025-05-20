package com.threadly.repository.post;

import com.threadly.entity.post.PostLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * PostLikeEntity Jpa Repository
 */
public interface PostLikeJpaRepository extends JpaRepository<PostLikeEntity, String> {

  /**
   * userId, postId에 일치하는 데이터가 있는지 조회
   *
   * @param postId
   * @param userId
   * @return
   */
  @Query("""
      select count(p) > 0 
      from PostLikeEntity p 
      where p.id.postId=:postId
      and p.id.userId=:userId
      """)
  boolean existByPostIdAndUserId(@Param("postId") String postId, @Param("userId") String userId);

  /**
   * postId로 데이터 수 조회
   * @param postId
   * @return
   */
  @Query("""
      select count(p)
      from PostLikeEntity p
      where p.id.postId=:postId
      """)
  long countByPostId(@Param("postId") String postId);

}
