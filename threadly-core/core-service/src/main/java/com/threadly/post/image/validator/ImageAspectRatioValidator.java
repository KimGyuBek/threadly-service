package com.threadly.post.image.validator;

import com.threadly.exception.ErrorCode;
import com.threadly.exception.post.PostImageException;
import com.threadly.file.AspectRatio;
import com.threadly.file.UploadImage;
import com.threadly.properties.UploadProperties;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 업로드 이미지 비율 검증
 */
@Component
@Slf4j
public class ImageAspectRatioValidator {

  private final UploadProperties uploadProperties;


  public ImageAspectRatioValidator(UploadProperties uploadProperties) {
    this.uploadProperties = uploadProperties;
  }


  /**
   * 검증
   * @param images
   */
  public void validate(List<UploadImage> images) {
    for (UploadImage image : images) {
      if (!isValidAspectRatio(image)) {
        log.warn("이미지 비율 검증 실패 - 파일명: {}", image.getOriginalFileName());
        throw new PostImageException(ErrorCode.POST_IMAGE_ASPECT_RATIO_INVALID);
      }
      log.debug("이미지 비율 검증 성공 - 파일명: {}", image.getOriginalFileName());
    }
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
      log.error("이미지 비율 검증 오류 {}", e.getMessage());
      return false;
    }

  }

}
