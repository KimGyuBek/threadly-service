package com.threadly.core.port.post.in.image;

import com.threadly.commons.file.UploadImage;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시글 이미지 업로드 요청 command 객체
 */
@Getter
@AllArgsConstructor
public class UploadPostImageCommand {

  private String userId;
  private List<UploadImage> images;



}
