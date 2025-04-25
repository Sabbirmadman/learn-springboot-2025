package com.lelarn.dreamshops.response;

import java.math.BigDecimal;

import com.lelarn.dreamshops.model.Cart;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartItemResponse {
    private long cartItemId;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private int quantity;
    private String status;
    private BigDecimal subtotal; 

    public CartItemResponse(Cart cart) {
        this.cartItemId = cart.getId();
        this.productId = cart.getProduct().getId();
        this.productName = cart.getProduct().getName();
        this.price = cart.getProduct().getPrice();
        this.quantity = cart.getQuantity();
        this.status = cart.getStatus();
        if (this.price != null) {
            this.subtotal = this.price.multiply(BigDecimal.valueOf(this.quantity));
        }
    }

}
