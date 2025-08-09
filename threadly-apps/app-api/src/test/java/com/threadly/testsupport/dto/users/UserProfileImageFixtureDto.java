package com.threadly.testsupport.dto.users;

import com.threadly.core.domain.image.ImageStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserprofileImage Fixture Dto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileImageFixtureDto {

  private String userProfileImageId;
  private String userId;
  private String storedFileName;
  private String imageUrl;
  private ImageStatus status;

}
