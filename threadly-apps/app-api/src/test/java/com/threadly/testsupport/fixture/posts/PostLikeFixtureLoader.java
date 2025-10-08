package com.threadly.testsupport.fixture.posts;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.adapter.persistence.post.adapter.PostCommandPersistenceAdapter;
import com.threadly.adapter.persistence.post.adapter.PostLikePersistenceAdapter;
import com.threadly.core.domain.post.PostLike;
import com.threadly.testsupport.dto.posts.PostLikeFixtureDto;
import com.threadly.testsupport.fixture.FixtureLoader;
import com.threadly.testsupport.fixture.users.UserFixtureLoader;
import com.threadly.testsupport.mapper.posts.PostLikeFixtureMapper;
import com.threadly.utils.TestLogUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 로더
 */
@RequiredArgsConstructor
@Component
public class PostLikeFixtureLoader {

  private final PostCommandPersistenceAdapter postPersistenceAdapter;

  private final UserFixtureLoader userFixtureLoader;
  private final PostFixtureLoader postFixtureLoader;

  @PersistenceContext
  private final EntityManager entityManager;
  private final PostLikePersistenceAdapter postLikePersistenceAdapter;


  /**
   * 전체 좋아요 데이터 삽입
   *
   * @param userDataPath
   * @param postDataPath
   * @param postLikeData
   */
  @Transactional
  public void load(String userDataPath, String postDataPath, String postLikeData) {
    loadPostFixture(userDataPath, postDataPath);

    List<PostLikeFixtureDto> postData = getPostLikeData(postLikeData);
    generatePostLike(postData, postData.size());
  }

  /**
   * 게시글 좋아요 데이터 삽입
   * @param postLikeDataFixture
   */
  @Transactional
  public void load(String postLikeDataFixture) {
    List<PostLikeFixtureDto> postData = getPostLikeData(postLikeDataFixture);
    generatePostLike(postData, postData.size());

  }


  /**
   * 게시글 좋아요 저장
   *
   * @param postFixtureDtoList
   * @param count
   */
  private void generatePostLike(List<PostLikeFixtureDto> postFixtureDtoList, int count) {
    List<PostLikeFixtureDto> fixtures = postFixtureDtoList;

    if (count > fixtures.size() || count < 1) {
      count = fixtures.size();
    }

    for (int i = 0; i < count; i++) {
      PostLikeFixtureDto dto = fixtures.get(i);
      PostLike postLike = PostLikeFixtureMapper.toPostLike(dto);

      postLikePersistenceAdapter.createPostLike(postLike);
    }

    /*트랜잭션 커밋 전에 insert 쿼리 강제 실행*/
    entityManager.flush();
    entityManager.clear();

    TestLogUtils.log("게시글 좋아요 데이터 생성 완료 : 총 " + count + "개");
  }

  /**
   * 게시글 데이터 불러오기
   *
   * @param path
   * @return
   */
  private static List<PostLikeFixtureDto> getPostLikeData(String path) {
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
