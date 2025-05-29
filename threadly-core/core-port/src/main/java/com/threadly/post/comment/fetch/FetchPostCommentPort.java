package com.threadly.post.comment.fetch;

import com.threadly.posts.PostCommentStatusType;
import com.threadly.posts.comment.PostComment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.swing.text.html.Option;

/**
 * 게시글 댓글 조회 관련 Port
 */
public interface FetchPostCommentPort {

  /**
   * 댓글 id를 기준으로 댓글을 조회한다.
   *
   * @param commentId
   * @return
   */
  Optional<PostComment> fetchById(String commentId);

  /**
   * 댓글 id와 사용자 id를 기준으로 댓글 상세 정보를 조회한다
   * <p>
   * 주어진 사용자 기준으로 좋아요 여부 등 사용자 관련 상태도 포함된다
   *
   * @param commentId 댓글 식별자
   * @param userId    조회 대상 사용자 id
   * @return 댓글 상세 정보
   */
  Optional<PostCommentDetailForUserProjection> fetchCommentDetail(String commentId,
      String userId);


  /**
   * 지정된 postId에 대한 댓글 목록을 커서 기반으로 조회한다.
   * <p>
   * 댓글은 commentedAt과 commenterId를 기준으로 내림차순 정렬되며,커서 기준 이전 댓글들만 조회된다.
   * <p>
   * 커서가 null인 경우 가장 최신 댓글부터 조회하며, 최대 {@code limit}개 까지 조회된댜.
   *
   * @param postId
   * @param cursorCommentedAt
   * @param cursorCommenterId
   * @param limit
   * @return 댓글 상세 정보를 담은 프로젝션 목록
   */
  List<PostCommentDetailForUserProjection> fetchCommentListByPostIdWithCursor(String postId,
      String userId,
      LocalDateTime cursorCommentedAt, String cursorCommenterId, int limit);

  /**
   * 게시글 댓글 상태 조회
   * @param commentId
   * @return
   */
  Optional<PostCommentStatusType> fetchCommentStatus(String commentId);


}
