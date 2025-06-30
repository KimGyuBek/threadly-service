package com.threadly.post.image;


import com.threadly.file.UploadImage;
import com.threadly.post.image.upload.UploadImageResponse;
import com.threadly.post.image.upload.UploadPostImagePort;
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
 * 로컬
 * <p>
 * 게시글 이미지 업로드 adapter
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LocalPostImageUploadAdapter implements UploadPostImagePort {

  private final UploadProperties uploadProperties;

  @Override
  public List<UploadImageResponse> uploadPostImage(List<UploadImage> uploadImages) {
    List<UploadImageResponse> uploadImageResponses = new ArrayList<>();

    for (int i = 0; i < uploadImages.size(); i++) {
      uploadImageResponses.add(storeImage(uploadImages.get(i), i));
    }
    return uploadImageResponses;
  }

  /**
   * 이미지 저장
   *
   * @param uploadImage
   * @return
   */
  private UploadImageResponse storeImage(UploadImage uploadImage, int imageOrder) {
    String originalFileName = uploadImage.getOriginalFileName();
    String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
    String storedFileName = uploadImage.getStoredFileName() + extension;
    Path fullPath = Paths.get(uploadProperties.getLocation(), storedFileName);

    log.debug("fullPath {}", fullPath);

    try {
      Files.write(fullPath, uploadImage.getContent());
      return new UploadImageResponse(storedFileName,
          uploadProperties.getAccessUrl() + storedFileName);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

}
