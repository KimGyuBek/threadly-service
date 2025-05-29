package com.threadly.post.comment;

import com.threadly.ErrorCode;
import com.threadly.exception.post.PostException;
import com.threadly.post.comment.fetch.FetchPostCommentPort;
import com.threadly.post.comment.get.GetPostCommentApiResponse;
import com.threadly.post.comment.get.GetPostCommentDetailQuery;
import com.threadly.post.comment.get.GetPostCommentListApiResponse;
import com.threadly.post.comment.get.GetPostCommentListApiResponse.NextCursor;
import com.threadly.post.comment.get.GetPostCommentListQuery;
import com.threadly.post.comment.get.GetPostCommentUseCase;
import com.threadly.post.fetch.FetchPostPort;
import com.threadly.posts.PostStatusType;
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
  public GetPostCommentListApiResponse getPostCommentDetailListForUser(
      GetPostCommentListQuery query
  ) {

    /*게시글 유효성 검증*/
    PostStatusType posStatus = fetchPostPort.fetchPostStatusByPostId(query.postId())
        .orElseThrow(() -> new PostException(
            ErrorCode.POST_NOT_FOUND));
    if (!posStatus.equals(PostStatusType.ACTIVE)) {
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
            projection.getCommenterId(),
            projection.getCommenterNickname(),
            projection.getCommenterProfileImageUrl(),
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

    return new GetPostCommentListApiResponse(
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

