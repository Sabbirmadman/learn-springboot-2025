package com.lelarn.dreamshops.service.category;

import java.util.List;

import com.lelarn.dreamshops.model.Category;

public interface ICategoryService {
  List<Category> getAllCategories();

  Category getCategoryById(Long id);

  Category createCategory(String name);

  Category updateCategory(Long id, String name);

  void deleteCategory(Long id);

  Category getCategoryByName(String name);
}
