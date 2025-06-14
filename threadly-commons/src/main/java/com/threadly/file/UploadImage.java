package com.threadly.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UploadImage {

  private String originalFileName;
  private String storedFileName;
  private byte[] content;
  private String contentType;
  private long size;


  public UploadImage(String originalFileName, byte[] content, String contentType, long size) {
    this.originalFileName = originalFileName;
    this.content = content;
    this.contentType = contentType;
    this.size = size;
  }

  /**
   * storedFileName을 포함한 새로운 객체 생성
   *
   * @param storedFileName
   * @return
   */
  public UploadImage withStoredFileName(String storedFileName) {
    return new UploadImage(this.originalFileName, storedFileName, this.content, this.contentType,
        this.size);
  }
}
