package com.threadly.testsupport.fixture.posts;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.adapter.persistence.post.adapter.PostCommandQueryPersistenceAdapter;
import com.threadly.core.domain.post.Post;
import com.threadly.testsupport.dto.posts.PostFixtureDto;
import com.threadly.testsupport.fixture.FixtureLoader;
import com.threadly.testsupport.fixture.users.UserFixtureLoader;
import com.threadly.testsupport.mapper.posts.PostFixtureMapper;
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
public class PostFixtureLoader {

  private final PostCommandQueryPersistenceAdapter postPersistenceAdapter;
  private final UserFixtureLoader userFixtureLoader;

  @PersistenceContext
  private final EntityManager entityManager;


  /**
   * 전체 게시글 데이터 삽입
   *
   * @param postDataPath
   */
  @Transactional
  public void load(String userDataPath, String postDataPath) {
    loadUserFixture(userDataPath);
    List<PostFixtureDto> postData = getPostData(postDataPath);
    generatePost(postData, postData.size());
  }


  /**
   * count 만큼 게시글 데이터 삽입
   *
   * @param path
   * @param count
   */
  @Transactional
  public void load(String path, int count) {
    List<PostFixtureDto> postData = getPostData(path);
    generatePost(postData, count);
  }

  /**
   * 사용자 데이터 삽입
   */
  public void loadUserFixture(String userDataPath) {
    userFixtureLoader.load(userDataPath);
  }

  /**
   * 게시글 저장
   *
   * @param postFixtureDtoList
   * @param count
   */
  private void generatePost(List<PostFixtureDto> postFixtureDtoList, int count) {
    List<PostFixtureDto> fixtures = postFixtureDtoList;

    if (count > fixtures.size() || count < 1 || count == 0) {
      count = fixtures.size();
    }

    for (int i = 0; i < count; i++) {
      PostFixtureDto dto = fixtures.get(i);
      Post post = PostFixtureMapper.toPost(dto);

      postPersistenceAdapter.savePost(post);
    }

    /*트랜잭션 커밋 전에 insert 쿼리 강제 실행*/
    entityManager.flush();
    entityManager.clear();

    TestLogUtils.log("게시글 데이터 생성 완료 : 총 " + count + "개");
  }

  /**
   * 게시글 데이터 불러오기
   *
   * @param path
   * @return
   */
  private static List<PostFixtureDto> getPostData(String path) {
    return
        FixtureLoader.load( path, new TypeReference<>() {
        });
  }


}
