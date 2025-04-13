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
import org.springframework.web.bind.annotation.RestController;

import com.lelarn.dreamshops.model.Category;
import com.lelarn.dreamshops.request.CategoryRequest;
import com.lelarn.dreamshops.response.CategoryResponse;
import com.lelarn.dreamshops.service.category.ICategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final ICategoryService categoryService;

  @GetMapping
  public ResponseEntity<List<CategoryResponse>> getAllCategories() {
    List<Category> categories = categoryService.getAllCategories();
    List<CategoryResponse> response = categories.stream()
        .map(CategoryResponse::new)
        .toList();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
    Category category = categoryService.getCategoryById(id);
    return ResponseEntity.ok(new CategoryResponse(category));
  }

  @PostMapping
  public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest request) {
    Category category = categoryService.createCategory(request.getName());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new CategoryResponse(category));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryResponse> updateCategory(
      @PathVariable Long id,
      @RequestBody CategoryRequest request) {
    Category category = categoryService.updateCategory(id, request.getName());
    return ResponseEntity.ok(new CategoryResponse(category));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.noContent().build();
  }
}
