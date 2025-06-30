package com.threadly.post.image;

import com.threadly.file.UploadImage;
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
//  private String postId;
  private List<UploadImage> images;


}
