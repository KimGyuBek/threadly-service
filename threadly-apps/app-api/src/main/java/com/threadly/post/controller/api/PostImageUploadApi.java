package com.threadly.post.controller.api;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.core.port.post.in.image.UploadPostImagesApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Tag(name = "게시글 이미지 API", description = "게시글 이미지 업로드 API")
public interface PostImageUploadApi {

  @Operation(summary = "게시글 이미지 업로드")
  @PostMapping("/images")
  ResponseEntity<UploadPostImagesApiResponse> uploadImage(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestParam("images") List<MultipartFile> images);

}
