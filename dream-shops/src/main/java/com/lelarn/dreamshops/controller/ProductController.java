package com.lelarn.dreamshops.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.lelarn.dreamshops.response.ApiResponse;
import com.lelarn.dreamshops.response.ProductResponse;
import com.lelarn.dreamshops.service.product.IProductService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final IProductService productService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<ProductResponse>>> getProducts(
          @RequestParam(value = "category", required = false) String category,
          @RequestParam(value = "brand", required = false) String brand,
          @RequestParam(value = "name", required = false) String name) {
    try {
      List<Product> products = productService.getFilteredProducts(category, brand, name);
      List<ProductResponse> productResponses = products.stream()
              .map(ProductResponse::new)
              .collect(Collectors.toList());
      if (productResponses.isEmpty()) {
        return ApiResponse.success(HttpStatus.OK, "No products found matching the criteria", productResponses);
      }
      return ApiResponse.success("Products retrieved successfully", productResponses);
    } catch (Exception e) {
      // Log the exception e
      return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve products", e.getMessage());
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable(value = "id") Long id) {
    try {
      Product product = productService.getProductById(id); // Assumes this throws exception if not found
      ProductResponse responseData = new ProductResponse(product);
      return ApiResponse.success("Product retrieved successfully", responseData);
    } catch (EntityNotFoundException e) { // Catch specific exception if service throws it
      return ApiResponse.error(HttpStatus.NOT_FOUND, "Product not found", e.getMessage());
    } catch (Exception e) {
      // Log the exception e
      return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve product", e.getMessage());
    }
  }

  @PostMapping
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<ApiResponse<ProductResponse>> addProduct(@RequestBody AddProductRequest request) {
    try {
      Product newProduct = productService.addProduct(request);
      ProductResponse responseData = new ProductResponse(newProduct);
      return ApiResponse.success(HttpStatus.CREATED, "Product added successfully", responseData);
    } catch (Exception e) { // Catch more specific exceptions if possible (e.g., ValidationException)
      // Log the exception e
      return ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to add product", e.getMessage());
    }
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@PathVariable(value = "id") Long id, @RequestBody AddProductRequest request) {
    try {
      Product updatedProduct = productService.updateProduct(id, request);
      ProductResponse responseData = new ProductResponse(updatedProduct);
      return ApiResponse.success("Product updated successfully", responseData);
    } catch (EntityNotFoundException e) {
      return ApiResponse.error(HttpStatus.NOT_FOUND, "Product not found for update", e.getMessage());
    } catch (Exception e) {
      // Log the exception e
      return ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to update product", e.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<ApiResponse<Object>> deleteProduct(@PathVariable(value = "id") Long id) { // Return type changed
    try {
      productService.deleteProductById(id);
      // Return success with no data, using Object as the generic type
      return ApiResponse.success(HttpStatus.OK, "Product deleted successfully", null);
    } catch (EntityNotFoundException e) { // Or EmptyResultDataAccessException depending on service impl
      return ApiResponse.error(HttpStatus.NOT_FOUND, "Product not found for deletion", e.getMessage());
    } catch (Exception e) {
      // Log the exception e
      return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete product", e.getMessage());
    }
  }

}