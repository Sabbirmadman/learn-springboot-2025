package com.lelarn.dreamshops.service.product;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException; // Import this
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lelarn.dreamshops.exceptions.ProductNotFoundException;
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
    if (request.getCategory() != null && request.getCategory().getName() != null) {
      category = categoryRepository.findByName(request.getCategory().getName());
      if (category == null) {
         category = new Category(request.getCategory().getName());
         category = categoryRepository.save(category);
      }
    }

    Product newProduct = new Product();
    newProduct.setName(request.getName());
    newProduct.setBrand(request.getBrand());
    newProduct.setPrice(request.getPrice());
    newProduct.setInventory(request.getInventory());
    newProduct.setDescription(request.getDescription());
    newProduct.setCategory(category);

    return productRepository.save(newProduct);
  }

  @Override
  public Product getProductById(Long id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
  }

  @Override
  @Transactional
  public Product updateProduct(Long productId, AddProductRequest request) {
    Product existingProduct = getProductById(productId);

    existingProduct.setName(request.getName());
    existingProduct.setBrand(request.getBrand());
    existingProduct.setPrice(request.getPrice());
    existingProduct.setInventory(request.getInventory());
    existingProduct.setDescription(request.getDescription());

    if (request.getCategory() != null && request.getCategory().getName() != null) {
      Category category = categoryRepository.findByName(request.getCategory().getName());
      if (category == null) {
         category = new Category(request.getCategory().getName());
         category = categoryRepository.save(category);
      }
      existingProduct.setCategory(category);
    } else {
       existingProduct.setCategory(null); // Allow setting category to null
    }

    return productRepository.save(existingProduct);
  }

    @Override
    public List<Product> getFilteredProducts(
            String category,
            String brand,
            String name
    ){
      Specification<Product> spec = ProductSpecification.filterBy(category, brand, name);
      return productRepository.findAll(spec);
    }

    @Override
    @Transactional
    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        try {
            productRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
             throw new ProductNotFoundException("Product not found with id: " + id);
        }
    }
}
