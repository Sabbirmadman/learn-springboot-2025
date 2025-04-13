package com.lelarn.dreamshops.service.product;

import java.util.List;

import com.lelarn.dreamshops.model.Product;
import com.lelarn.dreamshops.request.AddProductRequest;

public interface IProductService {
  Product addProduct(AddProductRequest product);

  Product getProductById(Long id);

  void deleteProductById(Long id);

  void updateProduct(Product product, Long productId);

  List<Product> getAllProducts();

  List<Product> getProductsByCategory(String category);

  List<Product> getProductsByBrand(String brand);

  List<Product> getProductsByCategoryAndBrand(String category, String brand);

  List<Product> getProductsByName(String name);

  List<Product> getProductsByBrandAndName(String brand, String name);

  long countProductsByBrandAndName(String brand, String name);

}
