package com.lelarn.dreamshops.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lelarn.dreamshops.model.Cart;
import com.lelarn.dreamshops.model.User;

public interface CartRepositiry extends JpaRepository<Cart, Long> {

    List<Cart> findByUserAndStatus(User user, String status);

    Optional<Cart> findByUserAndProductAndStatus(User user, com.lelarn.dreamshops.model.Product product, String status);

    Optional<Cart> findByIdAndUser(Long id, User user);
}
