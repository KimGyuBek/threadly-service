package com.threadly.post;

import static com.threadly.posts.PostStatusType.ARCHIVE;
import static com.threadly.posts.PostStatusType.BLOCKED;
import static com.threadly.posts.PostStatusType.DELETED;

import com.threadly.ErrorCode;
import com.threadly.exception.post.PostException;
import com.threadly.post.like.FetchPostLikePort;
import com.threadly.post.projection.PostDetailProjection;
import com.threadly.post.query.GetPostEngagementQuery;
import com.threadly.post.query.GetPostListQuery;
import com.threadly.post.query.GetPostQuery;
import com.threadly.post.response.PostDetailApiResponse;
import com.threadly.post.response.PostDetailListApiResponse;
import com.threadly.post.response.PostDetailListApiResponse.NextCursor;
import com.threadly.post.response.PostEngagementApiResponse;
import com.threadly.posts.PostStatusType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 조회 관련 서비스
 */
@Service
@RequiredArgsConstructor
public class PostQueryService implements GetPostUseCase, GetPostEngagementUseCase {

  private final FetchPostPort fetchPostPort;

  @Override
  public PostDetailListApiResponse getUserVisiblePostListByCursor(GetPostListQuery query) {

    List<PostDetailApiResponse> allPostList = fetchPostPort.findUserVisiblePostListByCursor(
            query.getUserId(), query.getCursorPostedAt(), query.getCursorPostId(), query.getLimit() + 1)
        .stream().map(
            projection -> new PostDetailApiResponse(
                projection.getPostId(),
                projection.getUserId(),
                projection.getUserProfileImageUrl(),
                projection.getUserNickname(),
                projection.getContent(),
                projection.getViewCount(),
                projection.getPostedAt(),
                projection.getLikeCount(),
                projection.getCommentCount(),
                projection.isLiked())).toList();


    /*다음 페이지가 있는지 검증*/
    boolean hasNext = allPostList.size() > query.getLimit();

    /*리스트 분할*/
    List<PostDetailApiResponse> pagedPostList =
        hasNext
            ? allPostList.subList(0, query.getLimit())
            : allPostList;

    /*커서 지정*/
    LocalDateTime cursorPostedAt =
        hasNext ? pagedPostList.getLast().postedAt() : null;
    String cursorPostId = hasNext ? pagedPostList.getLast().postId() : null;

    return new PostDetailListApiResponse(pagedPostList,
        new NextCursor(cursorPostedAt, cursorPostId));

  }

  @Transactional(readOnly = true)
  @Override
  public PostDetailApiResponse getPost(GetPostQuery query) {
    PostDetailProjection postDetailProjection = fetchPostPort.findPostDetailsByPostIdAndUserId(
            query.getPostId(), query.getUserId())
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));


    /*TODO 도메인 로직으로 변경*/
    PostStatusType status = postDetailProjection.getPostStatus();
    if (status == DELETED) {
      throw new PostException(ErrorCode.POST_ALREADY_DELETED);
    } else if (status == ARCHIVE) {
      throw new PostException(ErrorCode.POST_NOT_FOUND);
    } else if (status == BLOCKED) {
      throw new PostException(ErrorCode.POST_BLOCKED);
    }

    return new PostDetailApiResponse(postDetailProjection.getPostId(),
        postDetailProjection.getUserId(),
        postDetailProjection.getUserProfileImageUrl(), postDetailProjection.getUserNickname(),
        postDetailProjection.getContent(), postDetailProjection.getViewCount(),
        postDetailProjection.getPostedAt(), postDetailProjection.getLikeCount(),
        postDetailProjection.getCommentCount(), postDetailProjection.isLiked());
  }

  @Transactional(readOnly = true)
  @Override
  public PostEngagementApiResponse getPostEngagement(GetPostEngagementQuery query) {
    return
        fetchPostPort.findPostEngagementByPostIdAndUserId(
            query.getPostId(), query.getUserId()
        ).map(projection -> new PostEngagementApiResponse(
            projection.getPostId(),
            projection.getAuthorId(),
            projection.getAuthorNickname(),
            projection.getAuthorProfileImageUrl(),
            projection.getContent(),
            projection.getLikeCount(),
            projection.isLiked()
        )).orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
  }
}
