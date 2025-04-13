package com.lelarn.dreamshops.response;

import com.lelarn.dreamshops.model.Category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
  private Long id;
  private String name;

  public CategoryResponse(Category category) {
    this.id = category.getId();
    this.name = category.getName();
  }
}
