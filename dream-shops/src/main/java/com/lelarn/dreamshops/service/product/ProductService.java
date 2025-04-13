package com.lelarn.dreamshops.service.product;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lelarn.dreamshops.exceptions.ProductNotFoundException;
import com.lelarn.dreamshops.model.Category;
import com.lelarn.dreamshops.model.Product;
import com.lelarn.dreamshops.repository.ProductRepository;
import com.lelarn.dreamshops.repository.CategoryRepository;
import com.lelarn.dreamshops.request.AddProductRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  @Override
  public Product addProduct(AddProductRequest request) {
    Category category = null;
    if (request.getCategory() != null) {
      category = categoryRepository.findByName(request.getCategory().getName());
      if (category == null) {
        category = new Category(request.getCategory().getName());
        category = categoryRepository.save(category);
      }
    }

    return productRepository.save(createProduct(request, category));
  }

  private Product createProduct(AddProductRequest product, Category category) {
    return new Product(
        product.getName(),
        product.getBrand(),
        product.getPrice(),
        product.getInventory(),
        product.getDescription(),
        category);
  }

  @Override
  public Product getProductById(Long id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
  }

  @Override
  public void deleteProductById(Long id) {
    Product product = getProductById(id);
    productRepository.delete(product);
  }

  @Override
  public void updateProduct(Product updatedProduct, Long productId) {
    Product existingProduct = getProductById(productId);

    // Update product fields
    existingProduct.setName(updatedProduct.getName());
    existingProduct.setBrand(updatedProduct.getBrand());
    existingProduct.setPrice(updatedProduct.getPrice());
    existingProduct.setInventory(updatedProduct.getInventory());
    existingProduct.setDescription(updatedProduct.getDescription());

    // Update category if provided
    if (updatedProduct.getCategory() != null) {
      Category category = categoryRepository.findByName(updatedProduct.getCategory().getName());
      if (category == null) {
        category = new Category(updatedProduct.getCategory().getName());
        category = categoryRepository.save(category);
      }
      existingProduct.setCategory(category);
    }

    productRepository.save(existingProduct);
  }

  @Override
  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  @Override
  public List<Product> getProductsByCategory(String category) {
    return productRepository.findByCategoryName(category);
  }

  @Override
  public List<Product> getProductsByBrand(String brand) {
    return productRepository.findByBrand(brand);
  }

  @Override
  public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
    return productRepository.findByCategoryNameAndBrand(category, brand);
  }

  @Override
  public List<Product> getProductsByName(String name) {
    return productRepository.findByName(name);
  }

  @Override
  public List<Product> getProductsByBrandAndName(String brand, String name) {
    return productRepository.findByBrandAndName(brand, name);
  }

  @Override
  public long countProductsByBrandAndName(String brand, String name) {
    return productRepository.countByBrandAndName(brand, name);
  }
}
