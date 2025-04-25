package com.lelarn.dreamshops.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

  // GET /api/products - Get filtered and paginated products
  @GetMapping
  public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProducts(
      @RequestParam(required = false) String category,
      @RequestParam(required = false) String brand,
      @RequestParam(required = false) String name,
      @PageableDefault(size = 10, sort = "id") Pageable pageable) {
    try {
      Page<Product> productPage = productService.getFilteredProducts(category, brand, name, pageable);
      Page<ProductResponse> responsePage = productPage.map(ProductResponse::new);
      return ApiResponse.success("Products retrieved successfully", responsePage);
    } catch (Exception e) {
      return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve products", e.getMessage());
    }
  }

  // GET /api/products/{id} - Get product by ID
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
    try {
      Product product = productService.getProductById(id);
      return ApiResponse.success("Product found", new ProductResponse(product));
    } catch (EntityNotFoundException e) {
      return ApiResponse.error(HttpStatus.NOT_FOUND, "Product not found", e.getMessage());
    } catch (Exception e) {
      return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve product", e.getMessage());
    }
  }

  // POST /api/products - Add a new product (Admin only)
  @PostMapping
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<ApiResponse<ProductResponse>> addProduct(@RequestBody AddProductRequest request) {
    try {
      Product newProduct = productService.addProduct(request);
      return ApiResponse.success(HttpStatus.CREATED, "Product added successfully", new ProductResponse(newProduct));
    } catch (Exception e) {
      return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add product", e.getMessage());
    }
  }

  // PUT /api/products/{id} - Update an existing product (Admin only)
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@PathVariable Long id,
      @RequestBody AddProductRequest request) {
    try {
      Product updatedProduct = productService.updateProduct(id, request);
      return ApiResponse.success("Product updated successfully", new ProductResponse(updatedProduct));
    } catch (EntityNotFoundException e) {
      return ApiResponse.error(HttpStatus.NOT_FOUND, "Product not found for update", e.getMessage());
    } catch (Exception e) {
      return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update product", e.getMessage());
    }
  }

  // DELETE /api/products/{id} - Delete a product (Admin only)
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
    try {
      productService.deleteProductById(id);
      return ApiResponse.success(HttpStatus.NO_CONTENT, "Product deleted successfully", null);
    } catch (EntityNotFoundException e) {
      return ApiResponse.error(HttpStatus.NOT_FOUND, "Product not found for deletion", e.getMessage());
    } catch (Exception e) {
      return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete product", e.getMessage());
    }
  }
}