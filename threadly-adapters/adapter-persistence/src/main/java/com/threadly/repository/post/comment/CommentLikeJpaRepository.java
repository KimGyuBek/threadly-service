package com.threadly.repository.post.comment;

import com.threadly.entity.post.CommentLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * CommentLikeEntity Jpa Repository
 */
public interface CommentLikeJpaRepository extends JpaRepository<CommentLikeEntity, String> {

  /**
   * userId, commentId로 일치하는 데이터가 있는지 조회
   *
   * @param userId
   * @param commentId
   * @return
   */
  @Query(value = """
      select count(c) > 0
      from CommentLikeEntity c
      where c.id.userId = :userId
      and c.id.commentId = :commentId
      """)
  boolean existsByCommentIdAndUserId(@Param("userId") String userId,
      @Param("commentId") String commentId);

  /**
   * commentId에 해당하는 데이터 수 조회
   *
   * @param commentId
   * @return
   */
  @Query(value = """
      select count(c)
      from CommentLikeEntity c
      where c.id.commentId = :commentId
      """)
  long countByCommentId(@Param("commentId") String commentId);
}
