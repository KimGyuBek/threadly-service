package com.threadly.core.service.post;

import static com.threadly.core.domain.post.PostStatus.ARCHIVE;
import static com.threadly.core.domain.post.PostStatus.BLOCKED;
import static com.threadly.core.domain.post.PostStatus.DELETED;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.post.PostException;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.domain.image.ImageStatus;
import com.threadly.core.domain.post.PostStatus;
import com.threadly.core.port.commons.dto.UserPreview;
import com.threadly.core.port.post.in.query.dto.GetPostEngagementApiResponse;
import com.threadly.core.port.post.in.query.dto.GetPostEngagementQuery;
import com.threadly.core.port.post.in.query.dto.GetPostListQuery;
import com.threadly.core.port.post.in.query.dto.GetPostQuery;
import com.threadly.core.port.post.in.query.dto.PostDetails;
import com.threadly.core.port.post.in.query.PostQueryUseCase;
import com.threadly.core.port.post.out.fetch.FetchPostPort;
import com.threadly.core.port.post.out.fetch.PostDetailProjection;
import com.threadly.core.port.post.out.image.fetch.FetchPostImagePort;
import com.threadly.core.port.post.out.image.fetch.PostImageProjection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 조회 관련 서비스
 */
@Service
@RequiredArgsConstructor
public class PostQueryService implements PostQueryUseCase {

  private final FetchPostPort fetchPostPort;

  private final FetchPostImagePort fetchPostImagePort;

  @Transactional(readOnly = true)
  @Override
  public CursorPageApiResponse<PostDetails> getUserVisiblePostListByCursor(GetPostListQuery query) {

    /*게시글 상세 정보 조회*/
    List<PostDetails> allPostList = fetchPostPort.fetchUserVisiblePostListByCursor(
            query.getUserId(), query.getCursorPostedAt(), query.getCursorPostId(), query.getLimit() + 1)
        .stream().map(
            projection -> new PostDetails(
                projection.getPostId(),
                new UserPreview(
                    projection.getUserId(),
                    projection.getUserNickname(),
                    projection.getUserProfileImageUrl()
                ),
                projection.getContent(),
                fetchPostImagePort.findAllByPostIdAndStatus(
                    projection.getPostId(),
                    ImageStatus.CONFIRMED
                ).stream().map(
                    image -> new PostDetails.PostImage(
                        image.getImageId(),
                        image.getImageUrl(),
                        image.getImageOrder()
                    )).toList(),
                projection.getViewCount(),
                projection.getPostedAt(),
                projection.getLikeCount(),
                projection.getCommentCount(),
                projection.isLiked())).toList();

    return CursorPageApiResponse.from(allPostList, query.getLimit());
  }

  @Transactional(readOnly = true)
  @Override
  public PostDetails getPost(GetPostQuery query) {
    /*게시글 상세 정보 조회*/
    PostDetailProjection postDetailProjection = fetchPostPort.fetchPostDetailsByPostIdAndUserId(
            query.getPostId(), query.getUserId())
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

    /*게시글 이미지 조회*/
    List<PostImageProjection> postImageProjections = fetchPostImagePort.findAllByPostIdAndStatus(
        query.getPostId(), ImageStatus.CONFIRMED);

    /*TODO 도메인 로직으로 변경*/
    PostStatus status = postDetailProjection.getPostStatus();
    if (status == DELETED) {
      throw new PostException(ErrorCode.POST_ALREADY_DELETED);
    } else if (status == ARCHIVE) {
      throw new PostException(ErrorCode.POST_NOT_FOUND);
    } else if (status == BLOCKED) {
      throw new PostException(ErrorCode.POST_BLOCKED);
    }

    return new PostDetails(postDetailProjection.getPostId(),
        new UserPreview(
            postDetailProjection.getUserId(),
            postDetailProjection.getUserNickname(),
            postDetailProjection.getUserProfileImageUrl()
        ),
        postDetailProjection.getContent(),
        postImageProjections.stream().map(
            projection ->
                new PostDetails.PostImage(
                    projection.getImageId(),
                    projection.getImageUrl(),
                    projection.getImageOrder()
                )
        ).toList(),
        postDetailProjection.getViewCount(),
        postDetailProjection.getPostedAt(),
        postDetailProjection.getLikeCount(),
        postDetailProjection.getCommentCount(),
        postDetailProjection.isLiked());
  }

  @Transactional(readOnly = true)
  @Override
  public GetPostEngagementApiResponse getPostEngagement(GetPostEngagementQuery query) {
    return
        fetchPostPort.fetchPostEngagementByPostIdAndUserId(
            query.getPostId(), query.getUserId()
        ).map(projection -> new GetPostEngagementApiResponse(
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
