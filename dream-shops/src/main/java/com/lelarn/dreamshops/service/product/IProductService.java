package com.lelarn.dreamshops.service.product;

import java.util.List;

import com.lelarn.dreamshops.model.Product;
import com.lelarn.dreamshops.request.AddProductRequest;

public interface IProductService {
  Product addProduct(AddProductRequest product);

  Product getProductById(Long id);

  Product updateProduct(Long productId, AddProductRequest request);

  List<Product> getFilteredProducts(
          String category,
          String brand,
          String name
  );

  void deleteProductById(Long id);
}
