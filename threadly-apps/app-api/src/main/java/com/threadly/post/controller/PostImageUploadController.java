package com.threadly.post.controller;

import com.threadly.auth.AuthenticationUser;
import com.threadly.post.image.UploadPostImageCommand;
import com.threadly.post.image.UploadPostImageUseCase;
import com.threadly.post.image.UploadPostImagesApiResponse;
import com.threadly.post.mapper.ImageMapper;
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
      @AuthenticationPrincipal AuthenticationUser user,
      @RequestParam("postId") String postId,
      @RequestParam("images") List<MultipartFile> files

  ) {

    return ResponseEntity.status(201).body(
        uploadPostImageUseCase.uploadPostImages(
            new UploadPostImageCommand(
                user.getUserId(), postId, files.stream().map(
                ImageMapper::toUploadImage
            ).toList()
            )
        )
    );
  }

}
