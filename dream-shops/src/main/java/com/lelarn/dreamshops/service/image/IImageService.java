package com.lelarn.dreamshops.service.image;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.lelarn.dreamshops.model.Image;

public interface IImageService {
  Image saveImage(MultipartFile file, Long productId) throws IOException;

  Image getImageById(Long id);

  List<Image> getImagesByProductId(Long productId);

  void deleteImage(Long id);

  byte[] getImageData(Image image) throws IOException;

  void updateImage(Image image);
}
