package com.threadly.repository.user;

import com.threadly.entity.user.UserProfileImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * UserProfileImage Jpa Repository
 */
public interface UserProfileImageJpaRepository extends
    JpaRepository<UserProfileImageEntity, String> {

}
