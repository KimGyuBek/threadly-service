package com.threadly.repository;

import com.threadly.user.UserStatusType;
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
  public UserStatusType findStatusByEmail(String email) {
    return
        em.createQuery("""
                select u.userStatusType from UserEntity u where u.email = :email
                """, UserStatusType.class)
            .setParameter("email", email)
            .getSingleResult();
  }
}
