package com.threadly.post.mapper;

import com.threadly.file.UploadImage;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

/**
 * MultipartFile -> UploadImage 매퍼
 */
/*TODO 공동화*/
public class ImageMapper {

  public static UploadImage toUploadImage(MultipartFile file) {
    try {
      return
          new UploadImage(
              file.getOriginalFilename(),
              file.getBytes(),
              file.getContentType(),
              file.getSize(),
              file.getInputStream()
          );
    } catch (IOException e) {
      throw new RuntimeException("파일 읽기 실패", e);
    }
  }

}
