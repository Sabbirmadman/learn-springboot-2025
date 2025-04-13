package com.lelarn.dreamshops.service.image;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lelarn.dreamshops.exceptions.ImageNotFoundException;
import com.lelarn.dreamshops.model.Image;
import com.lelarn.dreamshops.model.Product;
import com.lelarn.dreamshops.repository.ImageRepository;
import com.lelarn.dreamshops.service.product.IProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {

  private final ImageRepository imageRepository;
  private final IProductService productService;

  @Override
  public Image saveImage(MultipartFile file, Long productId) throws IOException {
    Product product = productService.getProductById(productId);

    Image image = new Image();
    image.setFileName(file.getOriginalFilename());
    image.setFileType(file.getContentType());

    try {
      Blob blob = new SerialBlob(file.getBytes());
      image.setImage(blob);
    } catch (SQLException e) {
      throw new IOException("Could not save image data", e);
    }

    image.setProduct(product);
    return imageRepository.save(image);
  }

  @Override
  public Image getImageById(Long id) {
    return imageRepository.findById(id)
        .orElseThrow(() -> new ImageNotFoundException("Image not found with id: " + id));
  }

  @Override
  public List<Image> getImagesByProductId(Long productId) {
    return imageRepository.findByProductId(productId);
  }

  @Override
  public void deleteImage(Long id) {
    Image image = getImageById(id);
    imageRepository.delete(image);
  }

  @Override
  public byte[] getImageData(Image image) throws IOException {
    try {
      Blob blob = image.getImage();
      int blobLength = (int) blob.length();
      return blob.getBytes(1, blobLength);
    } catch (SQLException e) {
      throw new IOException("Could not extract image data", e);
    }
  }

  @Override
  public void updateImage(Image image) {
    imageRepository.save(image);
  }
}
