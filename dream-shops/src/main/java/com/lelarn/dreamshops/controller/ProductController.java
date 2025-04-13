package com.lelarn.dreamshops.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lelarn.dreamshops.model.Product;
import com.lelarn.dreamshops.request.AddProductRequest;
import com.lelarn.dreamshops.response.ProductResponse;
import com.lelarn.dreamshops.service.product.IProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final IProductService productService;

  @PostMapping
  public ResponseEntity<ProductResponse> addProduct(@RequestBody AddProductRequest request) {
    Product savedProduct = productService.addProduct(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new ProductResponse(savedProduct.getId(), savedProduct.getName(),
            savedProduct.getBrand(), savedProduct.getPrice(), savedProduct.getInventory(),
            savedProduct.getDescription(),
            savedProduct.getCategory() != null ? savedProduct.getCategory().getName() : null));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
    Product product = productService.getProductById(id);
    return ResponseEntity.ok(new ProductResponse(product));
  }

  @GetMapping
  public ResponseEntity<List<ProductResponse>> getAllProducts(
      @RequestParam(required = false) String category,
      @RequestParam(required = false) String brand,
      @RequestParam(required = false) String name) {

    List<Product> products;

    if (category != null && brand != null) {
      products = productService.getProductsByCategoryAndBrand(category, brand);
    } else if (brand != null && name != null) {
      products = productService.getProductsByBrandAndName(brand, name);
    } else if (category != null) {
      products = productService.getProductsByCategory(category);
    } else if (brand != null) {
      products = productService.getProductsByBrand(brand);
    } else if (name != null) {
      products = productService.getProductsByName(name);
    } else {
      products = productService.getAllProducts();
    }

    List<ProductResponse> response = products.stream()
        .map(ProductResponse::new)
        .toList();

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductResponse> updateProduct(
      @PathVariable Long id,
      @RequestBody Product updatedProduct) {
    productService.updateProduct(updatedProduct, id);
    Product product = productService.getProductById(id);
    return ResponseEntity.ok(new ProductResponse(product));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.deleteProductById(id);
    return ResponseEntity.noContent().build();
  }
}
