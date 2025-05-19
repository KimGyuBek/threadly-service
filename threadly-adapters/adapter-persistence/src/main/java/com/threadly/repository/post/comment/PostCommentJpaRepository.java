package com.threadly.repository.post.comment;

import com.threadly.entity.post.PostCommentEntity;
import com.threadly.posts.PostCommentStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * PostCommentEntity Jpa Repository
 */
public interface PostCommentJpaRepository extends JpaRepository<PostCommentEntity, String> {

//  @Query(value = """
//      select
//       pc.commentId as commentId,
//       pc.post.postId as postId,
//       pc.user.userId as userId,
//       pc.content as content,
//       pc.status as commentStatus,
//       p.status as postStatus
//       from PostCommentEntity pc
//       join pc.post p
//       where pc.commentId = :commentId
//      """)
//  Optional<PostCommentWithPostStatusResponse> findByCommentIdWithPostStatus(
//      @Param("commentId") String commentId);

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
      @Param("status") PostCommentStatusType status);


}
