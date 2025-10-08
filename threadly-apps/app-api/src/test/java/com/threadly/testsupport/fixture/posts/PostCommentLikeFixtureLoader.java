package com.threadly.testsupport.fixture.posts;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.adapter.persistence.post.adapter.PostCommentLikerAdapterQuery;
import com.threadly.core.domain.post.comment.CommentLike;
import com.threadly.testsupport.dto.posts.PostCommentLikeFixtureDto;
import com.threadly.testsupport.fixture.FixtureLoader;
import com.threadly.testsupport.mapper.posts.PostCommentLikeFixtureMapper;
import com.threadly.utils.TestLogUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 댓글 좋아요 로더
 */
@RequiredArgsConstructor
@Component
public class PostCommentLikeFixtureLoader {

  private final PostCommentFixtureLoader postCommentFixtureLoader;

  @PersistenceContext
  private final EntityManager entityManager;

  private final PostCommentLikerAdapterQuery postCommentLikeAdapter;


  /**
   * 전체 댓글 좋아요 삽입
   *
   * @param userDataPath
   * @param postDataPath
   * @param postCommentDataPath
   */
  @Transactional
  public void load(String userDataPath, String postDataPath, String postCommentDataPath,
      String postCommentLikeDataPath) {
    loadPostCommentFixture(userDataPath, postDataPath, postCommentDataPath);

    List<PostCommentLikeFixtureDto> postData = getPostCommentLikeData(postCommentLikeDataPath);
    generatePostCommentLike(postData, postData.size());
  }

  /**
   * 게시글 댓글 좋아요 저장
   *
   * @param count
   */
  private void generatePostCommentLike(List<PostCommentLikeFixtureDto> dtoList, int count) {
    List<PostCommentLikeFixtureDto> fixtures = dtoList;

    if (count > fixtures.size() || count < 1) {
      count = fixtures.size();
    }

    for (int i = 0; i < count; i++) {
      PostCommentLikeFixtureDto dto = fixtures.get(i);
      CommentLike commentLike = PostCommentLikeFixtureMapper.toPostComment(dto);

      postCommentLikeAdapter.createPostCommentLike(commentLike);
    }

    /*트랜잭션 커밋 전에 insert 쿼리 강제 실행*/
    entityManager.flush();
    entityManager.clear();

    TestLogUtils.log("게시글 댓글 좋아요 데이터 생성 완료 : 총 " + count + "개");
  }

  /**
   * 게시글 댓글 좋아요 데이터 불러오기
   *
   * @param path
   * @return
   */
  private static List<PostCommentLikeFixtureDto> getPostCommentLikeData(String path) {
    return
        FixtureLoader.load(path, new TypeReference<>() {
        });
  }

  /**
   * 게시글 댓글 데이터 삽입
   */
  private void loadPostCommentFixture(String userDataPath, String postDataPath,
      String postCommentDataPath) {
    postCommentFixtureLoader.load(userDataPath, postDataPath, postCommentDataPath);
  }

}
