package com.lelarn.dreamshops.service.category;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lelarn.dreamshops.exceptions.CategoryNotFoundException;
import com.lelarn.dreamshops.model.Category;
import com.lelarn.dreamshops.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

  private final CategoryRepository categoryRepository;

  @Override
  public List<Category> getAllCategories() {
    return categoryRepository.findAll();
  }

  @Override
  public Category getCategoryById(Long id) {
    return categoryRepository.findById(id)
        .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
  }

  @Override
  public Category createCategory(String name) {
    Category existingCategory = categoryRepository.findByName(name);
    if (existingCategory != null) {
      return existingCategory;
    }

    Category category = new Category(name);
    return categoryRepository.save(category);
  }

  @Override
  public Category updateCategory(Long id, String name) {
    Category category = getCategoryById(id);
    category.setName(name);
    return categoryRepository.save(category);
  }

  @Override
  public void deleteCategory(Long id) {
    Category category = getCategoryById(id);
    categoryRepository.delete(category);
  }

  @Override
  public Category getCategoryByName(String name) {
    Category category = categoryRepository.findByName(name);
    if (category == null) {
      throw new CategoryNotFoundException("Category not found with name: " + name);
    }
    return category;
  }
}
