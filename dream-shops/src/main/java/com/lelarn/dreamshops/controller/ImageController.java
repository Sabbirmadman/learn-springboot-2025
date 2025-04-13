package com.lelarn.dreamshops.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.lelarn.dreamshops.model.Image;
import com.lelarn.dreamshops.response.ImageResponse;
import com.lelarn.dreamshops.service.image.IImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

  private final IImageService imageService;

  @PostMapping("/upload/{productId}")
  public ResponseEntity<ImageResponse> uploadImage(
      @RequestParam("image") MultipartFile file,
      @PathVariable Long productId) throws IOException {

    Image savedImage = imageService.saveImage(file, productId);

    String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
        .path("/api/images/")
        .path(savedImage.getId().toString())
        .toUriString();

    savedImage.setDownloadURL(downloadUrl);
    imageService.updateImage(savedImage);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new ImageResponse(savedImage));
  }

  @GetMapping("/{id}")
  public ResponseEntity<byte[]> getImage(@PathVariable Long id) throws IOException {
    Image image = imageService.getImageById(id);

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(image.getFileType()))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
        .body(imageService.getImageData(image));
  }

  @GetMapping("/product/{productId}")
  public ResponseEntity<List<ImageResponse>> getProductImages(@PathVariable Long productId) {
    List<Image> images = imageService.getImagesByProductId(productId);
    List<ImageResponse> response = images.stream()
        .map(ImageResponse::new)
        .toList();

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
    imageService.deleteImage(id);
    return ResponseEntity.noContent().build();
  }
}
