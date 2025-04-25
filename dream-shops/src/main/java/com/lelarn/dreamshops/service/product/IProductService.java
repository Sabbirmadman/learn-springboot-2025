package com.lelarn.dreamshops.service.product;

import com.lelarn.dreamshops.model.Product;
import com.lelarn.dreamshops.request.AddProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductService {
  Product addProduct(AddProductRequest product);

  Product getProductById(Long id);

  Product updateProduct(Long productId, AddProductRequest request);

  Page<Product> getFilteredProducts(
      String category,
      String brand,
      String name,
      Pageable pageable);

  void deleteProductById(Long id);
}
