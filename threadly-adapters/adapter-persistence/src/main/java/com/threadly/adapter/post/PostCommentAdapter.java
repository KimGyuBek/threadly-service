package com.threadly.adapter.post;

import com.threadly.entity.post.PostCommentEntity;
import com.threadly.entity.post.PostEntity;
import com.threadly.entity.user.UserEntity;
import com.threadly.mapper.post.PostMapper;
import com.threadly.mapper.user.UserMapper;
import com.threadly.post.comment.SavePostCommentPort;
import com.threadly.post.comment.response.CreatePostCommentResponse;
import com.threadly.posts.Post;
import com.threadly.posts.PostComment;
import com.threadly.repository.post.comment.PostCommentJpaRepository;
import com.threadly.repository.user.UserJpaRepository;
import com.threadly.user.User;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 게시글 댓글 관련 Adapter
 */
@Repository
@RequiredArgsConstructor
public class PostCommentAdapter implements SavePostCommentPort {

  private final PostCommentJpaRepository postCommentJpaRepository;

  @Override
  public CreatePostCommentResponse savePostComment(Post post, PostComment postComment, User user) {

    UserEntity userEntity = UserMapper.toEntity(user);
    PostEntity postEntity = PostMapper.toEntity(post, userEntity);

    PostCommentEntity postCommentEntity = PostCommentEntity.newComment(
        postEntity, userEntity, postComment
    );

    postCommentJpaRepository.save(postCommentEntity);

    return new CreatePostCommentResponse(
        postComment.getCommentId(),
        postComment.getUserId(),
        postComment.getContent(),
        LocalDateTime.now()
    );
  }
}
