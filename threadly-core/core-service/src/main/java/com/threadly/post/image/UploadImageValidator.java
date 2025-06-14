package com.threadly.post.image;

import com.threadly.ErrorCode;
import com.threadly.exception.post.PostImageException;
import com.threadly.file.UploadImage;
import com.threadly.properties.UploadProperties;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

/**
 * 업로드 이미지 검증
 */
@Component
public class UploadImageValidator {

  private final UploadProperties uploadProperties;

  private final Tika tika = new Tika();

  public UploadImageValidator(UploadProperties uploadProperties) {
    this.uploadProperties = uploadProperties;
  }

  /**
   * 검증
   *
   * @param files
   */
  public void validate(List<UploadImage> files) {
    validateImageCount(files);
    validateImageSize(files);
    Map<UploadImage, String> mimeCache = new HashMap<>();
    for (UploadImage file : files) {
      mimeCache.put(file, getMimeType(file));
    }
    validateMime(mimeCache);
    validateExtensionAndMimeMatch(mimeCache);
  }

  /**
   * 이미지 용량 검증
   *
   * @param files
   */
  private void validateImageSize(List<UploadImage> files) {
    for (UploadImage file : files) {
      if (file.getSize() > uploadProperties.getMaxSize().toBytes()) {
        throw new PostImageException(ErrorCode.POST_IMAGE_TOO_LARGE);
      }
    }
  }

  /**
   * 업로드 파일 수 검증
   *
   * @param files
   */
  private void validateImageCount(List<UploadImage> files) {
    /*이미지 수 초과 시*/
    if (files.size() > uploadProperties.getMaxImageCount()) {
      throw new PostImageException(ErrorCode.POST_IMAGE_UPLOAD_LIMIT_EXCEEDED);
    }

    /*파일이 없을 경우*/
    if (files.isEmpty()) {
      throw new PostImageException(ErrorCode.POST_IMAGE_EMPTY);
    }
  }

  /**
   * 이미지 파일 mime 검증
   *
   * @param mimeCache
   */
  private void validateMime(Map<UploadImage, String> mimeCache) {
    Map<String, String> mimeToExt = uploadProperties.getMimeToExtensions();
    mimeCache.forEach((key, value) -> {
      if (!mimeToExt.containsKey(value)) {
        throw new PostImageException(ErrorCode.POST_IMAGE_INVALID_MIME_TYPE);
      }
    });
  }

  /**
   * 파일 확장자 검증
   *
   * @param mimeCache
   */
  private void validateExtensionAndMimeMatch(Map<UploadImage, String> mimeCache) {
    mimeCache.forEach((key, value) -> {
      String extension = extractExtension(key.getOriginalFileName());

      /*허용되는 확장자인지 검증*/
      if (!uploadProperties.getAllowExtensions().contains(extension)) {
        throw new PostImageException(ErrorCode.POST_IMAGE_INVALID_EXTENSION);
      }

      /*extension과 mime이 일치하는지 검증*/
      if (!isExtensionMatchingMime(extension, value)) {
        throw new PostImageException(ErrorCode.POST_IMAGE_INVALID_IMAGE);
      }
    });
  }

  /**
   * fileName에서 확장자 추출
   *
   * @param fileName
   * @return
   */
  private static String extractExtension(String fileName) {
    int lastDot = fileName.lastIndexOf('.');
    if (lastDot == -1 || lastDot == fileName.length() - 1) {
      throw new PostImageException(ErrorCode.POST_IMAGE_INVALID_EXTENSION);
    }
    return fileName.substring(lastDot + 1).toLowerCase();
  }

  /**
   * extension과 mime이 일치하는지 검증
   *
   * @param extension
   * @param mime
   * @return
   */
  private boolean isExtensionMatchingMime(String extension, String mime) {
    return uploadProperties.getAllowTypes().getOrDefault(extension, "").equalsIgnoreCase(mime);
  }

  /**
   * mime 추출
   *
   * @param file
   * @return
   */
  private String getMimeType(UploadImage file) {
    try {
      return tika.detect(file.getInputStream());
    } catch (IOException e) {
      throw new PostImageException(ErrorCode.POST_IMAGE_INVALID_IMAGE);
    }
  }
}
