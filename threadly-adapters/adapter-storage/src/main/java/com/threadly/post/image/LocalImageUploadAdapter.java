package com.threadly.post.image;


import com.threadly.file.UploadImage;
import com.threadly.image.UploadImagePort;
import com.threadly.image.UploadImageResponse;
import com.threadly.properties.UploadProperties;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 이미지 업로드 adapter
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LocalImageUploadAdapter implements UploadImagePort {

  private final UploadProperties uploadProperties;

  @Override
  public List<UploadImageResponse> uploadPostImageList(List<UploadImage> uploadImages) {
    List<UploadImageResponse> uploadImageResponses = new ArrayList<>();

    for (int i = 0; i < uploadImages.size(); i++) {
      uploadImageResponses.add(storeImage(uploadImages.get(i),
          uploadProperties.getLocation().getPostImage()));
    }
    return uploadImageResponses;
  }

  @Override
  public UploadImageResponse uploadProfileImage(UploadImage uploadImage) {
    return
        storeImage(uploadImage, uploadProperties.getLocation().getProfileImage());
  }

  /**
   * 이미지 저장
   *
   * @param uploadImage
   * @param storePath
   * @return
   */
  private UploadImageResponse storeImage(UploadImage uploadImage, String storePath) {
    String originalFileName = uploadImage.getOriginalFileName();
    String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
    String storedFileName = uploadImage.getStoredFileName() + extension;
    Path fullPath = Paths.get(storePath, storedFileName);

    log.debug("fullPath {}", fullPath);

    try {

      Files.createDirectories(fullPath.getParent());
      Files.write(fullPath, uploadImage.getContent());
      return new UploadImageResponse(storedFileName,
          uploadProperties.getAccessUrl() + storedFileName);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

}
