package com.threadly.testsupport.fixture.posts;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.adapter.post.PostCommentAdapter;
import com.threadly.posts.comment.PostComment;
import com.threadly.testsupport.dto.posts.PostCommentFixtureDto;
import com.threadly.testsupport.fixture.FixtureLoader;
import com.threadly.testsupport.mapper.posts.PostCommentFixtureMapper;
import com.threadly.utils.TestLogUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 댓글 로더
 */
@RequiredArgsConstructor
@Component
public class PostCommentFixtureLoader {

  private final PostFixtureLoader postFixtureLoader;

  @PersistenceContext
  private final EntityManager entityManager;

  private final PostCommentAdapter postCommentAdapter;


  /**
   * 전체 댓글 삽입
   *
   * @param userDataPath
   * @param postDataPath
   * @param postCommentDataPath
   */
  @Transactional
  public void load(String userDataPath, String postDataPath, String postCommentDataPath) {
    loadPostFixture(userDataPath, postDataPath);

    List<PostCommentFixtureDto> postData = getPostCommentData(postCommentDataPath);
    generatePostComment(postData, postData.size());
  }

  /**
   * 게시글 댓글 저장
   *
   * @param postFixtureDtoList
   * @param count
   */
  private void generatePostComment(List<PostCommentFixtureDto> postFixtureDtoList, int count) {
    List<PostCommentFixtureDto> fixtures = postFixtureDtoList;

    if (count > fixtures.size() || count < 1) {
      count = fixtures.size();
    }

    for (int i = 0; i < count; i++) {
      PostCommentFixtureDto dto = fixtures.get(i);
      PostComment postComment = PostCommentFixtureMapper.toPostComment(dto);

      postCommentAdapter.savePostComment(postComment);
    }

    /*트랜잭션 커밋 전에 insert 쿼리 강제 실행*/
    entityManager.flush();
    entityManager.clear();

    TestLogUtils.log("게시글 댓글 데이터 생성 완료 : 총 " + count + "개");
  }

  /**
   * 게시글 댓글 데이터 불러오기
   *
   * @param path
   * @return
   */
  private static List<PostCommentFixtureDto> getPostCommentData(String path) {
    return
        FixtureLoader.load(path, new TypeReference<>() {
        });
  }

  /**
   * 게시글 데이터 삽입
   */
  private void loadPostFixture(String userDataPath, String postDataPath) {
    postFixtureLoader.load(userDataPath, postDataPath);
  }

}
