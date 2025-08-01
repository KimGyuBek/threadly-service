package com.threadly.repository;

import com.threadly.entity.user.UserProfileImageEntity;
import com.threadly.mapper.user.UserProfileImageMapper;
import com.threadly.user.profile.image.UserProfileImage;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test User ProfileImage Repository
 */
@Repository
public class TestUserProfileImageRepository {

  private final EntityManager em;

  public TestUserProfileImageRepository(EntityManager em) {
    this.em = em;
  }

  /**
   * 주어진 UserProfileImage 도메인 엔티티로 변환 후 저장
   *
   * @param domain
   */
  public void save(UserProfileImage domain) {
    em.persist(UserProfileImageMapper.toEntity(domain));
  }


  /**
   * 주어진 profileImageId에 해당하는 entity 조회
   *
   * @param profileImageId
   * @return
   */
  public Optional<UserProfileImageEntity> findById(String profileImageId) {
    em.flush();
    em.clear();

    Optional<UserProfileImageEntity> userProfileImageEntity = Optional.ofNullable(
        em.find(UserProfileImageEntity.class, profileImageId));
    return userProfileImageEntity;
  }
}
