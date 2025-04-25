package com.lelarn.dreamshops.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.lelarn.dreamshops.model.Product;
import com.lelarn.dreamshops.model.Image;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder; // Import

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
  private Long id;
  private String name;
  private String brand;
  private BigDecimal price;
  private int inventory;
  private String description;
  private String category;
  private List<String> imageUrls;

  public ProductResponse(Product product) {
    this.id = product.getId();
    this.name = product.getName();
    this.brand = product.getBrand();
    this.price = product.getPrice();
    this.inventory = product.getInventory();
    this.description = product.getDescription();
    this.category = product.getCategory() != null ? product.getCategory().getName() : null;
    // Map image IDs to download URLs
    if (product.getImages() != null) {
      this.imageUrls = product.getImages().stream()
          .map(Image::getId)
          .map(imageId -> ServletUriComponentsBuilder.fromCurrentContextPath()
              .path("/api/images/")
              .path(String.valueOf(imageId))
              .toUriString())
          .collect(Collectors.toList());
    }
  }
}
