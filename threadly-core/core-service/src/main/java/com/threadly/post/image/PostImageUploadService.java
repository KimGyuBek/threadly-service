package com.threadly.post.image;

import com.threadly.exception.ErrorCode;
import com.threadly.exception.post.PostImageException;
import com.threadly.post.PostImage;
import com.threadly.post.image.UploadPostImagesApiResponse.PostImageResponse;
import com.threadly.post.image.save.SavePostImagePort;
import com.threadly.image.UploadImageResponse;
import com.threadly.image.UploadImagePort;
import com.threadly.post.image.validator.ImageAspectRatioValidator;
import com.threadly.post.image.validator.ImageUploadValidator;
import com.threadly.properties.UploadProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 게시글 이미지 업로드 service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PostImageUploadService implements UploadPostImageUseCase {

  private final SavePostImagePort savePostImagePort;
  private final UploadImagePort uploadImagePort;

  private final UploadProperties uploadProperties;

  private final ImageUploadValidator imageUploadValidator;

  private final ImageAspectRatioValidator imageAspectRatioValidator;

  @Override
  public UploadPostImagesApiResponse uploadPostImages(UploadPostImageCommand command) {
    /* 1. 이미지 파일 검증*/
    imageUploadValidator.validate(command.getImages());

    /*2. 이미지 비율 검증*/
//    imageAspectRatioValidator.validate(command.getImages());

    /* 3. 업로드 이미지 수 검증*/
    if (command.getImages().isEmpty()
        || command.getImages().size() > uploadProperties.getMaxImageCount()) {
      throw new PostImageException(ErrorCode.POST_IMAGE_UPLOAD_LIMIT_EXCEEDED);
    }

    /*4. 이미지 파일 저장*/
    List<UploadImageResponse> uploadImageResponses = uploadImagePort.uploadPostImageList(
        command.getImages());
    log.info("이미지 업로드 완료: {}", uploadImageResponses.toString());

    /*5. 이미지 메타 데이터 db 저장*/
    List<PostImage> postImages = new ArrayList<>();
    uploadImageResponses.forEach(response -> {
      PostImage postImage = PostImage.newPostImage(
          response.getStoredName(),
          response.getImageUrl()
      );
      postImages.add(postImage);

      savePostImagePort.savePostImage(postImage);
      log.debug("이미지 메타 데이터 저장 완료: {}", postImage.toString());
    });

    return new UploadPostImagesApiResponse(
        postImages.stream().map(
            domain -> new PostImageResponse(
                domain.getPostImageId(),
                domain.getImageUrl()
            )
        ).toList()
    );
  }
}
