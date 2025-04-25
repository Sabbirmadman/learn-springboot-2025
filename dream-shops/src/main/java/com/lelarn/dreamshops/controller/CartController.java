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
            // This case should ideally be handled by security filters,
            // but throwing an exception here is a safeguard.
            throw new IllegalStateException("User not authenticated");
        }
        return authentication.getName();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CartItemResponse>> addItemToCart(@Valid @RequestBody AddToCartRequest request) {
        try {
            String username = getCurrentUsername();
            CartItemResponse cartItem = cartService.addItemToCart(request, username);
            return ApiResponse.success(HttpStatus.CREATED, "Cart item has been added", cartItem);
        } catch (EntityNotFoundException e) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, "Product not found", e.getMessage());
        } catch (IllegalStateException e) { // Catch unauthenticated user
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Authentication required", e.getMessage());
        } catch (Exception e) {
            // Log the exception e
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add item to cart", e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> getCart() {
        try {
            String username = getCurrentUsername();
            List<CartItemResponse> cartItems = cartService.getUserCart(username);
            if (cartItems.isEmpty()) {
                return ApiResponse.success(HttpStatus.OK, "Cart is empty", cartItems);
            }
            return ApiResponse.success("Cart items retrieved successfully", cartItems); // Changed message
        } catch (EntityNotFoundException e) { // This might mean the user or their cart doesn't exist
            return ApiResponse.error(HttpStatus.NOT_FOUND, "Cart or user not found", e.getMessage());
        } catch (IllegalStateException e) { // Catch unauthenticated user
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Authentication required", e.getMessage());
        } catch (Exception e) {
            // Log the exception e
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve cart", e.getMessage());
        }
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @RequestBody Map<String, Integer> payload) { // Expecting {"quantity": new_value}
        try {
            String username = getCurrentUsername();
            Integer quantity = payload.get("quantity");
            if (quantity == null || quantity <= 0) {
                // Return ApiResponse for bad request
                return ApiResponse.error(HttpStatus.BAD_REQUEST,
                        "Invalid quantity provided. Quantity must be greater than 0.", null);
            }
            CartItemResponse updatedItem = cartService.updateCartItemQuantity(cartItemId, quantity, username);
            return ApiResponse.success("Cart item quantity updated", updatedItem);
        } catch (EntityNotFoundException e) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, "Cart item not found", e.getMessage());
        } catch (IllegalStateException e) { // Catch unauthenticated user
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Authentication required", e.getMessage());
        } catch (IllegalArgumentException e) { // Catch potential errors from service layer validation
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to update quantity", e.getMessage());
        } catch (Exception e) {
            // Log the exception e
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update cart item quantity",
                    e.getMessage());
        }
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> removeItemFromCart(@PathVariable Long cartItemId) {
        try {
            String username = getCurrentUsername();
            cartService.removeItemFromCart(cartItemId, username);
            return ApiResponse.success(HttpStatus.NO_CONTENT, "Cart item removed successfully", null);
        } catch (EntityNotFoundException e) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, "Cart item not found for removal", e.getMessage());
        } catch (IllegalStateException e) { // Catch unauthenticated user
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Authentication required", e.getMessage());
        } catch (Exception e) {
            // Log the exception e
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to remove item from cart",
                    e.getMessage());
        }
    }
}
