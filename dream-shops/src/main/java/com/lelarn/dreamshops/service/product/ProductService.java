package com.lelarn.dreamshops.service.product;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Use the generic ResourceNotFoundException
import com.lelarn.dreamshops.exceptions.ResourceNotFoundException;
import com.lelarn.dreamshops.model.Category;
import com.lelarn.dreamshops.model.Product;
import com.lelarn.dreamshops.repository.ProductRepository;
import com.lelarn.dreamshops.repository.CategoryRepository;
import com.lelarn.dreamshops.repository.ProductSpecification;
import com.lelarn.dreamshops.request.AddProductRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  @Override
  @Transactional
  public Product addProduct(AddProductRequest request) {
    Category category = null;
    if (request.getCategory() != null && request.getCategory().getId() != null) {
      category = categoryRepository.findById(request.getCategory().getId())
          .orElseThrow(
              () -> new ResourceNotFoundException("Category not found with id: " + request.getCategory().getId()));
    } else if (request.getCategory() != null && request.getCategory().getName() != null) {
      category = categoryRepository.findByName(request.getCategory().getName());
      if (category == null) {
        throw new ResourceNotFoundException("Category not found with name: " + request.getCategory().getName());
      }
    }

    Product product = new Product();
    product.setName(request.getName());
    product.setBrand(request.getBrand());
    product.setPrice(request.getPrice());
    product.setInventory(request.getInventory());
    product.setDescription(request.getDescription());
    product.setCategory(category);

    return productRepository.save(product);
  }

  @Override
  public Product getProductById(Long id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
  }

  @Override
  @Transactional
  public Product updateProduct(Long productId, AddProductRequest request) {
    Product existingProduct = getProductById(productId);

    Category category = existingProduct.getCategory();
    if (request.getCategory() != null) {
      if (request.getCategory().getId() != null) {
        category = categoryRepository.findById(request.getCategory().getId())
            .orElseThrow(
                () -> new ResourceNotFoundException("Category not found with id: " + request.getCategory().getId()));
      } else if (request.getCategory().getName() != null) {
        category = categoryRepository.findByName(request.getCategory().getName());
        if (category == null) {
          throw new ResourceNotFoundException("Category not found with name: " + request.getCategory().getName());
        }
      }
    }

    existingProduct.setName(request.getName());
    existingProduct.setBrand(request.getBrand());
    existingProduct.setPrice(request.getPrice());
    existingProduct.setInventory(request.getInventory());
    existingProduct.setDescription(request.getDescription());
    existingProduct.setCategory(category);

    return productRepository.save(existingProduct);
  }

  @Override
  public Page<Product> getFilteredProducts(String category, String brand, String name, Pageable pageable) {
    Specification<Product> spec = ProductSpecification.filterBy(category, brand, name);
    return productRepository.findAll(spec, pageable);
  }

  @Override
  @Transactional
  public void deleteProductById(Long id) {
    if (!productRepository.existsById(id)) {
      throw new ResourceNotFoundException("Product not found with id: " + id);
    }
    try {

      productRepository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      throw new ResourceNotFoundException("Product not found with id: " + id);
    }

  }
}
