package com.lelarn.dreamshops.response;

import com.lelarn.dreamshops.model.Image;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {
  private Long id;
  private String fileName;
  private String fileType;
  private String downloadUrl;
  private Long productId;

  public ImageResponse(Image image) {
    this.id = image.getId();
    this.fileName = image.getFileName();
    this.fileType = image.getFileType();
    this.downloadUrl = image.getDownloadURL();
    this.productId = image.getProduct().getId();
  }
}
