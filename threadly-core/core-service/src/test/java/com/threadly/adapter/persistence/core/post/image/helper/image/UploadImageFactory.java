package com.threadly.adapter.persistence.core.post.image.helper.image;

import com.threadly.commons.file.UploadImage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.springframework.mock.web.MockMultipartFile;

/**
 * MockImage 관련 테스트 헬퍼
 */
public class UploadImageFactory {

  /**
   * MockMultipartFile -> UploadImage
   *
   * @param file
   * @return
   */
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

  /**
   * 특정 비율에 맞는 이미지 생성
   *
   * @param imageName
   * @param format
   * @param width
   * @param height
   * @return
   */
  public static UploadImage generateImageWithRatio(String imageName, String format, int width,
      int height) throws IOException {
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    ByteArrayOutputStream boas = new ByteArrayOutputStream();
    ImageIO.write(image, format, boas);

    return
        UploadImageFactory.fromMock(
            new MockMultipartFile(
                "images",
                imageName,
                "image/" + format,
                boas.toByteArray()
            ));
  }


  public void test(UploadImage uploadImage) {
    try (
        InputStream inputStream = uploadImage.getInputStream();
        ){
      BufferedImage image = ImageIO.read(inputStream);
      /*image 비율 조정*/


    } catch (Exception e) {

    }

  }
}
