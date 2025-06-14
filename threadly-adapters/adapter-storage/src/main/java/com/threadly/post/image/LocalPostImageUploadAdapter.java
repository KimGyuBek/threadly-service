package com.threadly.post.image;


import com.threadly.file.UploadImage;
import com.threadly.post.image.upload.UploadImageResponse;
import com.threadly.post.image.upload.UploadPostImagePort;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 로컬
 * <p>
 * 게시글 이미지 업로드 adapter
 */
@Component
@Slf4j
public class LocalPostImageUploadAdapter implements UploadPostImagePort {

  @Value("${properties.file.upload.location}")
  private String uploadDir;

  @Value("${properties.file.upload.access-url}")
  private String accessUrlPrefix;

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
    Path fullPath = Paths.get(uploadDir, storedFileName);

    log.debug("fullPath {}", fullPath);

    try {
      Files.write(fullPath, uploadImage.getContent());

      return new UploadImageResponse(storedFileName, accessUrlPrefix + storedFileName,
          imageOrder);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }

  }

}
