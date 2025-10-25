package com.threadly.repository;

import com.threadly.core.domain.user.UserStatus;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

/**
 * Test User Repository
 */
@Repository
public class TestUserRepository {

  private final EntityManager em;

  public TestUserRepository(EntityManager em) {
    this.em = em;
  }

  /**
   * email로 UserStatusType 조회
   * @param email
   * @return
   */
  public UserStatus findStatusByEmail(String email) {
    return
        em.createQuery("""
                select u.userStatus from UserEntity u where u.email = :email
                """, UserStatus.class)
            .setParameter("email", email)
            .getSingleResult();
  }
}
