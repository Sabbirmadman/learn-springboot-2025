package com.lelarn.dreamshops.controller;

import com.lelarn.dreamshops.request.AddToCartRequest;
import com.lelarn.dreamshops.response.CartItemResponse;
import com.lelarn.dreamshops.service.cart.ICartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.lelarn.dreamshops.response.ApiResponse;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('CUSTOMER')")
public class CartController {

    private final ICartService cartService;

    // Get current user's username from security context
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }
        return authentication.getName(); 
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CartItemResponse>> addItemToCart(@Valid @RequestBody AddToCartRequest request) {
        try{
            String username = getCurrentUsername();
            CartItemResponse cartItem = cartService.addItemToCart(request, username);
            return ApiResponse.success(HttpStatus.CREATED,"Cart item has been added", cartItem);

        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve categories", e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> getCart() {
        try {
            String username = getCurrentUsername();
            List<CartItemResponse> cartItems = cartService.getUserCart(username);
            return ApiResponse.success("Cart items found", cartItems);
        } catch (EntityNotFoundException e) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, "Cart item not found", e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve cart", e.getMessage());
        }
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItemResponse> updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @RequestBody Map<String, Integer> payload) { // Expecting {"quantity": new_value}
        String username = getCurrentUsername();
        Integer quantity = payload.get("quantity");
        if (quantity == null || quantity <= 0) {
             return ResponseEntity.badRequest().build(); // Or throw specific exception
        }
        CartItemResponse updatedItem = cartService.updateCartItemQuantity(cartItemId, quantity, username);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long cartItemId) {
        String username = getCurrentUsername();
        cartService.removeItemFromCart(cartItemId, username);
        return ResponseEntity.noContent().build();
    }
}
