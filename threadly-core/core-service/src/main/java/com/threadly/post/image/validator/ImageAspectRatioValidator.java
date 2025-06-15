package com.threadly.post.image.validator;

import com.threadly.file.AspectRatio;
import com.threadly.file.UploadImage;
import com.threadly.properties.UploadProperties;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;

/**
 * 업로드 이미지 비율 검증
 */
@Component
public class ImageAspectRatioValidator {

  private final UploadProperties uploadProperties;


  public ImageAspectRatioValidator(UploadProperties uploadProperties) {
    this.uploadProperties = uploadProperties;
  }


  /**
   * 검증
   * @param images
   * @return
   */
  public boolean isValid(List<UploadImage> images) {
    for (UploadImage image : images) {
      if (!isValidAspectRatio(image)) {
        return false;
      }
    }
    return true;
  }

  /**
   * 이미지 비율 검증
   *
   * @param uploadImage
   * @return
   */
  private boolean isValidAspectRatio(UploadImage uploadImage) {

    AspectRatio target = uploadProperties.getAspectRatio().getTarget();
    double tolerance = uploadProperties.getAspectRatio().getTolerance();

    try (InputStream inputStream = uploadImage.getInputStream()) {
      BufferedImage image = ImageIO.read(inputStream);

      /*이미지가 아닌 경우*/
      if (image == null) {
        return false;
      }

      /*비율 구하기*/
      int width = image.getWidth();
      int height = image.getHeight();
      double actualRatio = (double) width / height;

      /*비율과 허용 오차를 비교해서 리턴*/
      return
          target.isValid(actualRatio, tolerance);
    } catch (Exception e) {
      return false;
    }

  }

}
