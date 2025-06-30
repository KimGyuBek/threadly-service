package com.threadly.repository.user;

import com.threadly.entity.user.UserProfileEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * UserProfile Jpa Repository
 */
public interface UserProfileJpaRepository extends JpaRepository<UserProfileEntity, String> {


}
