package com.threadly.post.controller;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.core.usecase.post.image.UploadPostImageCommand;
import com.threadly.core.usecase.post.image.UploadPostImageUseCase;
import com.threadly.core.usecase.post.image.UploadPostImagesApiResponse;
import com.threadly.post.mapper.ImageMapper;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * 게시글 이미지 업로드 컨트롤러
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/post-images")
public class PostImageUploadController {

  private final UploadPostImageUseCase uploadPostImageUseCase;


  /**
   * 게시글 이미지 업로드
   *
   * @param user
   * @param files
   * @return
   */
  @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UploadPostImagesApiResponse> uploadImage(
      @AuthenticationPrincipal JwtAuthenticationUser user,
//      @RequestParam(value = "postId", required = false) String postId,
      @RequestParam(value = "images", required = false) List<MultipartFile> images
  ) {
    /*업로드 이미지가 null인 경우*/
    if (images == null) {
      images = Collections.emptyList();
    }

    return ResponseEntity.status(201).body(
        uploadPostImageUseCase.uploadPostImages(
            new UploadPostImageCommand(
                user.getUserId(),
                images.stream().map(
                ImageMapper::toUploadImage
            ).toList()
            )));
  }

}
