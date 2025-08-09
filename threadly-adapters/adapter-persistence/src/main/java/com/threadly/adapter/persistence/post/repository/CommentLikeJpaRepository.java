package com.threadly.adapter.persistence.post.repository;

import com.threadly.adapter.persistence.post.entity.CommentLikeEntity;
import com.threadly.core.port.post.like.comment.PostCommentLikerProjection;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

  /**
   * userId, commentId에 해당하는 데이터 삭제
   *
   * @param commentId
   * @param userId
   */
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(value = """
      delete from CommentLikeEntity  c 
      where c.id.commentId = :commentId 
      and c.id.userId = :userId
      """)
  void deleteByCommentIdAndUserId(@Param("commentId") String commentId,
      @Param("userId") String userId);

  /**
   * 특정 게시글에 좋아요를 누른 사람 목록을 커서 기반으로 조회
   * <p>
   * 가장 최근 좋아요 부터 생성일(created_at)을 기준으로 내림차순 정렬되며, 커서 값 보다 이전에 생성된 좋아여를 누른 사용자들을 조회
   *
   * @param commentId
   * @param userId
   * @param cursorLikedAt
   * @param cursorLikerId
   * @return
   */
  @Query(value = """
      select cl.user_id           as likerId,
             up.nickname          as likerNickname,
             upi.image_url        as likerProfileImageUrl,
             up.bio               as likerBio,
             cl.created_at        as likedAt
      from comment_likes cl
               join users u on cl.user_id = u.user_id
               join user_profile up on u.user_id = up.user_id
               join post_comments pc on cl.comment_id = pc.comment_id
               left join user_profile_images upi on up.user_id = upi.user_id
      where pc.status = 'ACTIVE'
        and pc.comment_id = :commentId
        and (:cursorLikedAt is null
          or cl.created_at < :cursorLikedAt
          or (cl.created_at = :cursorLikedAt and cl.user_id < :cursorLikerId))
      order by cl.created_at desc, cl.user_id desc
      limit :limit;
      """, nativeQuery = true)
  List<PostCommentLikerProjection> findPostLikersByCommentIdWithCursor(@Param("commentId")
      String commentId, @Param("cursorLikedAt")
      LocalDateTime cursorLikedAt, @Param("cursorLikerId") String cursorLikerId,
      @Param("limit") int limit);

  /**
   * postId에 해당하는 댓글 목록의 좋아요 전체 삭제
   *
   * @param postId
   */
  @Modifying()
  @Query(value = """
      delete
      from comment_likes cl
      where cl.comment_id in (select pc.comment_id
                              from post_comments pc
                              where pc.post_id = :postId)
      """, nativeQuery = true)
  void deleteAllByPostId(@Param("postId") String postId);

  /**
   * posId에 해당하는 데이터 수 조회
   *
   * @param postId
   * @return
   */
  @Query("""
      select count(c) from CommentLikeEntity c
       where c.comment.post.postId = :postId
      """)
  long countByComment_Post_PostId(@Param("postId") String postId);
}
