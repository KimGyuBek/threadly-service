package com.threadly.post.image;

import com.threadly.ErrorCode;
import com.threadly.exception.post.PostException;
import com.threadly.post.PostImage;
import com.threadly.post.fetch.FetchPostPort;
import com.threadly.post.image.UploadPostImagesApiResponse.PostImageResponse;
import com.threadly.post.image.save.SavePostImagePort;
import com.threadly.post.image.upload.UploadImageResponse;
import com.threadly.post.image.upload.UploadPostImagePort;
import com.threadly.util.RandomUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 게시글 이미지 관련 command 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PostImageCommandService implements UploadPostImageUseCase {

  private final FetchPostPort fetchPostPort;

  private final SavePostImagePort savePostImagePort;
  private final UploadPostImagePort uploadPostImagePort;

  @Override
  public UploadPostImagesApiResponse uploadPostImages(UploadPostImageCommand command) {
    /*1. 게시글 존재 및 게시글 작성자 검증*/
    String writerId = fetchPostPort.fetchUserIdByPostId(command.getPostId()).orElseThrow(
        () -> new PostException(ErrorCode.POST_NOT_FOUND)
    );

    /*게시글 작성자 id와 요청 userId가 일치하지 않는 경우*/
    if (!writerId.equals(command.getUserId())) {
      throw new PostException(ErrorCode.POST_IMAGE_UPLOAD_FORBIDDEN);
    }

    /*2. 이미지 파일 저장*/
    List<UploadImageResponse> uploadImageResponses = uploadPostImagePort.uploadPostImage(
        command.getImages().stream().map(
            uploadImage -> uploadImage.withStoredFileName(RandomUtils.generateNanoId())
        ).toList()
    );
    log.info("이미지 업로드 완료: {}", uploadImageResponses.toString());

    /*3. 이미지 메타 데이터 db 저장*/
    uploadImageResponses.forEach(response -> {
      PostImage postImage = PostImage.newPostImage(
          command.getPostId(),
          response.getStoredName(),
          response.getImageUrl(),
          response.getImageOrder()
      );

      savePostImagePort.savePostImage(postImage);
      log.debug("이미지 메타 데이터 저장 완료: {}", postImage.toString());
    });

    return new UploadPostImagesApiResponse(
        uploadImageResponses.stream().map(
            response -> new PostImageResponse(
                response.getImageUrl(),
                response.getImageOrder())).toList()
    );
  }
}
