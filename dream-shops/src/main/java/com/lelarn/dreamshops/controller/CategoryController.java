package com.lelarn.dreamshops.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.lelarn.dreamshops.model.Category;
import com.lelarn.dreamshops.request.CategoryRequest;
import com.lelarn.dreamshops.response.CategoryResponse;
import com.lelarn.dreamshops.service.category.ICategoryService;

import com.lelarn.dreamshops.response.ApiResponse;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final ICategoryService categoryService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
    try {
      List<Category> categories = categoryService.getAllCategories();
      List<CategoryResponse> response = categories.stream()
          .map(CategoryResponse::new)
          .toList();
      if (response.isEmpty()) {
        return ApiResponse.success(HttpStatus.OK, "No categories found", response);
      }
      return ApiResponse.success("Categories retrieved successfully", response); // Changed message

    } catch (Exception e) {
      return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve categories", e.getMessage());
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
    try {
      Category category = categoryService.getCategoryById(id);
      return ApiResponse.success("Category found", new CategoryResponse(category));
    } catch (EntityNotFoundException e) {
      return ApiResponse.error(HttpStatus.NOT_FOUND, "Category not found", e.getMessage());
    } catch (Exception e) {
      return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve category", e.getMessage());
    }
  }

  @PostMapping
  @PreAuthorize("hasAuthority('ADMIN')") // Assuming only admins can create categories
  public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@RequestBody CategoryRequest request) {
    try {
      Category category = categoryService.createCategory(request.getName());
      return ApiResponse.success(HttpStatus.CREATED, "Category created successfully", new CategoryResponse(category));
    } catch (Exception e) { // Consider adding more specific exceptions like DataIntegrityViolationException
                            // for unique constraints
      return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create category", e.getMessage());
    }
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMIN')") // Assuming only admins can update categories
  public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
      @PathVariable Long id,
      @RequestBody CategoryRequest request) {
    try {
      Category category = categoryService.updateCategory(id, request.getName());
      return ApiResponse.success("Category updated successfully", new CategoryResponse(category));
    } catch (EntityNotFoundException e) {
      return ApiResponse.error(HttpStatus.NOT_FOUND, "Category not found for update", e.getMessage());
    } catch (Exception e) {
      return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update category", e.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMIN')") // Assuming only admins can delete categories
  public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
    try {
      categoryService.deleteCategory(id);
      return ApiResponse.success(HttpStatus.NO_CONTENT, "Category deleted successfully", null);
    } catch (EntityNotFoundException e) {
      return ApiResponse.error(HttpStatus.NOT_FOUND, "Category not found for deletion", e.getMessage());
    } catch (Exception e) { // Consider DataIntegrityViolationException if category is in use
      return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete category", e.getMessage());
    }
  }
}
