package com.threadly.core.service.post;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.post.PostException;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.domain.image.ImageStatus;
import com.threadly.core.port.commons.dto.UserPreview;
import com.threadly.core.port.post.in.query.PostQueryUseCase;
import com.threadly.core.port.post.in.query.dto.GetPostEngagementApiResponse;
import com.threadly.core.port.post.in.query.dto.GetPostEngagementQuery;
import com.threadly.core.port.post.in.query.dto.GetPostQuery;
import com.threadly.core.port.post.in.query.dto.GetPostsQuery;
import com.threadly.core.port.post.in.query.dto.GetUserPostsQuery;
import com.threadly.core.port.post.in.query.dto.PostDetails;
import com.threadly.core.port.post.in.query.dto.PostDetails.PostImage;
import com.threadly.core.port.post.out.PostQueryPort;
import com.threadly.core.port.post.out.image.PostImageQueryPort;
import com.threadly.core.port.post.out.image.projection.PostImageProjection;
import com.threadly.core.port.post.out.projection.PostDetailProjection;
import com.threadly.core.service.validator.follow.FollowValidator;
import com.threadly.core.service.validator.post.PostValidator;
import com.threadly.core.service.validator.user.UserValidator;
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

  private final PostValidator postValidator;
  private final UserValidator userValidator;

  private final PostQueryPort postQueryPort;

  private final PostImageQueryPort postImageQueryPort;

  private final FollowValidator followValidator;

  @Transactional(readOnly = true)
  @Override
  public CursorPageApiResponse<PostDetails> getUserVisiblePosts(GetPostsQuery query) {

    /*게시글 상세 정보 조회*/
    List<PostDetails> postDetailsList = postQueryPort.fetchUserVisiblePostsByCursor(
            query.getUserId(), query.getCursorPostedAt(), query.getCursorPostId(), query.getLimit() + 1)
        .stream().map(
            projection -> {
              UserPreview author = new UserPreview(
                  projection.getUserId(),
                  projection.getUserNickname(),
                  projection.getUserProfileImageUrl() == null ? "/"
                      : projection.getUserProfileImageUrl()
              );

              List<PostImage> postImageList = postImageQueryPort.findAllByPostIdAndStatus(
                  projection.getPostId(),
                  ImageStatus.CONFIRMED
              ).stream().map(
                  image -> new PostImage(
                      image.getImageId(),
                      image.getImageUrl(),
                      image.getImageOrder()
                  )).toList();

              return new PostDetails(
                  projection.getPostId(),
                  author,
                  projection.getContent(),
                  postImageList,
                  projection.getViewCount(),
                  projection.getPostedAt(),
                  projection.getLikeCount(),
                  projection.getCommentCount(),
                  projection.isLiked());
            }).toList();

    return CursorPageApiResponse.from(postDetailsList, query.getLimit());
  }

  @Transactional(readOnly = true)
  @Override
  public PostDetails getPost(GetPostQuery query) {
    /*게시글 상세 정보 조회*/
    PostDetailProjection postDetailsProjection = postValidator.getPostDetailsProjectionOrElseThrow(
        query.getPostId(), query.getUserId());

    /*게시글 이미지 조회*/
    List<PostImageProjection> postImageProjections = postImageQueryPort.findAllByPostIdAndStatus(
        query.getPostId(), ImageStatus.CONFIRMED);

    /*게시글이 조회 가능한 상태인지 검증*/
    postValidator.validateAccessibleStatus(postDetailsProjection.getPostStatus());

    return new PostDetails(postDetailsProjection.getPostId(),
        new UserPreview(
            postDetailsProjection.getUserId(),
            postDetailsProjection.getUserNickname(),
            postDetailsProjection.getUserProfileImageUrl() == null ? "/"
                : postDetailsProjection.getUserProfileImageUrl()
        ),
        postDetailsProjection.getContent(),
        postImageProjections.stream().map(
            projection ->
                new PostDetails.PostImage(
                    projection.getImageId(),
                    projection.getImageUrl(),
                    projection.getImageOrder()
                )
        ).toList(),
        postDetailsProjection.getViewCount(),
        postDetailsProjection.getPostedAt(),
        postDetailsProjection.getLikeCount(),
        postDetailsProjection.getCommentCount(),
        postDetailsProjection.isLiked());
  }

  @Transactional(readOnly = true)
  @Override
  public CursorPageApiResponse<PostDetails> getUserPosts(GetUserPostsQuery query) {
    /*1. targetUserId 상태 검증*/
    userValidator.validateUserStatusWithException(query.userId());

    /*2. 팔로우 관계 검증*/
    followValidator.validateProfileAccessibleWithException(query.userId(), query.targetId());

    /*3. 사용자 게시글 조회 후 응답 생성*/
    List<PostDetails> userPostDetailsList = postQueryPort.fetchUserPostsByCursor(
        query.userId(),
        query.targetId(),
        query.cursorPostedAt(),
        query.cursorPostId(),
        query.limit() + 1
    ).stream().map(
        projection -> {
          UserPreview author = new UserPreview(
              projection.getUserId(),
              projection.getUserNickname(),
              projection.getUserProfileImageUrl()
          );

          return new PostDetails(
              projection.getPostId(),
              author,
              projection.getContent(),
              List.of(),
              projection.getViewCount(),
              projection.getPostedAt(),
              projection.getLikeCount(),
              projection.getCommentCount(),
              projection.isLiked()
          );
        }
    ).toList();

    return CursorPageApiResponse.from(userPostDetailsList, query.limit());
  }

  @Transactional(readOnly = true)
  @Override
  public GetPostEngagementApiResponse getPostEngagement(GetPostEngagementQuery query) {
    return
        postQueryPort.fetchPostEngagementByPostIdAndUserId(
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
