package com.lelarn.dreamshops.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.lelarn.dreamshops.response.ApiResponse;
import com.lelarn.dreamshops.response.ImageResponse;
import com.lelarn.dreamshops.service.image.IImageService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

  private final IImageService imageService;

  @PostMapping("/upload/{productId}")
  @PreAuthorize("hasAuthority('ADMIN')") // Assuming only admins can upload product images
  public ResponseEntity<ApiResponse<ImageResponse>> uploadImage(
      @RequestParam("image") MultipartFile file,
      @PathVariable Long productId) {
    try {
      Image savedImage = imageService.saveImage(file, productId);

      String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
          .path("/api/images/")
          .path(savedImage.getId().toString())
          .toUriString();

      savedImage.setDownloadURL(downloadUrl);
      imageService.updateImage(savedImage); // Persist the download URL

      return ApiResponse.success(HttpStatus.CREATED, "Image uploaded successfully", new ImageResponse(savedImage));

    } catch (EntityNotFoundException e) {
      return ApiResponse.error(HttpStatus.NOT_FOUND, "Product not found for image upload", e.getMessage());
    } catch (IOException e) {
      return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to process image file", e.getMessage());
    } catch (Exception e) {
      return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload image", e.getMessage());
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getImage(@PathVariable Long id) {
    try {
      Image image = imageService.getImageById(id);
      byte[] imageData = imageService.getImageData(image);

      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(image.getFileType()))
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
          .body(imageData);
    } catch (EntityNotFoundException e) {
      // Return ApiResponse for errors
      return ApiResponse.error(HttpStatus.NOT_FOUND, "Image not found", e.getMessage());
    } catch (IOException e) {
      // Return ApiResponse for errors
      return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to read image data", e.getMessage());
    } catch (Exception e) {
      // Return ApiResponse for errors
      return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve image", e.getMessage());
    }
  }

  @GetMapping("/product/{productId}")
  public ResponseEntity<ApiResponse<List<ImageResponse>>> getProductImages(@PathVariable Long productId) {
    try {
      List<Image> images = imageService.getImagesByProductId(productId);
      List<ImageResponse> response = images.stream()
          .map(ImageResponse::new)
          .toList();
      if (response.isEmpty()) {
        return ApiResponse.success(HttpStatus.OK, "No images found for this product", response);
      }
      return ApiResponse.success("Product images retrieved successfully", response);
    } catch (EntityNotFoundException e) {
      return ApiResponse.error(HttpStatus.NOT_FOUND, "Product not found when retrieving images", e.getMessage());
    } catch (Exception e) {
      return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve product images", e.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMIN')") // Assuming only admins can delete images
  public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable Long id) {
    try {
      imageService.deleteImage(id);
      return ApiResponse.success(HttpStatus.NO_CONTENT, "Image deleted successfully", null);
    } catch (EntityNotFoundException e) {
      return ApiResponse.error(HttpStatus.NOT_FOUND, "Image not found for deletion", e.getMessage());
    } catch (Exception e) {
      return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete image", e.getMessage());
    }
  }
}
