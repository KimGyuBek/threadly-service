package com.threadly.helper;

import com.threadly.file.UploadImage;
import java.io.IOException;
import org.springframework.mock.web.MockMultipartFile;

public class UploadImageTestFactory {

  public static UploadImage fromMock(MockMultipartFile file) {
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
      throw new RuntimeException("파일 변환 실패 ", e);
    }
  }
}
