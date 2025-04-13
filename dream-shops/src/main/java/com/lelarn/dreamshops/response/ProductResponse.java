package com.lelarn.dreamshops.response;

import java.math.BigDecimal;

import com.lelarn.dreamshops.model.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

  public ProductResponse(Product product) {
    this.id = product.getId();
    this.name = product.getName();
    this.brand = product.getBrand();
    this.price = product.getPrice();
    this.inventory = product.getInventory();
    this.description = product.getDescription();
    this.category = product.getCategory() != null ? product.getCategory().getName() : null;
  }
}
