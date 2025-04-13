package com.lelarn.dreamshops.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lelarn.dreamshops.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

  List<Product> findByCategoryName(String category);

  List<Product> findByBrand(String brand);

  List<Product> findByCategoryNameAndBrand(String category, String brand);

  List<Product> findByName(String name);

  List<Product> findByBrandAndName(String brand, String name);

  long countByBrandAndName(String brand, String name);

}