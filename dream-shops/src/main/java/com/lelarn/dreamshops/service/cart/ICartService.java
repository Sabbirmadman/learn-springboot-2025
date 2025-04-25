package com.lelarn.dreamshops.service.cart;

import java.util.List;

import com.lelarn.dreamshops.request.AddToCartRequest;
import com.lelarn.dreamshops.response.CartItemResponse;

public interface ICartService {
    CartItemResponse addItemToCart(AddToCartRequest request, String username);
    List<CartItemResponse> getUserCart(String username);
    CartItemResponse updateCartItemQuantity(Long cartItemId, int quantity, String username);
    void removeItemFromCart(Long cartItemId, String username);
}


