package com.threadly.post.comment;

import com.threadly.commons.dto.UserPreview;
import com.threadly.exception.ErrorCode;
import com.threadly.exception.post.PostException;
import com.threadly.post.PostStatus;
import com.threadly.post.comment.fetch.FetchPostCommentPort;
import com.threadly.post.comment.get.GetPostCommentApiResponse;
import com.threadly.post.comment.get.GetPostCommentDetailQuery;
import com.threadly.post.comment.get.GetPostCommentListQuery;
import com.threadly.post.comment.get.GetPostCommentUseCase;
import com.threadly.post.comment.get.GetPostCommentsApiResponse;
import com.threadly.post.comment.get.GetPostCommentsApiResponse.NextCursor;
import com.threadly.post.fetch.FetchPostPort;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 댓글 조회 관련 서비스
 */
@Service
@RequiredArgsConstructor
public class PostCommentQueryService implements GetPostCommentUseCase {

  private final FetchPostCommentPort fetchPostCommentPort;

  private final FetchPostPort fetchPostPort;

  @Transactional(readOnly = true)
  @Override
  public GetPostCommentsApiResponse getPostCommentDetailListForUser(
      GetPostCommentListQuery query
  ) {

    /*게시글 유효성 검증*/
    PostStatus posStatus = fetchPostPort.fetchPostStatusByPostId(query.postId())
        .orElseThrow(() -> new PostException(
            ErrorCode.POST_NOT_FOUND));
    if (!posStatus.equals(PostStatus.ACTIVE)) {
      throw new PostException(ErrorCode.POST_NOT_ACCESSIBLE);
    }

    /*게시글 댓글 목록 조회*/
    List<GetPostCommentApiResponse> allCommentList = fetchPostCommentPort.fetchCommentListByPostIdWithCursor(
        query.postId(),
        query.userId(),
        query.cursorCommentedAt(),
        query.cursorCommentId(),
        query.limit() + 1
    ).stream().map(
        projection -> new GetPostCommentApiResponse(
            projection.getPostId(),
            projection.getCommentId(),
            new UserPreview(
                projection.getCommenterId(),
                projection.getCommenterNickname(),
                projection.getCommenterProfileImageUrl()
            ),
            projection.getCommentedAt(),
            projection.getLikeCount(),
            projection.getContent(),
            projection.isLiked()
        )
    ).toList();

    /*다음 페이지가 있는지 검증*/
    boolean hasNext = allCommentList.size() > query.limit();

    /*리스트 분할*/
    List<GetPostCommentApiResponse> pagedCommentList =
        hasNext ? allCommentList.subList(0, query.limit()) : allCommentList;

    /* 커서 지정*/
    LocalDateTime cursorCommentedAt =
        hasNext ? pagedCommentList.getLast().commentedAt() : null;

    String cursorCommentId = hasNext ? pagedCommentList.getLast().commentId() : null;

    return new GetPostCommentsApiResponse(
        pagedCommentList,
        new NextCursor(cursorCommentedAt, cursorCommentId)
    );
  }

  @Override
  public GetPostCommentApiResponse getPostCommentDetailForUser(
      GetPostCommentDetailQuery query) {

    /*게시글 상태 검증*/

    /*게시글 댓글 상태 검증*/

    /*대댓글 목록 조회*/

    return null;
  }
}

