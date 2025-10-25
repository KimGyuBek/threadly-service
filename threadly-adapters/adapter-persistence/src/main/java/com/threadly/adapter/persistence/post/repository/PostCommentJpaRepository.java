package com.threadly.adapter.persistence.post.repository;

import com.threadly.adapter.persistence.post.entity.PostCommentEntity;
import com.threadly.core.domain.post.PostCommentStatus;
import com.threadly.core.port.post.out.comment.PostCommentDetailForUserProjection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * PostCommentEntity Jpa Repository
 */
public interface PostCommentJpaRepository extends JpaRepository<PostCommentEntity, String> {

  /**
   * 댓글 상태 변경
   *
   * @param commentId
   * @param status
   */
  @Modifying
  @Query(value = """
      update PostCommentEntity pc
      set pc.status = :status
      where pc.commentId=:commentId
      """)
  void updatePostCommentStatus(
      @Param("commentId") String commentId,
      @Param("status") PostCommentStatus status);


  @Query(value = """
      select pc.post_id           as PostId,
             pc.comment_id        as comment_id,
             pc.user_id           as commenterId,
             up.nickname          as commenterNickname,
             upi.image_url        as commenterProfileImageUrl,
            coalesce(cl.like_count, 0)  as likeCount,
             pc.created_at        as commentedAt,
             pc.content           as content,
             coalesce(cl.liked, false) as liked,
             pc.status            as status
      from post_comments pc
               join users u on pc.user_id = u.user_id
               join user_profile up on u.user_id = up.user_id
               left join user_profile_images upi on up.user_id = upi.user_id
               left join(select comment_id,
                                count(*) like_count,
                                max(
                                        case
                                            when user_id = :userId
                                                then 1
                                            else 0
                                            end
                                ) > 0 as     liked
                         from comment_likes
                         where comment_id in
                               (select comment_id from post_comments where post_id = :postId)
                         group by comment_id) cl on pc.comment_id = cl.comment_id
      where pc.post_id = :postId;
      """, nativeQuery = true)
  Optional<PostCommentDetailForUserProjection> findPostCommentDetailForUserByPostId(
      @Param("postId") String postId, @Param("userId") String userId);

  @Query(value = """
      select pc.post_id                         as postId,
             pc.comment_id                      as commentId,
             pc.user_id                         as commenterId,
             up.nickname                        as commenterNickname,
             upi.image_url                      as commenterProfileImageUrl,
             coalesce(like_count.like_count, 0) as likeCount,
             pc.created_at                      as commentedAt,
             pc.content                         as content,
             coalesce(user_liked.liked, false)  as liked
      from post_comments pc
               join users u on pc.user_id = u.user_id
               join user_profile up on u.user_id = up.user_id
               left join user_profile_images upi on up.user_id = upi.user_id
               left join(select comment_id,
                                count(*) as like_count
                         from comment_likes
                         group by comment_id) like_count on pc.comment_id = like_count.comment_id
               left join(select distinct comment_id,
                                true as liked
                         from comment_likes
                         where user_id = :userId
                         ) user_liked on pc.comment_id = user_liked.comment_id
      where pc.status = 'ACTIVE' and pc.post_id = :postId
        and (
        cast(:cursorCommentedAt as timestamp) is null
          or pc.created_at < :cursorCommentedAt
          or (pc.created_at = :cursorCommentedAt and pc.comment_id < :cursorCommentId))
      order by pc.created_at desc, pc.comment_id desc
      limit :limit
      """, nativeQuery = true)
  List<PostCommentDetailForUserProjection> findPostCommentListForUserByPostId(
      @Param("postId") String postId, @Param("userId") String userId, @Param("cursorCommentedAt")
      LocalDateTime cursorCommentedAt, @Param("cursorCommentId") String cursorCommentId,
      @Param("limit") int limit);

  /**
   * 게시글 댓글 상태 조회
   *
   * @param commentId
   * @return
   */
  @Query(value = """
      select pc.status
      from post_comments pc
      where pc.comment_id = :commentId;
      
      """, nativeQuery = true)
  Optional<PostCommentStatus> findPostCommentStatus(@Param("commentId") String commentId);

  /**
   * postId에 해당하는 댓글 상태 변경
   *
   * @param postId
   * @param status
   */
  @Modifying
  @Query("""
      update PostCommentEntity pc 
      set pc.status = :status
      where pc.post.postId = :postId
      """)
  void updateCommentStatusByPostId(@Param("postId") String postId,
      @Param("status") PostCommentStatus status);

  /**
   * postId와 status에 부합하는 데이터 수 반환
   *
   * @param status
   * @param postId
   * @return
   */
  @Query("""
      select count(*) from PostCommentEntity pc
      where pc.post.postId = :postId and pc.status = :status
      """)
  long countByStatusAndPostId(@Param("status") PostCommentStatus status,
      @Param("postId") String postId);
}
